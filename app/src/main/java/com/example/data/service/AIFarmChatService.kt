package com.example.data.service

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "user" or "assistant"
    val text: String,
    val modelUsed: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AdvisorRole(val title: String, val model: String, val description: String) {
    GENERAL("General Agronomist", "gemini-3.5-flash", "General crop care, planting, and seasonal guidance"),
    PATHOLOGIST("Plant Pathologist", "gemini-3.1-pro-preview", "Complex disease reasoning & pest epidemiology"),
    QUICK("Quick Field Advisor", "gemini-3.1-flash-lite-preview", "Fast answers for urgent field queries")
}

class AIFarmChatService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        languageCode: String = "ne",
        role: AdvisorRole = AdvisorRole.GENERAL
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineAgronomyResponse(userMessage, languageCode)
        }

        try {
            val roleSpecificDirective = when (role) {
                AdvisorRole.PATHOLOGIST -> "You are a Senior Plant Pathologist with deep clinical expertise in fungal, viral, and bacterial pathogens in South Asian agriculture. Provide in-depth reasoning about symptom etiology, pathogen life cycles, and integrated disease containment."
                AdvisorRole.QUICK -> "You are a Rapid Field Scout. Give concise, direct, 2-3 sentence actionable bullet points for urgent farmer questions."
                AdvisorRole.GENERAL -> "You are 'Krishi Sathi', a practical agricultural officer supporting farmers in Nepal with day-to-day crop management, fertilization, irrigation, and IPM."
            }

            val systemPrompt = """
$roleSpecificDirective
Guidelines:
1. Provide accurate, practical agronomic advice for crops such as Rice, Maize, Wheat, Potato, Tomato, Vegetables, and Fruits.
2. Emphasize organic prevention, balanced soil nutrition, Integrated Pest Management (IPM), and water stewardship.
3. NEVER prescribe dangerous chemical cocktail mixtures or recommend banned chemicals. Always state that chemical pesticides must follow locally approved registered product labels and local agro-vet advice.
4. If a problem is severe or symptoms are ambiguous, encourage the farmer to consult their local municipality agriculture officer (कृषि शाखा / जे.टी.ए.).
5. Respond in the user's language: if Nepali ($languageCode == "ne"), write in natural Nepali script; if Hindi, Hindi script; if English, English.
6. Keep explanations clear, friendly, and structured with bullet points.
            """.trimIndent()

            val contentsArray = JSONArray()

            // System turn
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            contentsArray.put(JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", "नमस्ते! म तपाईंको कृषि सहयोगी 'कृषि साथी' (${role.title}) हुँ। भन्नुहोस्, बालीनाली सम्बन्धी के सहयोग चाहिन्छ?") })
                })
            })

            // Recent history (last 6 turns for multi-turn conversational context)
            val recentHistory = conversationHistory.takeLast(6)
            for (msg in recentHistory) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (msg.sender == "user") "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", msg.text) })
                    })
                })
            }

            // Current user turn
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", userMessage) })
                })
            })

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", if (role == AdvisorRole.PATHOLOGIST) 0.2 else 0.3)
                    put("maxOutputTokens", if (role == AdvisorRole.QUICK) 350 else 850)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/${role.model}:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext getOfflineAgronomyResponse(userMessage, languageCode)
            }

            val body = response.body?.string() ?: ""
            val root = JSONObject(body)
            val candidates = root.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            text?.takeIf { it.isNotBlank() } ?: getOfflineAgronomyResponse(userMessage, languageCode)
        } catch (e: Exception) {
            getOfflineAgronomyResponse(userMessage, languageCode)
        }
    }

    private fun getOfflineAgronomyResponse(query: String, lang: String): String {
        val q = query.lowercase()
        return when {
            q.contains("yellow") || q.contains("पहेँलो") || q.contains("पहेलो") || q.contains("पीला") -> {
                if (lang == "ne") {
                    """पात पहेँलो हुनुका प्रमुख कारणहरू:

१. नाइट्रोजन (Nitrogen) को कमी: पुराना तथा तलका पातहरू 'V' आकारमा पहेँलिँदै जान्छन्।
२. बढी पानी जम्नु वा जरा कुहिनु: खेतमा पानी निकासको राम्रो व्यवस्था गर्नुहोस्।
३. सुक्ष्म तत्व (Iron / Magnesium / Zinc) को कमी: नसाहरू हरियो रही बीचको भाग पहेँलो भएमा सुक्ष्म तत्व छर्कनुहोस्।
४. भाइरस वा ढुसी रोग: यदि पात बटारिएर पहेँलो भएको छ भने सेतो झिँगा वा थ्रिप्स कीराको नियन्त्रण गर्नुहोस्।

सुझाव: प्राङ्गारिक कम्पोस्ट मल प्रयोग गर्नुहोस् र माटोको चिस्यान सन्तुलनमा राख्नुहोस्। समस्या नरोकिए स्थानीय कृषि प्राविधिकसँग परामर्श गर्नुहोस्।"""
                } else {
                    """Common reasons for yellowing leaves:

1. Nitrogen Deficiency: Lower/older leaves turn pale yellow starting from tips in a 'V' pattern.
2. Poor Drainage / Waterlogging: Suffocated roots cannot uptake oxygen; ensure drainage channels.
3. Micronutrient Deficiency (Iron/Zinc/Magnesium): Interveinal chlorosis (veins stay green).
4. Viral or Fungal Disease: If leaves curl and show yellow mosaic, inspect for aphids/whiteflies.

Recommendation: Apply well-rotted farmyard manure, verify soil moisture, and consult your local extension office if yellowing spreads rapidly."""
                }
            }
            q.contains("rice") || q.contains("dhan") || q.contains("धान") || q.contains("चावल") -> {
                if (lang == "ne") {
                    """धान खेती गर्दा ध्यान दिनुपर्ने मुख्य बुँदाहरू:

१. उन्नत बीउ छनोट: आफ्नो क्षेत्र सुहाउँदो (जस्तै खुमल-४, सावाँ मन्सुली, सुक्खा सिरिज) प्रमाणित बीउ प्रयोग गर्नुहोस्।
२. बेर्ना उमेर: २१ देखि २५ दिनको कलिलो र बलियो बेर्ना मात्र रोप्नुहोस्।
३. बिरुवाको दूरी: हारदेखि हार २० सेमी र बोटदेखि बोट १५ सेमी कायम गर्नुहोस्।
४. सन्तुलित मलखाद: रासायनिक युरियालाई ३ पटक (रोप्दा, गाँज आउँदा र बाला निस्कने बेला) फुटाएर हाल्नुहोस्।
५. पानी व्यवस्थापन: बेर्ना रोपेको केही दिन २-३ सेमी पानी राख्ने, त्यसपछि बेला-बेला खेत सुकाएर हावा छिर्न दिनुपर्छ।"""
                } else {
                    """Essential best practices for Rice (Paddy) cultivation:

1. Quality Seed Selection: Use certified local varieties (e.g. Khumal series, Sabitri, Radha-4).
2. Healthy Seedlings: Transplant 21-25 day old robust seedlings (2-3 seedlings per hill).
3. Spacing: Maintain 20 cm x 15 cm row spacing for optimal tillering and sunlight.
4. Balanced Nutrition: Split Urea application into basal, tillering, and panicle initiation stages.
5. Water Management: Avoid continuous deep flooding; practice alternate wetting and drying to aerate root zones."""
                }
            }
            q.contains("fertilizer") || q.contains("मल") || q.contains("खाद") || q.contains("urea") -> {
                if (lang == "ne") {
                    """सन्तुलित मलखाद सम्बन्धी जानकारी:

- प्राङ्गारिक मल: प्रति रोपनी कम्तीमा ५०० देखि ८०० केजी राम्रोसँग पाकेको गोठेमल वा कम्पोस्ट प्रयोग गर्नुहोस्।
- NPK सन्तुलन: केवल युरिया मात्र बढी प्रयोग गर्दा रोग-कीराको प्रकोप तीव्र हुन्छ। डीएपी (DAP) र पोटास (MOP) पनि सन्तुलित मात्रामा प्रयोग गर्नुहोस्।
- माटोको pH: यदि माटो अमिलो छ भने कृषि चुन प्रयोग गर्नुहोस्।
- जैविक मल: गँड्यौला मल (Vermicompost) र ट्राइकोडर्मा मिसाएर प्रयोग गर्दा बिरुवाको प्रतिरोधात्मक क्षमता बढ्छ।"""
                } else {
                    """Balanced Soil Fertilization Guide:

- Organic Base: Always incorporate 10-15 tons of well-decomposed FYM or compost per hectare.
- Balanced NPK: Overusing urea attracts pests and weakens cell walls. Balance with Phosphorus (DAP) and Potassium (MOP).
- Soil Liming: Test your soil pH annually. Apply agricultural lime if pH is acidic (< 5.5).
- Bio-fertilizers: Incorporate Trichoderma and mycorrhiza to safeguard root systems from soil pathogens."""
                }
            }
            else -> {
                if (lang == "ne") {
                    """तपाईंको प्रश्नका लागि धन्यवाद।

बालीनालीमा देखिने कुनै पनि शङ्कास्पद रोग वा कीराको लागि:
१. 'स्क्यान' ट्याबमा गई प्रभावित पात वा फलको सफा फोटो खिच्नुहोस्।
२. जैविक रोकथाम, नीमजन्य औषधी र सन्तुलित सिँचाइमा जोड दिनुहोस्।
३. कुनै पनि रासायनिक औषधी प्रयोग गर्दा स्वीकृत लेवल र कृषि प्राविधिकको सल्लाह अनिवार्य लिनुहोस्।

तपाईं कुन बाली सम्बन्धी विस्तृत जानकारी चाहनुहुन्छ? कृपया बालीको नाम बताउनुहोस् (जस्तै: गोलभेंडा, आलु, मकै, धान आदि)।"""
                } else {
                    """Thank you for your question.

For precise crop diagnostics:
1. Go to the 'Scan' tab and upload a clear, focused photo of the affected plant part.
2. Focus on integrated management, sanitation, and biological controls first.
3. Always verify chemical interventions with local agricultural extension labels.

Which specific crop are you asking about? Feel free to mention Tomato, Rice, Maize, Potato, or other crops!"""
                }
            }
        }
    }
}
