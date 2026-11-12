package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.service.AppLanguage
import com.example.data.service.SubscriptionTier
import com.example.data.service.localized
import com.example.ui.theme.KrishiGold
import com.example.ui.theme.KrishiGoldContainer
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.viewmodel.FarmViewModel

@Composable
fun ProfileScreen(
    viewModel: FarmViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToAboutDeveloper: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val scans by viewModel.scanHistory.collectAsStateWithLifecycle()
    val farms by viewModel.farms.collectAsStateWithLifecycle()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showEthicsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var currentTier by remember { mutableStateOf(SubscriptionTier.FREE) }

    var weatherNotifEnabled by remember { mutableStateOf(true) }
    var diseaseAlertsEnabled by remember { mutableStateOf(true) }
    var farmRemindersEnabled by remember { mutableStateOf(true) }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(text = localized("language"), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.entries.forEach { lang ->
                        Surface(
                            onClick = {
                                viewModel.setLanguage(lang)
                                showLanguageDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${lang.displayName} (${lang.localName})", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showEthicsDialog) {
        AlertDialog(
            onDismissRequest = { showEthicsDialog = false },
            title = { Text(text = "AI Safety & Agricultural Ethics", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = """Krishi AI Core Principles:
1. Assistance Tool: AI predictions are decision-support aids, never absolute medical certainty.
2. Verified Recommendations: Dangerous chemical mixtures or unapproved pesticides are strictly disallowed. Chemical interventions must comply with locally registered pesticide labels and national directives.
3. Extension Partnership: When symptoms are severe or confidence is uncertain, we direct farmers to verified local agricultural officers.
4. Model Privacy: Uploaded crop images are used solely for agronomic diagnostic enhancement.""",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(onClick = { showEthicsDialog = false }) { Text("Understand") }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(text = "Privacy & Data Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = """Data Collection & Transparency:
- Collected: Crop photos, farm names, general district location.
- Purpose: Image analysis for pest/disease recognition and regional disease forecasting.
- Security: Data is encrypted and private farm coordinates are never disclosed publicly.
- You can clear your diagnosis history anytime from the History tab.""",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) { Text("Close") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Farmer Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(KrishiGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👨‍🌾", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${userProfile.district}, ${userProfile.province}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Farming: ${userProfile.farmingType}",
                                style = MaterialTheme.typography.labelSmall,
                                color = KrishiGreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatisticItem(label = "Farms", value = farms.size.toString())
                        StatisticItem(label = "AI Scans", value = scans.size.toString())
                        StatisticItem(label = "Crops", value = userProfile.mainCrops.size.toString())
                    }
                }
            }
        }

        // Monetization & Subscription Plan Card (Section 44)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KrishiGoldContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentTier.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF795548)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF57F17)
                        ) {
                            Text(
                                text = currentTier.priceNpr,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Current allowance: ${currentTier.scanLimit} • Local weather alerts included",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5D4037)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            currentTier = if (currentTier == SubscriptionTier.FREE) SubscriptionTier.PRO else SubscriptionTier.FREE
                            Toast.makeText(context, "Plan updated to ${currentTier.title}!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (currentTier == SubscriptionTier.FREE) "Upgrade to Krishi PRO" else "Switch to Free Tier")
                    }
                }
            }
        }

        // Language & Localization Setting
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = KrishiGreenPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = localized("language"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Current: ${userProfile.preferredLanguage}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                }
            }
        }

        // Notification Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Notification Preferences", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    NotificationToggleRow(
                        title = "Disease & Fungal Weather Alerts",
                        checked = diseaseAlertsEnabled,
                        onCheckedChange = { diseaseAlertsEnabled = it }
                    )
                    NotificationToggleRow(
                        title = "Daily Agrometeorology Advisory",
                        checked = weatherNotifEnabled,
                        onCheckedChange = { weatherNotifEnabled = it }
                    )
                    NotificationToggleRow(
                        title = "Farm Care Reminders",
                        checked = farmRemindersEnabled,
                        onCheckedChange = { farmRemindersEnabled = it }
                    )
                }
            }
        }

        // AI Safety & Legal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Trust, Ethics & Privacy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEthicsDialog = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = localized("ai_disclaimer_title"), style = MaterialTheme.typography.bodyMedium)
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = localized("privacy_policy"), style = MaterialTheme.typography.bodyMedium)
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToAboutDeveloper() }
                            .padding(vertical = 8.dp)
                            .testTag("about_developer_menu_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👨‍💻", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = localized("about_developer"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = KrishiGreenPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Featured About Developer Card with Prakash Bhandari's Photo
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAboutDeveloper() }
                    .testTag("about_developer_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_developer_profile),
                            contentDescription = "Prakash Bhandari - Developer Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Prakash Bhandari",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = KrishiGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Founder & Developer",
                            style = MaterialTheme.typography.bodySmall,
                            color = KrishiGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Krishi AI (कृषि साथी)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Developer Profile",
                        tint = KrishiGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Account Switch / Logout
        item {
            OutlinedButton(
                onClick = onNavigateToAuth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(localized("logout"))
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun StatisticItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = KrishiGreenPrimary)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = KrishiGreenPrimary, checkedTrackColor = KrishiGreenContainer)
        )
    }
}
