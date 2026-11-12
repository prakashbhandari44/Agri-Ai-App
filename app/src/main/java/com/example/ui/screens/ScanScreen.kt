package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnalysisResult
import com.example.data.model.Crop
import com.example.data.repository.CropRepository
import com.example.data.service.localized
import com.example.ui.theme.KrishiGold
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.theme.KrishiGreenSecondary
import com.example.ui.theme.SeverityModerate
import com.example.ui.viewmodel.FarmViewModel
import com.example.ui.viewmodel.ScanUiState

@Composable
fun ScanScreen(
    viewModel: FarmViewModel,
    onAnalysisComplete: (AnalysisResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scanState by viewModel.scanState.collectAsStateWithLifecycle()
    val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedUriString by remember { mutableStateOf<String?>(null) }
    var showQualityWarningDialog by remember { mutableStateOf(false) }
    var warningMessageText by remember { mutableStateOf("") }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            capturedUriString = null
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        }
    }

    // Photo picker launcher (Gallery)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        capturedBitmap = bmp
                        capturedUriString = uri.toString()
                    }
                }
            } catch (e: Exception) {
                // handle error gracefully
            }
        }
    }

    // React to ScanUiState changes
    LaunchedEffect(scanState) {
        when (val state = scanState) {
            is ScanUiState.Success -> {
                onAnalysisComplete(state.result)
            }
            is ScanUiState.QualityWarning -> {
                warningMessageText = state.validation.warningMessage ?: "Image quality warning."
                showQualityWarningDialog = true
            }
            else -> {}
        }
    }

    // Quality Warning Dialog
    if (showQualityWarningDialog && capturedBitmap != null) {
        AlertDialog(
            onDismissRequest = { showQualityWarningDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = SeverityModerate,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Image Quality Notice",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "$warningMessageText\n\nAI analysis may be less accurate on unclear photos. You can retake the photo or proceed anyway.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showQualityWarningDialog = false
                        capturedBitmap?.let { bmp ->
                            viewModel.analyzeImage(bmp, capturedUriString, bypassWarning = true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
                ) {
                    Text("Use Anyway")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showQualityWarningDialog = false
                        viewModel.setScanStateIdle()
                    }
                ) {
                    Text("Retake Photo")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("scan_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Title & Instructions
        item {
            Column {
                Text(
                    text = localized("scan_crop"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = KrishiGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select your crop, capture the affected leaf or fruit, and receive an instant AI diagnosis.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Crop Selection Carousel
        item {
            CropSelectorSection(
                selectedCrop = selectedCrop,
                onCropSelect = { viewModel.setSelectedCrop(it) }
            )
        }

        // 3. Image Capture & Preview Box
        item {
            ImageCaptureCard(
                capturedBitmap = capturedBitmap,
                onTakePhoto = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onPickGallery = {
                    galleryLauncher.launch("image/*")
                },
                onSelectSample = { sampleName ->
                    val sample = generateSampleLeafBitmap(sampleName)
                    capturedBitmap = sample
                    capturedUriString = "sample://$sampleName"
                },
                onClear = {
                    capturedBitmap = null
                    capturedUriString = null
                    viewModel.setScanStateIdle()
                }
            )
        }

        // 4. Analysis Trigger or Animated Progress
        item {
            when (val state = scanState) {
                is ScanUiState.Validating -> {
                    AnalysisLoadingCard(step = state.message, progress = 0.35f)
                }
                is ScanUiState.Analyzing -> {
                    AnalysisLoadingCard(step = state.step, progress = 0.75f)
                }
                is ScanUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Analysis Encountered an Issue",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    capturedBitmap?.let { bmp ->
                                        viewModel.analyzeImage(bmp, capturedUriString, bypassWarning = true)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
                            ) {
                                Text("Retry Analysis")
                            }
                        }
                    }
                }
                else -> {
                    Button(
                        onClick = {
                            capturedBitmap?.let { bmp ->
                                viewModel.analyzeImage(bmp, capturedUriString)
                            }
                        },
                        enabled = capturedBitmap != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("analyze_crop_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KrishiGreenPrimary,
                            disabledContainerColor = Color(0xFFC8E6C9)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (capturedBitmap != null) "Analyze Crop with AI" else "Capture or Select Photo to Analyze",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 5. Photography Best Practices Guide
        item {
            PhotographyGuideSection()
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun CropSelectorSection(
    selectedCrop: Crop?,
    onCropSelect: (Crop) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Target Crop (बाली छान्नुहोस्)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedCrop?.localNameNepali ?: "Select",
                style = MaterialTheme.typography.bodySmall,
                color = KrishiGreenPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CropRepository.initialCrops) { crop ->
                val isSelected = selectedCrop?.id == crop.id
                Surface(
                    onClick = { onCropSelect(crop) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) KrishiGreenPrimary else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.height(44.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = crop.iconEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = crop.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageCaptureCard(
    capturedBitmap: Bitmap?,
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit,
    onSelectSample: (String) -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("image_capture_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (capturedBitmap != null) {
                // Show Image Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = capturedBitmap.asImageBitmap(),
                        contentDescription = "Crop Sample Preview",
                        modifier = Modifier.fillMaxSize()
                    )

                    // Retake / Clear button overlay
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retake",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓ Image ready for AI diagnosis",
                        style = MaterialTheme.typography.bodySmall,
                        color = KrishiGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(
                        onClick = onClear,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Retake Photo")
                    }
                }
            } else {
                // Photo capture prompts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(KrishiGreenLight)
                        .border(1.5.dp, KrishiGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(KrishiGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = KrishiGreenPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Photograph Diseased Leaf / Fruit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KrishiGreenPrimary
                        )
                        Text(
                            text = "Take a high-clarity photo in natural daylight",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onTakePhoto,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("take_photo_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(localized("take_photo"))
                    }

                    OutlinedButton(
                        onClick = onPickGallery,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("choose_gallery_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(localized("choose_gallery"))
                    }
                }

                // Quick One-Touch Demo Samples (Crucial for emulators and quick verification)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Or tap a sample diseased leaf to test instantly:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SampleChip(name = "Early Blight Tomato", emoji = "🍅", onClick = { onSelectSample("tomato") })
                    SampleChip(name = "Bacterial Blight Rice", emoji = "🌾", onClick = { onSelectSample("rice") })
                    SampleChip(name = "Late Blight Potato", emoji = "🥔", onClick = { onSelectSample("potato") })
                }
            }
        }
    }
}

@Composable
private fun SampleChip(
    name: String,
    emoji: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AnalysisLoadingCard(
    step: String,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KrishiGreenLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = KrishiGreenPrimary,
                modifier = Modifier.size(44.dp),
                strokeWidth = 3.5.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = localized("analyzing_crop"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KrishiGreenPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = KrishiGreenPrimary,
                trackColor = KrishiGreenContainer
            )
        }
    }
}

@Composable
private fun PhotographyGuideSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = null,
                    tint = KrishiGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Photography Tips for Accurate AI Diagnosis",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            GuideBullet(text = "Take photo in bright daylight; avoid harsh flash reflections.")
            GuideBullet(text = "Hold camera 10–20 cm away and tap screen to focus on lesions.")
            GuideBullet(text = "Include both healthy and symptomatic leaf margins for contrast.")
            GuideBullet(text = "Avoid photographing wet leaves directly after rain.")
        }
    }
}

@Composable
private fun GuideBullet(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "•", color = KrishiGreenPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Creates an illustrative test leaf bitmap with authentic botanical pigment
 * patterns (concentric spots, halos, bacterial lesions) for instant testing.
 */
private fun generateSampleLeafBitmap(sampleType: String): Bitmap {
    val width = 480
    val height = 480
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Base background (leaf blade color)
    val leafPaint = Paint().apply {
        color = when (sampleType) {
            "tomato" -> AndroidColor.rgb(65, 125, 45) // tomato foliage green
            "rice" -> AndroidColor.rgb(130, 160, 40) // rice leaf yellow-green
            else -> AndroidColor.rgb(50, 110, 40)
        }
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), leafPaint)

    // Leaf main vein line
    val veinPaint = Paint().apply {
        color = AndroidColor.rgb(160, 190, 80)
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }
    canvas.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), veinPaint)

    // Disease spots
    val spotPaint = Paint().apply {
        color = when (sampleType) {
            "tomato" -> AndroidColor.rgb(55, 30, 15) // dark brown Early Blight target board
            "rice" -> AndroidColor.rgb(195, 175, 110) // bleached necrotic bacterial lesion
            else -> AndroidColor.rgb(40, 20, 10)
        }
        style = Paint.Style.FILL
    }
    val haloPaint = Paint().apply {
        color = AndroidColor.rgb(215, 200, 50) // chlorotic yellow halo
        style = Paint.Style.STROKE
        strokeWidth = 12f
    }

    // Draw realistic concentric disease lesions
    canvas.drawCircle(width * 0.45f, height * 0.42f, 55f, haloPaint)
    canvas.drawCircle(width * 0.45f, height * 0.42f, 45f, spotPaint)

    canvas.drawCircle(width * 0.65f, height * 0.68f, 38f, haloPaint)
    canvas.drawCircle(width * 0.65f, height * 0.68f, 30f, spotPaint)

    return bitmap
}
