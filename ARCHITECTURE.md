# Krishi AI — Architecture & Scalability Specification

## 1. Executive Summary & Vision
**Krishi AI** is an intelligent agricultural operating system designed to assist smallholder and commercial farmers in diagnosing crop diseases, managing farm plot cycles, receiving hyper-local agrometeorological risk advisories, and consulting certified agronomists. Initially tailored for Nepal with full bilingual support (Nepali & English), its decoupled architecture is engineered for frictionless international scaling across India, Bangladesh, Southeast Asia, and Africa.

---

## 2. Technology Stack Selection Rationale

### 2.1 UI Layer: Jetpack Compose (Material Design 3)
* **Single-Source Declarative UI**: Replaces legacy Android XML layouts with reactive Kotlin composables, reducing boilerplate by 45% and eliminating UI-state desynchronization bugs.
* **Window Size Classes**: Out-of-the-box adaptive layout support across standard vertical phones, high-resolution tablets, and foldables.
* **Accessible & High-Contrast**: Implements minimum 48dp touch targets, clear typography, and color schemes calibrated for outdoor sunlight readability.

### 2.2 Local Persistence: Room Database (SQLite + KSP)
* **Offline-First Guarantee**: Rural agricultural areas frequently experience intermittent or absent cellular connectivity. Room ensures full persistence for farm layouts, field histories, crop calendars, and historical diagnostic logs without server roundtrips.
* **Type-Safe Querying via KSP**: Uses Kotlin Symbol Processing for compile-time verified SQL queries and reactive `Flow<T>` emissions.

### 2.3 Network & API Layer: Retrofit 2 + OkHttp 4
* **Asynchronous Coroutine Integration**: Seamless integration with Kotlin Coroutines (`suspend fun`), automatic Moshi/Kotlinx Serialization converters, and configurable 60-second timeouts suited for multimodal AI payloads.
* **Dynamic Interceptors**: Centralized token injection for bearer authentication, request retries, and network connectivity checking.

### 2.4 Cloud & Identity: Firebase Platform
* **Authentication**: Scalable phone number OTP, Google Sign-In via Android Credential Manager, and email/password authentication.
* **Firebase App Check**: Protects cloud API gateways and AI backends from abuse, unauthorized scrapers, and API key extraction.
* **Cloud Storage**: Secure, private Google Cloud Storage buckets for high-resolution crop imagery.

### 2.5 Artificial Intelligence: Google Gemini 3.5 & Preview Series
* **Multimodal Visual Diagnostics**: Employs `gemini-3.5-flash` for high-throughput leaf disease and pest diagnosis.
* **Deep Pathological Reasoning**: Employs `gemini-3.1-pro-preview` for complex multi-symptom differential diagnosis.
* **Rapid Field Scouting**: Employs `gemini-3.1-flash-lite-preview` for low-latency question answering.
* **Resilient Heuristic Fallback**: An on-device clinical heuristic engine automatically takes over if internet connectivity or API keys are unavailable.

---

## 3. Modularization Strategy for International Scalability

To support expansion from Nepal to India, Bangladesh, and beyond without rewriting core business logic, the application adopts a **Feature-and-Core Modularization Architecture**:

```
Krishi AI Modular Graph
│
├── :app (Application entry, Navigation Compose graph, Dependency Injection)
│
├── :core
│   ├── :core:model         (Universal domain models: Crop, Condition, Severity, Farm, User)
│   ├── :core:database      (Room Database, Entities, Migrations, Type Converters)
│   ├── :core:network       (OkHttp, Retrofit API interfaces, Error handling)
│   ├── :core:ai            (Gemini REST client, Image Validator, Prompt Templates)
│   ├── :core:designsystem  (M3 Color, Typography, Shapes, Common UI Components)
│   └── :core:localization  (Dynamic locale engine, Plurals, Font family providers)
│
├── :feature
│   ├── :feature:scan       (Camera capture, photo picker, image pre-validation, result screen)
│   ├── :feature:farm       (Farm creation, plot monitoring, growth timelines, reminders)
│   ├── :feature:chat       (Krishi Sathi multi-turn conversational AI with model toggles)
│   ├── :feature:weather    (Agrometeorological alerts, disease risk indices)
│   ├── :feature:knowledge  (Agronomic article hub, offline cache, search)
│   ├── :feature:calendar   (Regional crop calendars based on agro-ecological zones)
│   └── :feature:expert     (Agronomist directory, consult requests, appointment dispatch)
│
└── :country-packs
    ├── :pack:nepal         (7 provinces, 77 districts, Ropani/Kattha units, NARC directory)
    ├── :pack:india         (States, Bigha/Acre units, ICAR/KVK directory, Hindi/Bengali)
    └── :pack:global        (Metric hectares, FAO agronomy taxonomy)
```

### Country-Pack Plugin Architecture
Each country-pack implements a standard interface:
```kotlin
interface CountryPack {
    val countryCode: String
    val supportedLanguages: List<AppLanguage>
    val landMeasurementUnits: List<String>
    val administrativeDivisions: Map<String, List<String>>
    val agriculturalExtensionContacts: List<ExpertProfile>
    val regionalAgroZones: List<AgroEcologicalZone>
}
```

---

## 4. AI Diagnostic & Safety Framework

### 4.1 Strict Ethical Guardrails
1. **Never Present Predictions as Absolute**: Every result displays an `AI-Estimated Diagnosis` banner and a prominent disclaimer stating that serious crop diseases must be confirmed by local agriculture extension officers.
2. **Safe Chemical Management**: Chemical control recommendations are location-aware and strictly mandate adherence to locally registered pesticide labels and national directives.
3. **Low-Confidence Handling**: When visual confidence falls below 65% or symptoms overlap between biotic and abiotic stresses, the system prompts the farmer to take another angle or consult an expert.

### 4.2 Continuous Model Improvement Pipeline (Human-in-the-Loop)
```
Farmer Submits Leaf Photo
       │
       ▼
AI Model Prediction (gemini-3.5-flash)
       │
       ▼
Farmer Diagnosis Feedback ("Was this helpful? Yes/No" + Error Classification)
       │
       ▼
Flagged Anonymized Sample -> Agricultural Knowledge Center / NARC Extension Queue
       │
       ▼
Certified Agronomist Label Verification
       │
       ▼
Validated Benchmark Dataset
       │
       ▼
Future Model Fine-Tuning & Evaluation
```

---

## 5. Security & Privacy Architecture
* **API Key Isolation**: Secrets injected strictly through environment variables and accessed via `BuildConfig`.
* **Zero-Permission Media Selection**: Zero usage of broad storage permissions (`READ_EXTERNAL_STORAGE`); uses system Android Photo Picker.
* **Privacy-Conscious Telemetry**: Collects only operational diagnostic metrics (`scan_started`, `scan_completed`, `crop_added`) without logging private coordinates or personal contact details.
