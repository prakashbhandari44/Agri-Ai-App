package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeverityModerate
import com.example.ui.viewmodel.FarmViewModel
import com.example.ui.viewmodel.toDomainModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: FarmViewModel,
    onViewScanResult: (AnalysisResult) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scans by viewModel.scanHistory.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredScans = scans.filter { scan ->
        val matchesQuery = searchQuery.isBlank() ||
                scan.cropName.contains(searchQuery, ignoreCase = true) ||
                scan.conditionName.contains(searchQuery, ignoreCase = true) ||
                scan.conditionLocalName.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "DISEASE" -> scan.conditionType == ConditionType.DISEASE.name
            "PEST" -> scan.conditionType == ConditionType.PEST.name
            "NUTRIENT" -> scan.conditionType == ConditionType.NUTRIENT_DEFICIENCY.name
            "HEALTHY" -> scan.conditionType == ConditionType.HEALTHY.name
            else -> true
        }
        matchesQuery && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Column {
                Text(
                    text = localized("nav_history"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = KrishiGreenPrimary
                )
                Text(
                    text = "Track your crop health diagnoses over time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                placeholder = { Text("Search by crop, disease, or pest...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChipItem(
                        title = "All Scans",
                        isSelected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" }
                    )
                }
                item {
                    FilterChipItem(
                        title = "Diseases",
                        isSelected = selectedFilter == "DISEASE",
                        onClick = { selectedFilter = "DISEASE" }
                    )
                }
                item {
                    FilterChipItem(
                        title = "Pests",
                        isSelected = selectedFilter == "PEST",
                        onClick = { selectedFilter = "PEST" }
                    )
                }
                item {
                    FilterChipItem(
                        title = "Nutrient Issues",
                        isSelected = selectedFilter == "NUTRIENT",
                        onClick = { selectedFilter = "NUTRIENT" }
                    )
                }
                item {
                    FilterChipItem(
                        title = "Healthy",
                        isSelected = selectedFilter == "HEALTHY",
                        onClick = { selectedFilter = "HEALTHY" }
                    )
                }
            }
        }

        // Scans Content
        if (filteredScans.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No records match '$searchQuery'" else localized("no_scans_yet"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Save your crop scans after photo diagnosis to build your farm record history.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateToScan,
                            colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localized("scan_crop"))
                        }
                    }
                }
            }
        } else {
            items(filteredScans, key = { it.id }) { scanEntity ->
                val domainModel = scanEntity.toDomainModel()
                val formattedDate = remember(scanEntity.timestamp) {
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    sdf.format(Date(scanEntity.timestamp))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewScanResult(domainModel) }
                        .testTag("history_item_${scanEntity.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KrishiGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (scanEntity.cropName.lowercase()) {
                                    "tomato" -> "🍅"
                                    "rice" -> "🌾"
                                    "potato" -> "🥔"
                                    "maize" -> "🌽"
                                    "wheat" -> "🌱"
                                    "chilli" -> "🌶️"
                                    "cabbage" -> "🥬"
                                    "apple" -> "🍎"
                                    else -> "🌿"
                                },
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = scanEntity.cropName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (scanEntity.severity) {
                                        Severity.HIGH.name -> Color(0xFFFFEBEE)
                                        Severity.MODERATE.name -> Color(0xFFFFF3E0)
                                        else -> Color(0xFFE8F5E9)
                                    }
                                ) {
                                    Text(
                                        text = scanEntity.severity,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (scanEntity.severity) {
                                            Severity.HIGH.name -> SeverityHigh
                                            Severity.MODERATE.name -> SeverityModerate
                                            else -> HealthGreen
                                        },
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = scanEntity.conditionName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (scanEntity.conditionLocalName.isNotBlank()) {
                                Text(
                                    text = scanEntity.conditionLocalName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KrishiGreenPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$formattedDate • AI Conf: ${scanEntity.confidence}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        IconButton(onClick = { viewModel.deleteScan(scanEntity.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FilterChipItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) KrishiGreenPrimary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
