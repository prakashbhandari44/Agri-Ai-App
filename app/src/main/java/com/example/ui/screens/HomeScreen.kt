package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.service.localized
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.KrishiGold
import com.example.ui.theme.KrishiGoldContainer
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.theme.KrishiGreenSecondary
import com.example.ui.theme.KrishiSoilBrown
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityLow
import com.example.ui.theme.SeverityModerate
import com.example.ui.viewmodel.FarmViewModel
import com.example.ui.viewmodel.toDomainModel

@Composable
fun HomeScreen(
    viewModel: FarmViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToMyFarm: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToCropCalendar: () -> Unit,
    onNavigateToExpertHelp: () -> Unit,
    onNavigateToAIChat: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onViewScanResult: (com.example.data.model.AnalysisResult) -> Unit,
    onNavigateToAboutDeveloper: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scans by viewModel.scanHistory.collectAsStateWithLifecycle()
    val farms by viewModel.farms.collectAsStateWithLifecycle()
    val fields by viewModel.fields.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top Greeting Header
        item {
            HeaderSection(
                userName = userProfile.name,
                district = userProfile.district,
                onAIChatClick = onNavigateToAIChat
            )
        }

        // 2. Weather & Agrometeorology Alert Banner
        item {
            WeatherSummaryCard(
                weather = weather,
                onClick = onNavigateToWeather
            )
        }

        // 3. Primary Scan Call-To-Action Card
        item {
            ScanHeroCard(
                onScanClick = onNavigateToScan
            )
        }

        // 4. Quick Action Grid / Chips
        item {
            QuickActionsSection(
                onMyFarmClick = onNavigateToMyFarm,
                onWeatherClick = onNavigateToWeather,
                onCalendarClick = onNavigateToCropCalendar,
                onExpertClick = onNavigateToExpertHelp,
                onAIChatClick = onNavigateToAIChat,
                onKnowledgeClick = onNavigateToKnowledge,
                onAboutDeveloperClick = onNavigateToAboutDeveloper
            )
        }

        // 5. Recent Scans Section
        item {
            SectionHeader(
                title = localized("recent_scans"),
                actionText = if (scans.isNotEmpty()) localized("view_all") else null,
                onActionClick = onNavigateToScan
            )
        }

        if (scans.isEmpty()) {
            item {
                EmptyScansCard(onScanClick = onNavigateToScan)
            }
        } else {
            items(scans.take(3)) { entity ->
                val domainModel = entity.toDomainModel()
                RecentScanCard(
                    scan = domainModel,
                    onClick = { onViewScanResult(domainModel) }
                )
            }
        }

        // 6. Farm Status Summary
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = localized("my_farm"),
                actionText = localized("view_all"),
                onActionClick = onNavigateToMyFarm
            )
        }

        item {
            FarmSummaryWidget(
                farmsCount = farms.size,
                fieldsCount = fields.size,
                fields = fields,
                onClick = onNavigateToMyFarm
            )
        }

        // 7. Farm Reminders List
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = localized("reminders"),
                actionText = "+ " + localized("add_reminder"),
                onActionClick = onNavigateToMyFarm
            )
        }

        if (reminders.isEmpty()) {
            item {
                Text(
                    text = "No active farm reminders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(reminders.take(3)) { reminder ->
                ReminderItem(
                    reminder = reminder,
                    onToggle = { viewModel.toggleReminder(reminder) }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    userName: String,
    district: String,
    onAIChatClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        KrishiGreenPrimary,
                        KrishiGreenSecondary
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🌾",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "${localized("greeting_prefix")}, $userName",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color(0xFFC8E6C9),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$district, Nepal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFC8E6C9)
                    )
                }
            }

            // Quick AI Chat Button
            IconButton(
                onClick = onAIChatClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .testTag("header_ai_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Sathi Chat",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun WeatherSummaryCard(
    weather: com.example.data.model.WeatherInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick)
            .testTag("weather_summary_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KrishiGreenLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = KrishiGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${weather.temperatureC}°C • ${weather.condition}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KrishiGreenPrimary
                        )
                        Text(
                            text = "Humidity: ${weather.humidityPercent}% • Rain: ${weather.rainProbabilityPercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Details",
                    tint = KrishiGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = SeverityModerate,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = weather.diseaseFavorableRisk,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ScanHeroCard(
    onScanClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("scan_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = KrishiGoldContainer,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "✨ AI Crop Doctor",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB78103),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = localized("scan_crop"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = localized("scan_crop_desc"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(KrishiGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Scan",
                        tint = KrishiGreenPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("home_scan_button"),
                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localized("scan_crop"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onMyFarmClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onExpertClick: () -> Unit,
    onAIChatClick: () -> Unit,
    onKnowledgeClick: () -> Unit,
    onAboutDeveloperClick: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuickActionChip(
                    icon = Icons.Default.Grass,
                    label = localized("my_farm"),
                    containerColor = Color(0xFFE8F5E9),
                    iconTint = KrishiGreenPrimary,
                    onClick = onMyFarmClick
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.Psychology,
                    label = localized("ai_chat"),
                    containerColor = Color(0xFFEDE7F6),
                    iconTint = Color(0xFF5E35B1),
                    onClick = onAIChatClick
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.CalendarMonth,
                    label = localized("crop_calendar"),
                    containerColor = Color(0xFFFFF8E1),
                    iconTint = Color(0xFFF57F17),
                    onClick = onCalendarClick
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.SupportAgent,
                    label = localized("expert_help"),
                    containerColor = Color(0xFFE0F7FA),
                    iconTint = Color(0xFF00838F),
                    onClick = onExpertClick
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.MenuBook,
                    label = localized("knowledge_center"),
                    containerColor = Color(0xFFFBE9E7),
                    iconTint = Color(0xFFD84315),
                    onClick = onKnowledgeClick
                )
            }
            item {
                QuickActionChip(
                    icon = Icons.Default.Person,
                    label = "About Dev",
                    containerColor = Color(0xFFE8F5E9),
                    iconTint = KrishiGreenPrimary,
                    onClick = onAboutDeveloperClick
                )
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (actionText != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                color = KrishiGreenPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
private fun EmptyScansCard(onScanClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌱",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = localized("no_scans_yet"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onScanClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(localized("scan_crop"))
            }
        }
    }
}

@Composable
private fun RecentScanCard(
    scan: com.example.data.model.AnalysisResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
            .testTag("recent_scan_${scan.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KrishiGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌿", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = scan.cropName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (scan.severity) {
                            com.example.data.model.Severity.HIGH -> Color(0xFFFFEBEE)
                            com.example.data.model.Severity.MODERATE -> Color(0xFFFFF3E0)
                            else -> Color(0xFFE8F5E9)
                        }
                    ) {
                        Text(
                            text = scan.severity.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (scan.severity) {
                                com.example.data.model.Severity.HIGH -> SeverityHigh
                                com.example.data.model.Severity.MODERATE -> SeverityModerate
                                else -> HealthGreen
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = scan.condition.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "AI Confidence: ${scan.condition.confidence}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FarmSummaryWidget(
    farmsCount: Int,
    fieldsCount: Int,
    fields: List<com.example.data.local.entity.FieldEntity>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("farm_summary_widget"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Farm Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$farmsCount Farms • $fieldsCount Fields",
                    style = MaterialTheme.typography.bodySmall,
                    color = KrishiGreenPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            fields.take(2).forEach { field ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌱", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${field.fieldName} (${field.cropName})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = KrishiGreenContainer
                    ) {
                        Text(
                            text = field.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = KrishiGreenPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderItem(
    reminder: com.example.data.local.entity.ReminderEntity,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) Color(0xFFF1F3F0) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = KrishiGreenPrimary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (reminder.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${reminder.crop} • ${reminder.date} at ${reminder.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
