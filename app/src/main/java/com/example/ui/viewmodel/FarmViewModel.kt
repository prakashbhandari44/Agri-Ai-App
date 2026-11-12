package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.FarmEntity
import com.example.data.local.entity.FieldEntity
import com.example.data.local.entity.MonitoringLogEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.ScanEntity
import com.example.data.model.AnalysisResult
import com.example.data.model.ConditionType
import com.example.data.model.Crop
import com.example.data.model.CropCondition
import com.example.data.model.Severity
import com.example.data.model.UserProfile
import com.example.data.model.WeatherInfo
import com.example.data.repository.CropRepository
import com.example.data.service.AIFarmChatService
import com.example.data.service.AppLanguage
import com.example.data.service.ChatMessage
import com.example.data.service.CropAnalysisService
import com.example.data.service.GeminiCropAnalysisService
import com.example.data.service.ImageValidationResult
import com.example.data.service.LocalizationManager
import com.example.data.service.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data class Validating(val message: String = "Checking image clarity...") : ScanUiState
    data class Analyzing(val step: String = "Analyzing crop condition...") : ScanUiState
    data class Success(val result: AnalysisResult) : ScanUiState
    data class QualityWarning(val validation: ImageValidationResult, val bitmap: Bitmap, val crop: Crop?) : ScanUiState
    data class Error(val errorMessage: String) : ScanUiState
}

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val userDao = db.userDao()
    private val farmDao = db.farmDao()
    private val cropDao = db.cropDao()
    private val scanDao = db.scanDao()
    private val reminderDao = db.reminderDao()

    private val localDataRepository = com.example.data.repository.LocalDataRepository(userDao, farmDao, cropDao)

    private val cropAnalysisService: CropAnalysisService = GeminiCropAnalysisService()
    private val weatherService = WeatherService()
    private val aiChatService = AIFarmChatService()

    // Scans State
    val scanHistory: StateFlow<List<ScanEntity>> = scanDao.getAllScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Farms & Fields State
    val farms: StateFlow<List<FarmEntity>> = farmDao.getAllFarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fields: StateFlow<List<FieldEntity>> = farmDao.getAllFields()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reminders State
    val reminders: StateFlow<List<ReminderEntity>> = reminderDao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Scanning State
    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

    // Selected Crop for scan
    private val _selectedCrop = MutableStateFlow<Crop?>(CropRepository.initialCrops[1]) // Default Tomato
    val selectedCrop: StateFlow<Crop?> = _selectedCrop.asStateFlow()

    // Current Analysis result viewing
    private val _currentResult = MutableStateFlow<AnalysisResult?>(null)
    val currentResult: StateFlow<AnalysisResult?> = _currentResult.asStateFlow()

    // Weather State
    private val _weather = MutableStateFlow(WeatherInfo())
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    // Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "assistant",
                text = "नमस्ते! म 'कृषि साथी' AI सहयोगी हुँ। बालीमा लागेको रोग, कीरा, मलखाद वा मौसम सम्बन्धी कुनै पनि जिज्ञासा सोध्न सक्नुहुन्छ।"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatSending = MutableStateFlow(false)
    val isChatSending: StateFlow<Boolean> = _isChatSending.asStateFlow()

    // User Profile & Settings
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            localDataRepository.seedInitialCropsIfEmpty(CropRepository.initialCrops)
        }

        viewModelScope.launch {
            // Seed a starter farm if empty
            farms.collect { currentFarms ->
                if (currentFarms.isEmpty()) {
                    val farmId = farmDao.insertFarm(
                        FarmEntity(
                            name = "Bhandari Adarsha Farm",
                            location = "Shivanagar, Bharatpur",
                            province = "Bagmati Province",
                            district = "Chitwan",
                            totalArea = 4.5,
                            areaUnit = "Kattha"
                        )
                    )
                    farmDao.insertField(
                        FieldEntity(
                            farmId = farmId,
                            fieldName = "Field 1 - Tomato Tunnel",
                            cropName = "Tomato",
                            variety = "Srijana Hybrid",
                            plantingDate = "2026-08-15",
                            expectedHarvestDate = "2026-11-20",
                            area = 2.0,
                            areaUnit = "Kattha",
                            status = "Fruiting"
                        )
                    )
                    farmDao.insertField(
                        FieldEntity(
                            farmId = farmId,
                            fieldName = "Field 2 - Paddy Terrace",
                            cropName = "Rice",
                            variety = "Sabitri",
                            plantingDate = "2026-07-05",
                            expectedHarvestDate = "2026-11-10",
                            area = 2.5,
                            areaUnit = "Kattha",
                            status = "Grain Filling"
                        )
                    )
                    reminderDao.insertReminder(
                        ReminderEntity(
                            title = "Organic Neem Spray for Tomato",
                            crop = "Tomato",
                            date = "Tomorrow",
                            time = "07:00 AM",
                            category = "Spraying"
                        )
                    )
                    reminderDao.insertReminder(
                        ReminderEntity(
                            title = "Paddy Water Drainage Check",
                            crop = "Rice",
                            date = "In 2 days",
                            time = "04:30 PM",
                            category = "Irrigation"
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            _weather.value = weatherService.getCurrentWeather(_userProfile.value.district)
        }
    }

    fun setSelectedCrop(crop: Crop?) {
        _selectedCrop.value = crop
    }

    fun setCurrentResult(result: AnalysisResult) {
        _currentResult.value = result
    }

    fun setScanStateIdle() {
        _scanState.value = ScanUiState.Idle
    }

    fun analyzeImage(bitmap: Bitmap, imageUriString: String?, bypassWarning: Boolean = false) {
        viewModelScope.launch {
            _scanState.value = ScanUiState.Validating("Validating leaf clarity & lighting...")

            val validation = cropAnalysisService.validateImage(bitmap)
            if (!validation.isValid && !bypassWarning) {
                _scanState.value = ScanUiState.QualityWarning(validation, bitmap, _selectedCrop.value)
                return@launch
            }

            _scanState.value = ScanUiState.Analyzing("AI analyzing plant disease signatures...")

            val analysisResult = cropAnalysisService.analyzeCropImage(
                bitmap = bitmap,
                selectedCrop = _selectedCrop.value,
                imageUriString = imageUriString
            )

            analysisResult.fold(
                onSuccess = { result ->
                    _currentResult.value = result
                    _scanState.value = ScanUiState.Success(result)
                },
                onFailure = { err ->
                    _scanState.value = ScanUiState.Error(err.message ?: "Failed to analyze crop image.")
                }
            )
        }
    }

    fun saveDiagnosis(result: AnalysisResult, farmId: Long? = null, notes: String = "") {
        viewModelScope.launch {
            val entity = ScanEntity(
                id = result.id,
                cropName = result.cropName,
                cropScientificName = result.cropScientificName,
                conditionType = result.condition.type.name,
                conditionName = result.condition.name,
                conditionLocalName = result.condition.localName,
                confidence = result.condition.confidence,
                severity = result.severity.name,
                symptomsCsv = result.symptoms.joinToString("||"),
                causesCsv = result.possibleCauses.joinToString("||"),
                preventionCsv = result.prevention.joinToString("||"),
                managementCsv = result.generalManagement.joinToString("||"),
                monitoringCsv = result.monitoringAdvice.joinToString("||"),
                whenToContactExpert = result.whenToContactExpert,
                imageQuality = result.imageQuality,
                uncertaintyNotice = result.uncertaintyNotice,
                imageUri = result.imageUri,
                timestamp = result.timestamp,
                farmId = farmId,
                notes = notes
            )
            scanDao.insertScan(entity)
        }
    }

    fun deleteScan(id: String) {
        viewModelScope.launch {
            scanDao.deleteScanById(id)
        }
    }

    fun addFarm(name: String, location: String, province: String, district: String, area: Double, unit: String) {
        viewModelScope.launch {
            farmDao.insertFarm(
                FarmEntity(
                    name = name,
                    location = location,
                    province = province,
                    district = district,
                    totalArea = area,
                    areaUnit = unit
                )
            )
        }
    }

    fun addField(farmId: Long, name: String, crop: String, variety: String, plantingDate: String, harvestDate: String, area: Double, unit: String) {
        viewModelScope.launch {
            farmDao.insertField(
                FieldEntity(
                    farmId = farmId,
                    fieldName = name,
                    cropName = crop,
                    variety = variety,
                    plantingDate = plantingDate,
                    expectedHarvestDate = harvestDate,
                    area = area,
                    areaUnit = unit,
                    status = "Growing"
                )
            )
        }
    }

    fun addReminder(title: String, crop: String, date: String, time: String, category: String) {
        viewModelScope.launch {
            reminderDao.insertReminder(
                ReminderEntity(
                    title = title,
                    crop = crop,
                    date = date,
                    time = time,
                    category = category
                )
            )
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            reminderDao.deleteReminder(id)
        }
    }

    fun sendChatMessage(text: String, role: com.example.data.service.AdvisorRole = com.example.data.service.AdvisorRole.GENERAL) {
        if (text.isBlank() || _isChatSending.value) return
        val userMsg = ChatMessage(sender = "user", text = text)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatSending.value = true

        viewModelScope.launch {
            val response = aiChatService.sendMessage(
                userMessage = text,
                conversationHistory = _chatMessages.value,
                languageCode = LocalizationManager.currentLanguage.code,
                role = role
            )
            val botMsg = ChatMessage(sender = "assistant", text = response, modelUsed = role.model)
            _chatMessages.value = _chatMessages.value + botMsg
            _isChatSending.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "assistant",
                text = "नमस्ते! नयाँ च्याट सुरु भयो। बालीनाली, रोग-कीरा वा मलखाद सम्बन्धी के जानकारी चाहिन्छ?"
            )
        )
    }

    fun setLanguage(language: AppLanguage) {
        LocalizationManager.currentLanguage = language
        _userProfile.value = _userProfile.value.copy(preferredLanguage = language.displayName)
    }

    fun updateProfile(
        name: String,
        district: String,
        province: String,
        farmingType: String,
        mainCrops: List<String>
    ) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            district = district,
            province = province,
            farmingType = farmingType,
            mainCrops = mainCrops
        )
        viewModelScope.launch {
            _weather.value = weatherService.getCurrentWeather(district)
        }
    }
}

fun ScanEntity.toDomainModel(): AnalysisResult {
    return AnalysisResult(
        id = this.id,
        cropName = this.cropName,
        cropScientificName = this.cropScientificName,
        condition = CropCondition(
            type = try { ConditionType.valueOf(this.conditionType) } catch (e: Exception) { ConditionType.DISEASE },
            name = this.conditionName,
            localName = this.conditionLocalName,
            confidence = this.confidence
        ),
        severity = try { Severity.valueOf(this.severity) } catch (e: Exception) { Severity.MODERATE },
        symptoms = if (this.symptomsCsv.isNotBlank()) this.symptomsCsv.split("||") else emptyList(),
        possibleCauses = if (this.causesCsv.isNotBlank()) this.causesCsv.split("||") else emptyList(),
        prevention = if (this.preventionCsv.isNotBlank()) this.preventionCsv.split("||") else emptyList(),
        generalManagement = if (this.managementCsv.isNotBlank()) this.managementCsv.split("||") else emptyList(),
        monitoringAdvice = if (this.monitoringCsv.isNotBlank()) this.monitoringCsv.split("||") else emptyList(),
        whenToContactExpert = this.whenToContactExpert,
        imageQuality = this.imageQuality,
        uncertaintyNotice = this.uncertaintyNotice,
        imageUri = this.imageUri,
        timestamp = this.timestamp,
        farmId = this.farmId,
        notes = this.notes
    )
}
