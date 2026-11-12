package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AnalysisResult
import com.example.data.model.ConditionType
import com.example.data.model.Severity
import com.example.data.service.localized
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.theme.KrishiGreenSecondary
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityModerate
import com.example.ui.viewmodel.FarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    result: AnalysisResult,
    viewModel: FarmViewModel,
    onBack: () -> Unit,
    onConsultExpert: () -> Unit,
    onScanAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val farms by viewModel.farms.collectAsStateWithLifecycle()
    var isSaved by remember { mutableStateOf(false) }

    var selectedFarmId by remember { mutableStateOf<Long?>(farms.firstOrNull()?.id) }
    var notesText by remember { mutableStateOf("") }
    var showFarmSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = localized("ai_estimated_result"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Krishi AI Diagnosis Report:\nCrop: ${result.cropName}\nCondition: ${result.condition.name}\nAI Confidence: ${result.condition.confidence}%\nSeverity: ${result.severity.displayName}\n\nSymptoms:\n${result.symptoms.joinToString("\n• ")}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Diagnosis"))
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KrishiGreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("scan_result_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Mandatory AI Disclaimer Warning Banner
            item {
                DisclaimerBanner(notice = result.uncertaintyNotice)
            }

            // 2. Primary Crop & Condition Diagnosis Card
            item {
                DiagnosisHeaderCard(result = result)
            }

            // 3. Low Confidence Warning (if confidence < 65% or Unknown)
            if (result.condition.confidence < 65 || result.condition.type == ConditionType.UNKNOWN) {
                item {
                    LowConfidenceNoticeCard(
                        onScanAnother = onScanAnother,
                        onConsultExpert = onConsultExpert
                    )
                }
            }

            // 4. Identified Symptoms
            item {
                ResultSectionCard(
                    title = localized("symptoms"),
                    icon = "🔍",
                    items = result.symptoms
                )
            }

            // 5. Possible Causes
            item {
                ResultSectionCard(
                    title = localized("possible_causes"),
                    icon = "🔬",
                    items = result.possibleCauses
                )
            }

            // 6. General & Safe Management
            item {
                ResultSectionCard(
                    title = localized("general_management"),
                    icon = "🛡️",
                    items = result.generalManagement,
                    containerColor = Color(0xFFF1F8E9)
                )
            }

            // 7. Prevention Protocols
            item {
                ResultSectionCard(
                    title = localized("prevention"),
                    icon = "🌱",
                    items = result.prevention
                )
            }

            // 8. Continuous Monitoring Advice
            item {
                ResultSectionCard(
                    title = localized("monitoring"),
                    icon = "📋",
                    items = result.monitoringAdvice
                )
            }

            // 9. When to contact local Agriculture Extension Officer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = localized("when_to_contact_expert"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF795548)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.whenToContactExpert,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 10. AI Model Feedback & Improvement (Section 45)
            item {
                DiagnosisFeedbackCard()
            }

            // 11. Save to Farm & Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            viewModel.saveDiagnosis(result, selectedFarmId, notesText)
                            isSaved = true
                            Toast.makeText(context, localized("saved_to_history"), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_diagnosis_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) KrishiGreenSecondary else KrishiGreenPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSaved) "Saved in History ✓" else localized("save_result"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onConsultExpert,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localized("ask_expert"))
                        }

                        OutlinedButton(
                            onClick = onScanAnother,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localized("scan_another"))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun DisclaimerBanner(notice: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3E0),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCC80))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = notice,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFBF360C),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DiagnosisHeaderCard(result: AnalysisResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Crop and scientific name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = result.cropName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = KrishiGreenPrimary
                    )
                    if (result.cropScientificName.isNotBlank()) {
                        Text(
                            text = result.cropScientificName,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Condition Type Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KrishiGreenContainer
                ) {
                    Text(
                        text = result.condition.type.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = KrishiGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Condition Name
            Text(
                text = result.condition.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (result.condition.localName.isNotBlank()) {
                Text(
                    text = result.condition.localName,
                    style = MaterialTheme.typography.titleSmall,
                    color = KrishiGreenSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Confidence and Severity metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricChip(
                    label = localized("confidence"),
                    value = "${result.condition.confidence}% (Estimated)",
                    color = KrishiGreenPrimary,
                    containerColor = KrishiGreenLight,
                    modifier = Modifier.weight(1f)
                )

                MetricChip(
                    label = localized("severity"),
                    value = result.severity.displayName,
                    color = when (result.severity) {
                        Severity.HIGH -> SeverityHigh
                        Severity.MODERATE -> SeverityModerate
                        else -> HealthGreen
                    },
                    containerColor = when (result.severity) {
                        Severity.HIGH -> Color(0xFFFFEBEE)
                        Severity.MODERATE -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun LowConfidenceNoticeCard(
    onScanAnother: () -> Unit,
    onConsultExpert: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = SeverityHigh,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Could Not Confidently Identify Problem",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SeverityHigh
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "The leaf symptoms do not clearly match a single known pathogen pattern with high certainty. Please do not apply heavy chemicals without technician confirmation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onScanAnother) {
                    Text("Try Another Angle")
                }
                Button(
                    onClick = onConsultExpert,
                    colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
                ) {
                    Text("Ask Agronomist")
                }
            }
        }
    }
}

@Composable
private fun ResultSectionCard(
    title: String,
    icon: String,
    items: List<String>,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    if (items.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(KrishiGreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DiagnosisFeedbackCard() {
    var feedbackState by remember { mutableStateOf<Boolean?>(null) }
    var selectedReason by remember { mutableStateOf<String?>(null) }
    var feedbackSubmitted by remember { mutableStateOf(false) }

    val errorOptions = listOf(
        "Wrong crop",
        "Wrong disease",
        "Wrong pest",
        "Wrong deficiency",
        "Image problem",
        "Other"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Was this AI diagnosis helpful?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (feedbackSubmitted) {
                Text(
                    text = "✓ Thank you! Your feedback helps train and validate our agro-pathology models.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KrishiGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            } else if (feedbackState == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            feedbackState = true
                            feedbackSubmitted = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("👍 Yes, Helpful")
                    }
                    OutlinedButton(
                        onClick = { feedbackState = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("👎 No, Problematic")
                    }
                }
            } else if (feedbackState == false) {
                Text(
                    text = "What went wrong? (Select reason):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    errorOptions.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowOptions.forEach { option ->
                                Surface(
                                    onClick = {
                                        selectedReason = option
                                        feedbackSubmitted = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedReason == option) KrishiGreenPrimary else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        color = if (selectedReason == option) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
