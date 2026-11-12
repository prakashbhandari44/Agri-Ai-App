package com.example.data.service

import android.graphics.Bitmap
import com.example.data.model.AnalysisResult
import com.example.data.model.ConditionType
import com.example.data.model.Crop
import com.example.data.model.CropCondition
import com.example.data.model.Severity

interface CropAnalysisService {
    suspend fun analyzeCropImage(
        bitmap: Bitmap,
        selectedCrop: Crop?,
        imageUriString: String? = null
    ): Result<AnalysisResult>

    fun validateImage(bitmap: Bitmap): ImageValidationResult
}

data class ImageValidationResult(
    val isValid: Boolean,
    val warningMessage: String? = null,
    val resolution: String = "",
    val qualityRating: String = "Good"
)

object ImageValidator {
    fun validate(bitmap: Bitmap): ImageValidationResult {
        val width = bitmap.width
        val height = bitmap.height
        val totalPixels = width * height

        if (totalPixels < 150 * 150) {
            return ImageValidationResult(
                isValid = false,
                warningMessage = "Image resolution is too low (< 150x150). Please retake a sharper photo close to the affected leaf.",
                resolution = "${width}x${height}",
                qualityRating = "Poor"
            )
        }

        // Check if image is extremely dark or uniform (e.g. camera covered)
        // Sample a 5x5 grid
        var totalBrightness = 0L
        val samplePoints = 25
        for (i in 1..5) {
            for (j in 1..5) {
                val px = (width * i) / 6
                val py = (height * j) / 6
                val pixel = bitmap.getPixel(px, py)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                totalBrightness += (r * 299 + g * 587 + b * 114) / 1000
            }
        }
        val avgBrightness = totalBrightness / samplePoints

        if (avgBrightness < 20) {
            return ImageValidationResult(
                isValid = true,
                warningMessage = "Photo appears very dark. Consider taking photo in good natural daylight.",
                resolution = "${width}x${height}",
                qualityRating = "Low Light"
            )
        }

        if (avgBrightness > 245) {
            return ImageValidationResult(
                isValid = true,
                warningMessage = "Photo is overly bright / washed out. Please avoid direct sunlight flash.",
                resolution = "${width}x${height}",
                qualityRating = "Overexposed"
            )
        }

        return ImageValidationResult(
            isValid = true,
            warningMessage = null,
            resolution = "${width}x${height}",
            qualityRating = "Optimal"
        )
    }
}
