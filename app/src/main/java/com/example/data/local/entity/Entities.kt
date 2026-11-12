package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Entity - Establishes local identity, regional preferences, and farmer profile.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default",
    val name: String,
    val email: String,
    val phone: String,
    val country: String = "Nepal",
    val province: String = "Bagmati Province",
    val district: String = "Chitwan",
    val preferredLanguage: String = "ne",
    val farmingType: String = "Semi-Commercial",
    val mainCropsCsv: String = "Rice||Tomato||Maize",
    val isGuest: Boolean = false,
    val role: String = "FARMER",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Farm Entity - Represents a farmer's registered agricultural holding,
 * linked to a User and containing multiple fields/plots.
 */
@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_default",
    val name: String,
    val location: String,
    val province: String,
    val district: String,
    val totalArea: Double,
    val areaUnit: String, // Ropani, Kattha, Bigha, Hectare, Acre
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Crop Entity - Scalable local crop catalog cached for offline agricultural guidance,
 * disease lookup, and multi-country expansion.
 */
@Entity(tableName = "crops")
data class CropEntity(
    @PrimaryKey val id: String, // e.g., "rice", "tomato", "maize"
    val name: String,
    val localNameNepali: String,
    val localNameHindi: String,
    val scientificName: String,
    val category: String, // Cereal, Vegetable, Fruit, Tuber, Legume, Oilseed
    val supportedCountriesCsv: String = "Nepal||India||Bangladesh",
    val iconEmoji: String,
    val description: String,
    val growingSeason: String,
    val commonDiseasesCsv: String,
    val commonPestsCsv: String,
    val nutrientProblemsCsv: String,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Field/Plot Entity - Individual cultivation plot within a Farm.
 */
@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: Long,
    val fieldName: String,
    val cropName: String,
    val variety: String,
    val plantingDate: String,
    val expectedHarvestDate: String,
    val area: Double,
    val areaUnit: String,
    val status: String
)

/**
 * Monitoring Log Entity - Field timeline events (Planted, Vegetative, Spray, Harvest).
 */
@Entity(tableName = "monitoring_logs")
data class MonitoringLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fieldId: Long,
    val date: String,
    val stage: String,
    val title: String,
    val notes: String,
    val photoUri: String? = null
)

/**
 * Scan Result Entity - Persisted AI diagnosis history.
 */
@Entity(tableName = "scan_results")
data class ScanEntity(
    @PrimaryKey val id: String,
    val cropName: String,
    val cropScientificName: String,
    val conditionType: String,
    val conditionName: String,
    val conditionLocalName: String,
    val confidence: Int,
    val severity: String,
    val symptomsCsv: String,
    val causesCsv: String,
    val preventionCsv: String,
    val managementCsv: String,
    val monitoringCsv: String,
    val whenToContactExpert: String,
    val imageQuality: String,
    val uncertaintyNotice: String,
    val imageUri: String?,
    val timestamp: Long,
    val farmId: Long?,
    val notes: String
)

/**
 * Farm Reminder Entity - Actionable task reminders for irrigation, fertilizer, spraying.
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val crop: String,
    val date: String,
    val time: String,
    val category: String,
    val isCompleted: Boolean = false,
    val notes: String = ""
)
