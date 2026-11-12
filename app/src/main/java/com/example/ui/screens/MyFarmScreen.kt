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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.entity.FarmEntity
import com.example.data.local.entity.FieldEntity
import com.example.data.repository.CropRepository
import com.example.data.service.localized
import com.example.ui.theme.KrishiGold
import com.example.ui.theme.KrishiGreenContainer
import com.example.ui.theme.KrishiGreenLight
import com.example.ui.theme.KrishiGreenPrimary
import com.example.ui.theme.KrishiGreenSecondary
import com.example.ui.viewmodel.FarmViewModel

@Composable
fun MyFarmScreen(
    viewModel: FarmViewModel,
    modifier: Modifier = Modifier
) {
    val farms by viewModel.farms.collectAsStateWithLifecycle()
    val fields by viewModel.fields.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    var showAddFarmDialog by remember { mutableStateOf(false) }
    var showAddFieldDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var selectedFarmForField by remember { mutableStateOf<FarmEntity?>(null) }

    // Dialog for Adding Farm
    if (showAddFarmDialog) {
        AddFarmDialog(
            onDismiss = { showAddFarmDialog = false },
            onConfirm = { name, location, province, district, area, unit ->
                viewModel.addFarm(name, location, province, district, area, unit)
                showAddFarmDialog = false
            }
        )
    }

    // Dialog for Adding Field
    if (showAddFieldDialog && selectedFarmForField != null) {
        AddFieldDialog(
            farm = selectedFarmForField!!,
            onDismiss = { showAddFieldDialog = false },
            onConfirm = { name, crop, variety, pDate, hDate, area, unit ->
                viewModel.addField(selectedFarmForField!!.id, name, crop, variety, pDate, hDate, area, unit)
                showAddFieldDialog = false
            }
        )
    }

    // Dialog for Adding Reminder
    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onConfirm = { title, crop, date, time, category ->
                viewModel.addReminder(title, crop, date, time, category)
                showAddReminderDialog = false
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("my_farm_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = localized("my_farm"),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = KrishiGreenPrimary
                        )
                        Text(
                            text = "Monitor your plots, crops, harvest dates & farm care",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddFarmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(localized("add_farm"))
                    }
                }
            }

            // Farms List
            if (farms.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🏡", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No farms registered yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add your farm and plot records to track crop health timelines.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddFarmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
                            ) {
                                Text(localized("add_farm"))
                            }
                        }
                    }
                }
            } else {
                items(farms) { farm ->
                    val farmFields = fields.filter { it.farmId == farm.id }
                    FarmCardItem(
                        farm = farm,
                        fields = farmFields,
                        onAddFieldClick = {
                            selectedFarmForField = farm
                            showAddFieldDialog = true
                        }
                    )
                }
            }

            // Crop Growth Timeline Demonstration
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = localized("farm_timeline"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                CropGrowthTimelineCard()
            }

            // Reminders Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = localized("reminders"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { showAddReminderDialog = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(localized("add_reminder"))
                    }
                }
            }

            items(reminders) { reminder ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (reminder.isCompleted) Color(0xFFF1F3F0) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (reminder.isCompleted) Color(0xFFC8E6C9) else Color(0xFFFFF3E0),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { viewModel.toggleReminder(reminder) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (reminder.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = KrishiGreenPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Pending",
                                        tint = Color(0xFFEF6C00),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = reminder.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (reminder.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${reminder.crop} • ${reminder.category} • ${reminder.date} (${reminder.time})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FarmCardItem(
    farm: FarmEntity,
    fields: List<FieldEntity>,
    onAddFieldClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("farm_card_${farm.id}"),
        shape = RoundedCornerShape(18.dp),
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KrishiGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = KrishiGreenPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = farm.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${farm.location}, ${farm.district}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KrishiGreenLight
                ) {
                    Text(
                        text = "${farm.totalArea} ${farm.areaUnit}",
                        style = MaterialTheme.typography.labelMedium,
                        color = KrishiGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-fields section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fields / Plots (${fields.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+ Add Field",
                    style = MaterialTheme.typography.labelMedium,
                    color = KrishiGreenPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onAddFieldClick)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (fields.isEmpty()) {
                Text(
                    text = "No plots added to this farm yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                fields.forEach { field ->
                    FieldSubItem(field = field)
                }
            }
        }
    }
}

@Composable
private fun FieldSubItem(field: FieldEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = field.fieldName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${field.cropName} (${field.variety})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Planted: ${field.plantingDate} | Harvest: ${field.expectedHarvestDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = KrishiGreenContainer
            ) {
                Text(
                    text = field.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = KrishiGreenPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CropGrowthTimelineCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Example Field: Tomato Tunnel 1 (Srijana Hybrid)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = KrishiGreenPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            TimelineStage(day = "Day 1", title = "Transplanting", desc = "Transplanted 25-day seedlings with basal compost & DAP", isDone = true)
            TimelineStage(day = "Day 20", title = "Staking & Weeding", desc = "Bamboo trellising installed and first shallow hoeing", isDone = true)
            TimelineStage(day = "Day 35", title = "AI Crop Scan", desc = "Diagnosed minor Early Blight on bottom leaves; sprayed bio-fungicide", isDone = true)
            TimelineStage(day = "Day 55", title = "Fruiting & Potassium Dressing", desc = "Heavy flower cluster set; applied MOP top dressing", isDone = false)
            TimelineStage(day = "Day 85", title = "First Commercial Harvest", desc = "Harvest ripe fruits early morning for wholesale market", isDone = false)
        }
    }
}

@Composable
private fun TimelineStage(
    day: String,
    title: String,
    desc: String,
    isDone: Boolean
) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(if (isDone) KrishiGreenPrimary else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = KrishiGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddFarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, location: String, province: String, district: String, area: Double, unit: String) -> Unit
) {
    var farmName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("Bagmati Province") }
    var district by remember { mutableStateOf("Chitwan") }
    var areaText by remember { mutableStateOf("2.5") }
    var areaUnit by remember { mutableStateOf("Ropani") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = localized("add_farm"), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = farmName,
                    onValueChange = { farmName = it },
                    label = { Text(localized("farm_name")) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Village / Municipality") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = areaText,
                        onValueChange = { areaText = it },
                        label = { Text("Total Area") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = areaUnit,
                        onValueChange = { areaUnit = it },
                        label = { Text("Unit (Ropani/Bigha)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (farmName.isNotBlank()) {
                        val area = areaText.toDoubleOrNull() ?: 1.0
                        onConfirm(farmName, location, province, district, area, areaUnit)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
            ) {
                Text("Save Farm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddFieldDialog(
    farm: FarmEntity,
    onDismiss: () -> Unit,
    onConfirm: (name: String, crop: String, variety: String, pDate: String, hDate: String, area: Double, unit: String) -> Unit
) {
    var fieldName by remember { mutableStateOf("") }
    var cropName by remember { mutableStateOf("Tomato") }
    var variety by remember { mutableStateOf("Local Hybrid") }
    var plantingDate by remember { mutableStateOf("2026-09-01") }
    var harvestDate by remember { mutableStateOf("2026-12-15") }
    var areaText by remember { mutableStateOf("1.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add Plot to ${farm.name}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fieldName,
                    onValueChange = { fieldName = it },
                    label = { Text("Plot/Field Name (e.g. South Terrace)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    label = { Text("Crop Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = variety,
                    onValueChange = { variety = it },
                    label = { Text("Seed Variety") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = plantingDate,
                        onValueChange = { plantingDate = it },
                        label = { Text("Planted Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = harvestDate,
                        onValueChange = { harvestDate = it },
                        label = { Text("Harvest Date") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fieldName.isNotBlank()) {
                        val area = areaText.toDoubleOrNull() ?: 1.0
                        onConfirm(fieldName, cropName, variety, plantingDate, harvestDate, area, farm.areaUnit)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
            ) {
                Text("Add Plot")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, crop: String, date: String, time: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var crop by remember { mutableStateOf("Tomato") }
    var date by remember { mutableStateOf("Tomorrow") }
    var time by remember { mutableStateOf("07:30 AM") }
    var category by remember { mutableStateOf("Spraying") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = localized("add_reminder"), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Task") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = crop,
                    onValueChange = { crop = it },
                    label = { Text("Crop") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, crop, date, time, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KrishiGreenPrimary)
            ) {
                Text("Save Reminder")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
