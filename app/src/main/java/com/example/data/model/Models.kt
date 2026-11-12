package com.example.data.model

enum class ConditionType(val displayName: String, val nepaliName: String) {
    DISEASE("Plant Disease", "रोग"),
    PEST("Pest Infestation", "कीरा/शत्रुजीव"),
    NUTRIENT_DEFICIENCY("Nutrient Deficiency", "पोषकतत्वको कमी"),
    HEALTHY("Healthy Plant", "स्वस्थ बाली"),
    ENVIRONMENTAL_STRESS("Environmental Stress", "वातावरणीय तनाव"),
    UNKNOWN("Undetermined / Uncertain", "अस्पष्ट / अनिश्चित")
}

enum class Severity(val displayName: String, val nepaliName: String) {
    LOW("Low", "कम"),
    MODERATE("Moderate", "मध्यम"),
    HIGH("High / Urgent", "गम्भीर / तुरुन्त ध्यान दिनुपर्ने"),
    UNKNOWN("Unknown", "अज्ञात")
}

data class Crop(
    val id: String,
    val name: String,
    val localNameNepali: String,
    val localNameHindi: String,
    val scientificName: String,
    val category: String,
    val supportedCountries: List<String> = listOf("Nepal", "India", "Bangladesh"),
    val iconEmoji: String,
    val description: String,
    val growingSeason: String,
    val commonDiseases: List<String>,
    val commonPests: List<String>,
    val nutrientProblems: List<String>,
    val status: String = "ACTIVE"
)

data class CropCondition(
    val type: ConditionType,
    val name: String,
    val localName: String = "",
    val confidence: Int = 0 // 0 to 100
)

data class AnalysisResult(
    val id: String = System.currentTimeMillis().toString(),
    val cropName: String,
    val cropScientificName: String,
    val condition: CropCondition,
    val severity: Severity,
    val symptoms: List<String>,
    val possibleCauses: List<String>,
    val prevention: List<String>,
    val generalManagement: List<String>,
    val monitoringAdvice: List<String>,
    val whenToContactExpert: String,
    val imageQuality: String = "Good",
    val uncertaintyNotice: String = "AI-estimated result for assistance only. For serious decisions, consult your local agricultural extension officer.",
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val farmId: Long? = null,
    val notes: String = ""
)

data class Farm(
    val id: Long = 0,
    val name: String,
    val location: String,
    val province: String,
    val district: String,
    val totalArea: Double,
    val areaUnit: String = "Ropani", // Ropani, Bigha, Hectare, Acre
    val fieldsCount: Int = 1
)

data class FarmField(
    val id: Long = 0,
    val farmId: Long,
    val fieldName: String,
    val cropName: String,
    val variety: String,
    val plantingDate: String,
    val expectedHarvestDate: String,
    val area: Double,
    val areaUnit: String = "Ropani",
    val status: String = "Growing" // Planted, Vegetative, Flowering, Fruiting, Harvested
)

data class CropMonitoringLog(
    val id: Long = 0,
    val fieldId: Long,
    val date: String,
    val stage: String,
    val title: String,
    val notes: String,
    val photoUri: String? = null
)

data class FarmReminder(
    val id: Long = 0,
    val title: String,
    val crop: String,
    val date: String,
    val time: String,
    val category: String, // Irrigation, Fertilizer, Spraying, Monitoring, Harvest
    val isCompleted: Boolean = false,
    val notes: String = ""
)

data class WeatherInfo(
    val locationName: String = "Chitwan, Nepal",
    val temperatureC: Int = 27,
    val condition: String = "Partly Cloudy",
    val humidityPercent: Int = 78,
    val rainProbabilityPercent: Int = 45,
    val rainfallMm: Double = 3.5,
    val windSpeedKmh: Int = 12,
    val diseaseFavorableRisk: String = "Moderate (High humidity favored for fungal blight)",
    val agriculturalAdvice: String = "Avoid foliar spraying before evening rain. Monitor tomatoes and potatoes for late blight."
)

data class ExpertProfile(
    val id: String,
    val name: String,
    val specialization: String,
    val affiliation: String,
    val location: String,
    val languages: List<String>,
    val rating: Double,
    val experienceYears: Int,
    val isAvailable: Boolean = true,
    val consultationFee: String = "Free / Subsidized"
)

data class KnowledgeArticle(
    val id: String,
    val title: String,
    val nepaliTitle: String,
    val category: String,
    val summary: String,
    val nepaliSummary: String,
    val content: String,
    val nepaliContent: String,
    val crops: List<String>,
    val readTimeMinutes: Int = 4,
    val lastUpdated: String = "2026-09"
)

data class UserProfile(
    val id: String = "user_default",
    val name: String = "Bhandari Farmer",
    val email: String = "farmer@krishiai.np",
    val phone: String = "+977 9801234567",
    val country: String = "Nepal",
    val province: String = "Bagmati Province",
    val district: String = "Chitwan",
    val preferredLanguage: String = "Nepali",
    val farmingType: String = "Semi-Commercial",
    val mainCrops: List<String> = listOf("Rice", "Tomato", "Maize"),
    val isGuest: Boolean = false
)
