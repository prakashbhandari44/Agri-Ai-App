package com.example.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// ================================================================
// Backend API Architecture & Contracts (RESTful Specification)
// ================================================================

data class LoginRequest(val emailOrPhone: String, val passwordHash: String)
data class AuthResponse(val token: String, val refreshToken: String, val user: ApiUser)
data class ApiUser(val id: String, val name: String, val role: String, val country: String, val district: String)

data class ApiCrop(
    val id: String,
    val name: String,
    val localNameNepali: String,
    val scientificName: String,
    val category: String,
    val commonDiseases: List<String>
)

data class ScanUploadRequest(
    val imageBase64: String,
    val cropId: String?,
    val farmId: Long?,
    val clientTimestamp: Long
)

data class ApiScanResponse(
    val id: String,
    val crop: String,
    val condition: String,
    val confidence: Int,
    val severity: String,
    val recommendations: List<String>
)

data class DiagnosisFeedbackRequest(
    val scanId: String,
    val isHelpful: Boolean,
    val errorCategory: String? = null, // "wrong_crop", "wrong_disease", "wrong_pest", "wrong_deficiency", "image_problem", "other"
    val userComment: String? = null
)

data class ExpertConsultationRequest(
    val expertId: String,
    val farmerName: String,
    val phone: String,
    val cropName: String,
    val scanId: String?,
    val queryNotes: String
)

data class GenericResponse(val success: Boolean, val message: String)

interface KrishiBackendApi {

    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: LoginRequest): AuthResponse

    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): ApiUser

    // Crops
    @GET("crops")
    suspend fun getCrops(@Query("country") country: String = "Nepal"): List<ApiCrop>

    @GET("crops/{id}")
    suspend fun getCropDetail(@Path("id") cropId: String): ApiCrop

    // Scans
    @POST("scan")
    suspend fun submitCropScan(
        @Header("Authorization") token: String,
        @Body request: ScanUploadRequest
    ): ApiScanResponse

    @GET("scans")
    suspend fun getScanHistory(@Header("Authorization") token: String): List<ApiScanResponse>

    // Feedback
    @POST("feedback")
    suspend fun submitFeedback(
        @Header("Authorization") token: String,
        @Body feedback: DiagnosisFeedbackRequest
    ): GenericResponse

    // Expert Consultation
    @POST("expert-requests")
    suspend fun requestExpertConsultation(
        @Header("Authorization") token: String,
        @Body request: ExpertConsultationRequest
    ): GenericResponse
}
