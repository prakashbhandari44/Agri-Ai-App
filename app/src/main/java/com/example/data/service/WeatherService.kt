package com.example.data.service

import com.example.data.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherService {

    suspend fun getCurrentWeather(district: String = "Chitwan"): WeatherInfo = withContext(Dispatchers.IO) {
        // District-specific agrometeorology simulations based on Nepal seasonal climatology
        when (district.lowercase()) {
            "chitwan", "rupandehi", "jhapa", "kailali" -> WeatherInfo(
                locationName = "$district, Nepal (Terai)",
                temperatureC = 29,
                condition = "Humid & Partly Cloudy",
                humidityPercent = 82,
                rainProbabilityPercent = 55,
                rainfallMm = 12.4,
                windSpeedKmh = 14,
                diseaseFavorableRisk = "High Fungal Blight & Downy Mildew Risk",
                agriculturalAdvice = "High humidity (>80%) creates favorable microclimate for late blight and leaf spots. Delay nitrogen top-dressing before impending afternoon rains."
            )
            "mustang", "jumla", "manang" -> WeatherInfo(
                locationName = "$district, Nepal (High Mountain)",
                temperatureC = 14,
                condition = "Clear Sky & Cool Breeze",
                humidityPercent = 48,
                rainProbabilityPercent = 10,
                rainfallMm = 0.0,
                windSpeedKmh = 18,
                diseaseFavorableRisk = "Low Fungal Risk / Frost Hazard at Night",
                agriculturalAdvice = "Protect apple orchards and potato nurseries from cold night winds. Ensure adequate mulch around root bases to retain soil warmth."
            )
            else -> WeatherInfo(
                locationName = "$district, Nepal (Mid-Hills)",
                temperatureC = 23,
                condition = "Mild Sunshine & Scattered Clouds",
                humidityPercent = 71,
                rainProbabilityPercent = 35,
                rainfallMm = 4.2,
                windSpeedKmh = 10,
                diseaseFavorableRisk = "Moderate Disease Risk",
                agriculturalAdvice = "Favorable weather for field weeding and vegetable staking. Scout for early blight and aphid colonies in terrace tomato crops."
            )
        }
    }
}
