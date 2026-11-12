package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.CropRepository
import com.example.data.service.AppLanguage
import com.example.data.service.LocalizationManager
import com.example.data.service.localized
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.viewmodel.FarmViewModel

@Composable
fun OnboardingScreen(
    viewModel: FarmViewModel,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedLanguage by remember { mutableStateOf(AppLanguage.NEPALI) }
    var selectedDistrict by remember { mutableStateOf("Chitwan") }

    val slides = listOf(
        Triple(
            "🌾",
            "Welcome to Krishi AI",
            "Your intelligent digital farming assistant designed for farmers in Nepal and South Asia."
        ),
        Triple(
            "📸",
            "Scan Your Crops",
            "Photograph diseased leaves, fruits, or plants for instant, AI-assisted agricultural diagnostics."
        ),
        Triple(
            "🔬",
            "Understand Crop Problems",
            "Identify fungal diseases, viral leaf curls, insect pests, and micro-nutrient deficiencies early."
        ),
        Triple(
            "🌦️",
            "Get Farming Guidance",
            "Receive local weather disease-risk warnings, integrated pest remedies, and growth reminders."
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (step < 4) {
                TextButton(onClick = onFinishOnboarding) {
                    Text("Skip", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Center Content
        if (step < 4) {
            val (emoji, title, desc) = slides[step]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(KrishiGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 56.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = KrishiGreenPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        } else {
            // Setup Profile / Language & District Step
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personalize Krishi AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = KrishiGreenPrimary
                )
                Text(
                    text = "Select your preferred language and farming district in Nepal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Language Selection
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        Surface(
                            onClick = {
                                selectedLanguage = lang
                                viewModel.setLanguage(lang)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedLanguage == lang) KrishiGreenPrimary else MaterialTheme.colorScheme.surface,
                            border = if (selectedLanguage == lang) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = lang.localName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedLanguage == lang) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // District Selection
                Text(
                    text = "Select Your District",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val sampleDistricts = listOf("Chitwan", "Jhapa", "Kavre", "Rupandehi", "Kaski", "Kailali")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sampleDistricts.chunked(3).forEach { rowDistricts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowDistricts.forEach { dist ->
                                Surface(
                                    onClick = { selectedDistrict = dist },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedDistrict == dist) KrishiGreenLight else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (selectedDistrict == dist) 1.5.dp else 1.dp,
                                        if (selectedDistrict == dist) KrishiGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = dist,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (selectedDistrict == dist) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedDistrict == dist) KrishiGreenPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicator dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (step == index) 18.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (step == index) KrishiGreenPrimary else Color.LightGray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (step < 4) {
                        step++
                    } else {
                        viewModel.updateProfile(
                            name = "Bhandari Farmer",
                            district = selectedDistrict,
                            province = "Bagmati Province",
                            farmingType = "Semi-Commercial",
                            mainCrops = listOf("Rice", "Tomato", "Maize")
                        )
                        onFinishOnboarding()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
            ) {
                Text(
                    text = if (step < 4) "Continue" else "Get Started With Krishi AI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}
