package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.service.localized
import com.example.ui.theme.KrishiGoldContainer
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary

data class CalendarEntry(
    val cropName: String,
    val nepaliName: String,
    val iconEmoji: String,
    val sowingSeason: String,
    val harvestSeason: String,
    val keyInterventions: String,
    val zone: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropCalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedZone by remember { mutableStateOf("Terai (तराई)") }

    val zones = listOf("Terai (तराई)", "Mid-Hills (पहाड)", "High Mountain (हिमाल)")

    val entries = listOf(
        CalendarEntry(
            cropName = "Paddy / Rice",
            nepaliName = "धान",
            iconEmoji = "🌾",
            sowingSeason = "Jestha - Asar (May - July)",
            harvestSeason = "Kartik - Mangsir (Oct - Nov)",
            keyInterventions = "Nursery preparation, 25-day transplanting, split urea top dressing, alternate wetting & drying.",
            zone = "Terai (तराई)"
        ),
        CalendarEntry(
            cropName = "Winter Wheat",
            nepaliName = "गहुँ",
            iconEmoji = "🌱",
            sowingSeason = "Kartik - Mangsir (Nov - Dec)",
            harvestSeason = "Chaitra - Baisakh (March - April)",
            keyInterventions = "Crown root initiation irrigation at 21 days, weed control, rust monitoring.",
            zone = "Terai (तराई)"
        ),
        CalendarEntry(
            cropName = "Mustard",
            nepaliName = "तोरी",
            iconEmoji = "🌼",
            sowingSeason = "Asoj - Kartik (Oct - Nov)",
            harvestSeason = "Magh - Falgun (Jan - Feb)",
            keyInterventions = "Sulfur fertilization, aphid monitoring during flowering, pre-harvest drying.",
            zone = "Terai (तराई)"
        ),
        CalendarEntry(
            cropName = "Maize",
            nepaliName = "मकै",
            iconEmoji = "🌽",
            sowingSeason = "Falgun - Chaitra (Feb - April)",
            harvestSeason = "Shrawan - Bhadra (July - August)",
            keyInterventions = "Earthing up, whorl monitoring for Fall Armyworm, push-pull companion planting.",
            zone = "Mid-Hills (पहाड)"
        ),
        CalendarEntry(
            cropName = "Tomato (Tunnels)",
            nepaliName = "गोलभेंडा",
            iconEmoji = "🍅",
            sowingSeason = "Bhadra - Asoj & Magh - Falgun",
            harvestSeason = "Continuous picking over 90-120 days",
            keyInterventions = "Trellis staking, side shoot pruning, late blight preventive copper sprays.",
            zone = "Mid-Hills (पहाड)"
        ),
        CalendarEntry(
            cropName = "Summer Potato",
            nepaliName = "बर्खे आलु",
            iconEmoji = "🥔",
            sowingSeason = "Falgun - Chaitra (March - April)",
            harvestSeason = "Bhadra - Asoj (Aug - Sept)",
            keyInterventions = "Ridge earthing up, certified seed tubers, frost protection, blight scouting.",
            zone = "High Mountain (हिमाल)"
        ),
        CalendarEntry(
            cropName = "Apple Orchard",
            nepaliName = "स्याउ",
            iconEmoji = "🍎",
            sowingSeason = "Winter dormancy pruning (Poush - Magh)",
            harvestSeason = "Bhadra - Asoj (August - September)",
            keyInterventions = "Bordeaux paste application on trunk, bee pollination hives, scab monitoring.",
            zone = "High Mountain (हिमाल)"
        )
    )

    val filteredEntries = entries.filter { it.zone == selectedZone }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = localized("crop_calendar"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KrishiGreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("crop_calendar_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Regional Agro-Ecological Calendar (Nepal)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = KrishiGreenPrimary
                )
                Text(
                    text = "Farming seasons vary significantly between the Terai plains, Mid-Hills, and High Himalayan valleys.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Zone Selector Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(zones) { zone ->
                        val isSelected = selectedZone == zone
                        Surface(
                            onClick = { selectedZone = zone },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) KrishiGreenPrimary else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = zone,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Calendar Cards
            items(filteredEntries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = entry.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${entry.cropName} (${entry.nepaliName})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = entry.zone,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = KrishiGreenLight,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Sowing / Planting", style = MaterialTheme.typography.labelSmall, color = KrishiGreenPrimary)
                                    Text(text = entry.sowingSeason, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = KrishiGoldContainer,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Harvest Period", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB78103))
                                    Text(text = entry.harvestSeason, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Key Operations: ${entry.keyInterventions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
