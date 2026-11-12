package com.example

import com.example.data.local.entity.CropEntity
import com.example.data.local.entity.FarmEntity
import com.example.data.local.entity.ScanEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.ConditionType
import com.example.data.model.Crop
import com.example.data.model.Severity
import com.example.data.model.UserProfile
import com.example.data.repository.CropRepository
import com.example.data.repository.toDomainModel
import com.example.data.repository.toEntity
import com.example.data.service.AppLanguage
import com.example.data.service.LocalizationManager
import com.example.ui.viewmodel.toDomainModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KrishiAiBusinessLogicTest {

    @Test
    fun testCropCatalogInitialization() {
        val crops = CropRepository.initialCrops
        assertTrue("Crop repository must contain initial crops", crops.size >= 10)

        val rice = crops.find { it.id == "rice" }
        assertNotNull("Rice must exist in catalog", rice)
        assertEquals("Oryza sativa", rice?.scientificName)
        assertTrue("Rice should support Nepal", rice?.supportedCountries?.contains("Nepal") == true)

        val tomato = crops.find { it.id == "tomato" }
        assertNotNull("Tomato must exist in catalog", tomato)
        assertTrue(tomato?.commonDiseases?.contains("Early Blight") == true)
    }

    @Test
    fun testUserEntityAndDomainMapping() {
        val userEntity = UserEntity(
            id = "usr_001",
            name = "Ram Bahadur Thapa",
            email = "ram@krishiai.np",
            phone = "+977 9841234567",
            country = "Nepal",
            province = "Bagmati Province",
            district = "Chitwan",
            preferredLanguage = "ne",
            farmingType = "Commercial Polyhouse",
            mainCropsCsv = "Tomato||Cucumber||Chilli",
            isGuest = false,
            role = "FARMER"
        )

        val domainUser = userEntity.toDomainModel()
        assertEquals("usr_001", domainUser.id)
        assertEquals("Ram Bahadur Thapa", domainUser.name)
        assertEquals("Chitwan", domainUser.district)
        assertEquals(3, domainUser.mainCrops.size)
        assertTrue(domainUser.mainCrops.contains("Tomato"))

        val roundTripEntity = domainUser.toEntity()
        assertEquals(userEntity.id, roundTripEntity.id)
        assertEquals(userEntity.email, roundTripEntity.email)
        assertEquals(userEntity.mainCropsCsv, roundTripEntity.mainCropsCsv)
    }

    @Test
    fun testFarmEntityCreation() {
        val farm = FarmEntity(
            id = 101L,
            userId = "usr_001",
            name = "Thapa Organic Agro Farm",
            location = "Gitanagar",
            province = "Bagmati Province",
            district = "Chitwan",
            totalArea = 5.0,
            areaUnit = "Kattha"
        )
        assertEquals(101L, farm.id)
        assertEquals("usr_001", farm.userId)
        assertEquals("Kattha", farm.areaUnit)
    }

    @Test
    fun testCropEntityAndDomainMapping() {
        val crop = Crop(
            id = "rice",
            name = "Rice",
            localNameNepali = "धान",
            localNameHindi = "चावल",
            scientificName = "Oryza sativa",
            category = "Cereal",
            supportedCountries = listOf("Nepal", "India"),
            iconEmoji = "🌾",
            description = "Staple food crop",
            growingSeason = "Monsoon",
            commonDiseases = listOf("Blast", "Bacterial Blight"),
            commonPests = listOf("Stem Borer"),
            nutrientProblems = listOf("Zinc deficiency")
        )

        val entity = crop.toEntity()
        assertEquals("rice", entity.id)
        assertEquals("धान", entity.localNameNepali)
        assertTrue(entity.commonDiseasesCsv.contains("Blast"))

        val domainCrop = entity.toDomainModel()
        assertEquals(crop.id, domainCrop.id)
        assertEquals(crop.scientificName, domainCrop.scientificName)
        assertEquals(2, domainCrop.commonDiseases.size)
    }

    @Test
    fun testLocalizationManager() {
        LocalizationManager.currentLanguage = AppLanguage.NEPALI
        val nepaliScan = LocalizationManager.getString("scan_crop")
        assertTrue(nepaliScan.contains("स्क्यान") || nepaliScan.isNotBlank())

        LocalizationManager.currentLanguage = AppLanguage.ENGLISH
        val englishScan = LocalizationManager.getString("scan_crop")
        assertEquals("Scan Crop", englishScan)
    }

    @Test
    fun testScanEntityDomainMapping() {
        val entity = ScanEntity(
            id = "test_123",
            cropName = "Tomato",
            cropScientificName = "Solanum lycopersicum",
            conditionType = "DISEASE",
            conditionName = "Early Blight",
            conditionLocalName = "अगेतो डडुवा",
            confidence = 88,
            severity = "MODERATE",
            symptomsCsv = "Spot 1||Spot 2",
            causesCsv = "Fungus Alternaria",
            preventionCsv = "Mulching||Rotation",
            managementCsv = "Copper spray",
            monitoringCsv = "Weekly check",
            whenToContactExpert = "When >20% infected",
            imageQuality = "Good",
            uncertaintyNotice = "AI estimate",
            imageUri = null,
            timestamp = 1000000L,
            farmId = 1L,
            notes = "Test field 1"
        )

        val domain = entity.toDomainModel()
        assertEquals("test_123", domain.id)
        assertEquals(ConditionType.DISEASE, domain.condition.type)
        assertEquals(Severity.MODERATE, domain.severity)
        assertEquals(88, domain.condition.confidence)
        assertEquals(2, domain.symptoms.size)
        assertEquals("Spot 1", domain.symptoms[0])
    }

    @Test
    fun testKnowledgeArticlesAndExpertsAvailable() {
        assertTrue(CropRepository.knowledgeArticles.isNotEmpty())
        assertTrue(CropRepository.expertsList.isNotEmpty())
        assertTrue(CropRepository.nepalProvinces.size == 7)
    }
}
