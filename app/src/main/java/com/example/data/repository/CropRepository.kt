package com.example.data.repository

import com.example.data.model.Crop
import com.example.data.model.ExpertProfile
import com.example.data.model.KnowledgeArticle

object CropRepository {

    val initialCrops = listOf(
        Crop(
            id = "rice",
            name = "Rice",
            localNameNepali = "धान (Dhan)",
            localNameHindi = "चावल (Dhan)",
            scientificName = "Oryza sativa",
            category = "Cereal",
            iconEmoji = "🌾",
            description = "Staple food crop of Nepal, cultivated across Terai and mid-hills during monsoon season.",
            growingSeason = "Asar to Kartik (June - November)",
            commonDiseases = listOf("Bacterial Leaf Blight", "Rice Blast", "Brown Spot", "Sheath Blight"),
            commonPests = listOf("Stem Borer", "Brown Planthopper", "Rice Hispa", "Gundhi Bug"),
            nutrientProblems = listOf("Nitrogen deficiency", "Zinc deficiency (Khaira disease)", "Phosphorus deficiency")
        ),
        Crop(
            id = "tomato",
            name = "Tomato",
            localNameNepali = "गोलभेंडा (Golbheda)",
            localNameHindi = "टमाटर (Tamatar)",
            scientificName = "Solanum lycopersicum",
            category = "Vegetable",
            iconEmoji = "🍅",
            description = "High-value commercial cash crop cultivated in polyhouses and open fields in Nepal.",
            growingSeason = "Year-round in tunnels; spring/autumn in open fields",
            commonDiseases = listOf("Early Blight", "Late Blight", "Bacterial Wilt", "Tomato Leaf Curl Virus (ToLCV)"),
            commonPests = listOf("Tuta absoluta (Leaf miner)", "Whiteflies", "Fruit Borer", "Aphids"),
            nutrientProblems = listOf("Calcium deficiency (Blossom End Rot)", "Potassium deficiency", "Magnesium deficiency")
        ),
        Crop(
            id = "maize",
            name = "Maize / Corn",
            localNameNepali = "मकै (Makkai)",
            localNameHindi = "मक्का (Makka)",
            scientificName = "Zea mays",
            category = "Cereal",
            iconEmoji = "🌽",
            description = "Primary staple crop in the mid-hills of Nepal, crucial for human food and livestock feed.",
            growingSeason = "Chaitra to Shrawan (March - August)",
            commonDiseases = listOf("Turcicum Leaf Blight", "Maydis Leaf Blight", "Common Rust", "Stalk Rot"),
            commonPests = listOf("Fall Armyworm (Spodoptera frugiperda)", "Stem Borer", "Cutworm"),
            nutrientProblems = listOf("Nitrogen deficiency (V-shaped leaf yellowing)", "Phosphorus deficiency (purple leaves)")
        ),
        Crop(
            id = "potato",
            name = "Potato",
            localNameNepali = "आलु (Aalu)",
            localNameHindi = "आलू (Aloo)",
            scientificName = "Solanum tuberosum",
            category = "Tuber",
            iconEmoji = "🥔",
            description = "Important staple and vegetable in high mountains, hills, and winter Terai.",
            growingSeason = "Winter in Terai; Summer in High Hills",
            commonDiseases = listOf("Late Blight (Phytophthora)", "Early Blight", "Black Scurf", "Common Scab"),
            commonPests = listOf("Potato Tuber Moth", "Aphids", "Cutworms"),
            nutrientProblems = listOf("Potassium deficiency", "Nitrogen deficiency", "Phosphorus deficiency")
        ),
        Crop(
            id = "wheat",
            name = "Wheat",
            localNameNepali = "गहुँ (Gahu)",
            localNameHindi = "गेहूं (Gehu)",
            scientificName = "Triticum aestivum",
            category = "Cereal",
            iconEmoji = "🌱",
            description = "Major winter cereal crop across the Terai and inner Terai regions of Nepal.",
            growingSeason = "Mangsir to Chaitra (November - April)",
            commonDiseases = listOf("Yellow/Stripe Rust", "Brown/Leaf Rust", "Loose Smut", "Powdery Mildew"),
            commonPests = listOf("Aphids", "Armyworm", "Termites"),
            nutrientProblems = listOf("Nitrogen deficiency", "Sulfur deficiency", "Zinc deficiency")
        ),
        Crop(
            id = "chilli",
            name = "Chilli / Pepper",
            localNameNepali = "खुर्सानी (Khursani)",
            localNameHindi = "मिर्च (Mirch)",
            scientificName = "Capsicum annuum",
            category = "Spice / Vegetable",
            iconEmoji = "🌶️",
            description = "Widely consumed spice crop across all eco-zones, highly susceptible to viral curl.",
            growingSeason = "Spring to Autumn (March - October)",
            commonDiseases = listOf("Anthracnose / Fruit Rot", "Chilli Leaf Curl Virus", "Damping Off", "Powdery Mildew"),
            commonPests = listOf("Thrips (Murda disease)", "Mites", "Aphids", "Fruit Borer"),
            nutrientProblems = listOf("Calcium deficiency", "Magnesium deficiency", "Nitrogen deficiency")
        ),
        Crop(
            id = "cauliflower",
            name = "Cauliflower",
            localNameNepali = "काउली (Kauli)",
            localNameHindi = "फूलगोभी (Phoolgobhi)",
            scientificName = "Brassica oleracea var. botrytis",
            category = "Cole Crop",
            iconEmoji = "🥦",
            description = "Major winter commercial vegetable in Nepal with great market demand.",
            growingSeason = "Bhadra to Magh (September - February)",
            commonDiseases = listOf("Black Rot", "Downy Mildew", "Clubroot", "Alternaria Leaf Spot"),
            commonPests = listOf("Diamondback Moth (DBM)", "Tobacco Caterpillar", "Cabbage Aphid"),
            nutrientProblems = listOf("Boron deficiency (Browning/Hollow stem)", "Molybdenum deficiency (Whiptail)")
        ),
        Crop(
            id = "cabbage",
            name = "Cabbage",
            localNameNepali = "बन्दागोभी (Banda)",
            localNameHindi = "पत्तागोभी (Pattagobhi)",
            scientificName = "Brassica oleracea var. capitata",
            category = "Cole Crop",
            iconEmoji = "🥬",
            description = "Resilient cole crop widely cultivated in peri-urban farms in Nepal.",
            growingSeason = "Autumn to Spring",
            commonDiseases = listOf("Black Rot", "Soft Rot", "Yellows"),
            commonPests = listOf("Diamondback Moth", "Cabbage Butterfly Caterpillar", "Aphids"),
            nutrientProblems = listOf("Boron deficiency", "Calcium deficiency (Tipburn)")
        ),
        Crop(
            id = "cucumber",
            name = "Cucumber",
            localNameNepali = "काँक्रो (Kankro)",
            localNameHindi = "खीरा (Kheera)",
            scientificName = "Cucumis sativus",
            category = "Cucurbit",
            iconEmoji = "🥒",
            description = "Popular warm season crop, heavily grown in Bhaktapur and Terai belt.",
            growingSeason = "Falgun to Shrawan (February - July)",
            commonDiseases = listOf("Downy Mildew", "Powdery Mildew", "Cucumber Mosaic Virus", "Anthracnose"),
            commonPests = listOf("Red Pumpkin Beetle", "Fruit Fly (Bactrocera cucurbitae)", "Aphids"),
            nutrientProblems = listOf("Magnesium deficiency", "Potassium deficiency")
        ),
        Crop(
            id = "brinjal",
            name = "Brinjal / Eggplant",
            localNameNepali = "भन्टा (Bhanta)",
            localNameHindi = "बैंगन (Baingan)",
            scientificName = "Solanum melongena",
            category = "Vegetable",
            iconEmoji = "🍆",
            description = "Hardy solanaceous crop popular in Terai and low-hill valleys.",
            growingSeason = "Year-round in sub-tropical zones",
            commonDiseases = listOf("Phomopsis Blight", "Bacterial Wilt", "Little Leaf of Brinjal"),
            commonPests = listOf("Shoot and Fruit Borer (Leucinodes orbonalis)", "Epilachna Beetle", "Jassids"),
            nutrientProblems = listOf("Nitrogen deficiency", "Iron chlorosis")
        ),
        Crop(
            id = "mustard",
            name = "Mustard / Rapeseed",
            localNameNepali = "तोरी (Tori)",
            localNameHindi = "सरसों (Sarson)",
            scientificName = "Brassica campestris",
            category = "Oilseed",
            iconEmoji = "🌼",
            description = "Primary cooking oil crop of Nepal, famously grown in Chitwan and Dang valleys.",
            growingSeason = "Kartik to Falgun (October - March)",
            commonDiseases = listOf("Alternaria Blight", "White Rust", "Downy Mildew"),
            commonPests = listOf("Mustard Aphid (Lipaphis erysimi)", "Sawfly"),
            nutrientProblems = listOf("Sulfur deficiency", "Zinc deficiency")
        ),
        Crop(
            id = "apple",
            name = "Apple",
            localNameNepali = "स्याउ (Syau)",
            localNameHindi = "सेब (Seb)",
            scientificName = "Malus domestica",
            category = "Fruit",
            iconEmoji = "🍎",
            description = "High mountain fruit of Mustang, Jumla, and Manang districts of Nepal.",
            growingSeason = "Perennial, harvested Bhadra-Asoj",
            commonDiseases = listOf("Apple Scab", "Powdery Mildew", "Crown Gall"),
            commonPests = listOf("Woolly Apple Aphid", "San Jose Scale", "Mites"),
            nutrientProblems = listOf("Calcium deficiency (Bitter Pit)", "Iron deficiency")
        ),
        Crop(
            id = "orange",
            name = "Orange / Mandarin",
            localNameNepali = "सुन्तला (Suntala)",
            localNameHindi = "संतरा (Santara)",
            scientificName = "Citrus reticulata",
            category = "Fruit",
            iconEmoji = "🍊",
            description = "Signature citrus fruit of mid-hills like Syangja, Dhankuta, and Gorkha.",
            growingSeason = "Perennial, harvest Mangsir-Poush",
            commonDiseases = listOf("Citrus Greening (HLB)", "Citrus Canker", "Phytophthora Gummosis"),
            commonPests = listOf("Citrus Psylla", "Leaf Miner", "Fruit Fly"),
            nutrientProblems = listOf("Zinc deficiency", "Iron chlorosis", "Magnesium deficiency")
        ),
        Crop(
            id = "banana",
            name = "Banana",
            localNameNepali = "केरा (Kera)",
            localNameHindi = "केला (Kela)",
            scientificName = "Musa acuminata",
            category = "Fruit",
            iconEmoji = "🍌",
            description = "Commercial tropical fruit grown in Kailali, Kanchanpur, Chitwan and Nawalparasi.",
            growingSeason = "Perennial",
            commonDiseases = listOf("Panama Wilt (Fusarium)", "Sigatoka Leaf Spot", "Banana Bunchy Top Virus"),
            commonPests = listOf("Banana Rhizome Weevil", "Pseudostem Weevil", "Aphids"),
            nutrientProblems = listOf("Potassium deficiency", "Nitrogen deficiency")
        )
    )

    val knowledgeArticles = listOf(
        KnowledgeArticle(
            id = "art_late_blight",
            title = "Managing Late Blight in Potato and Tomato",
            nepaliTitle = "आलु र गोलभेंडामा डडुवा (Late Blight) रोगको रोकथाम",
            category = "Plant Diseases",
            summary = "Early detection symptoms, weather triggers, organic sanitation, and safe management protocols for Phytophthora infestans.",
            nepaliSummary = "डडुवा रोग लाग्ने कारण, मौसम अनुकूलता र स्थानीय कृषि प्राविधिकको सल्लाह अनुसार सुरक्षित नियन्त्रण विधि।",
            content = """Late Blight caused by Phytophthora infestans is one of the most destructive diseases affecting Solanaceous crops in Nepal, particularly during cool, misty, humid weather in winter and monsoon.

Symptoms:
- Water-soaked dark brown to purplish lesions on leaf margins and tips.
- White fungal downy growth visible on leaf undersides in high humidity.
- Rapid blighting where whole foliage collapses and emits a rotting smell.
- Brown hard rot on tomato fruits and potato tubers.

Preventive Management:
1. Use certified disease-free seed tubers and resistant varieties like CIP clones.
2. Maintain wide plant spacing to improve air circulation.
3. Avoid overhead sprinkler irrigation during overcast foggy days.
4. Regularly scout fields early morning.

Safe Management:
- At first visible speck, remove and burn infected plant parts.
- Consult local municipal agriculture officer (कृषि शाखा) for approved preventive copper-based sprays or bio-fungicides. Always adhere strictly to label safety periods (PHI).""",
            nepaliContent = """डडुवा रोग नेपालका पहाडी तथा तराई क्षेत्रमा आलु र गोलभेंडा बालीलाई ठूलो नोक्सानी पुर्याउने ढुसीजन्य रोग हो। चिसो र आद्र मौसममा यसको प्रकोप तीव्र हुन्छ।

लक्षणहरू:
- पातको किनारमा पानीले भिजेका जस्ता खैरो वा कालो दाग देखिने।
- ओसिलो बिहानीपख पातको पछाडि सेतो ढुसीको जालो देखिने।
- बोटका पातहरू ओइलाएर सुक्ने र कुहिएको गन्ध आउने।

रोकथामका उपाय:
१. रोगमुक्त बीउ वा बेर्ना मात्र प्रयोग गर्ने।
२. बोटहरू बीच उचित दूरी कायम गरी हावा छिर्ने बनाउने।
३. बिहानै बालीको निरन्तर अनुगमन गर्ने।
४. लक्षण देखिनासाथ स्थानीय कृषि प्राविधिकसँग परामर्श गरी स्वीकृत ढुसीनासक सुरक्षित तरिकाले प्रयोग गर्ने।""",
            crops = listOf("Potato", "Tomato"),
            readTimeMinutes = 5
        ),
        KnowledgeArticle(
            id = "art_fall_armyworm",
            title = "Integrated Management of Fall Armyworm in Maize",
            nepaliTitle = "मकैमा अमेरिकन फौजी कीरा (Fall Armyworm) को एकीकृत व्यवस्थापन",
            category = "Integrated Pest Management",
            summary = "How to identify FAW early, push-pull strategies, bio-pesticides, and safe pest control in Nepal.",
            nepaliSummary = "फौजी कीराको पहिचान, जैविक नियन्त्रण, निमजन्य विषादी र समुदाय स्तरको नियन्त्रण अभियान।",
            content = """Fall Armyworm (Spodoptera frugiperda) has severely challenged maize growers across Nepal since 2019. Early whorl monitoring is critical before larvae burrow deep into the maize cob.

Key Identifiers:
- Pinholes and windowpane feeding on young whorl leaves.
- Heavy sawdust-like fecal frass in the central whorl.
- Inverted 'Y' shape mark on the head of older caterpillars and four square dots on the eighth segment.

Integrated Control:
1. Deep summer ploughing to expose pupae to birds and sun.
2. Intercrop maize with desmodium (Push) and border with Napier grass (Pull).
3. Drop a mixture of fine sand and wood ash (1:1) into whorls to physically deter larvae.
4. Apply neem-based formulations (Azadirachtin 1500 ppm @ 3-5 ml/L) early morning or late evening.
5. In severe infestations, seek guidance from the Plant Protection Officer at the Agriculture Knowledge Center (कृषि ज्ञान केन्द्र).""",
            nepaliContent = """अमेरिकन फौजी कीराले मकैको पात र गुभो खाएर अत्यधिक क्षति गर्दछ। यसको समयमै नियन्त्रण गर्न आवश्यक छ।

पहिचान:
- पातमा साना-साना प्वालहरू र जाली जस्तो देखिनु।
- मकैको गुभोमा काठको भुस जस्तो दिशा भरिनु।
- कीराको टाउकोमा उल्टो 'Y' आकारको चिह्न हुनु।

व्यवस्थापन विधि:
१. मकै रोप्नु अघि खेतलाई गहिरो जोत्ने।
२. गुभोभित्र मसिनो बालुवा र खरानी मिसाएर हाल्ने।
३. नीमको पात वा तेलबाट बनेको जैविक विषादी छर्कने।
४. थप समस्या भएमा नजिकको कृषि ज्ञान केन्द्रमा सम्पर्क गर्ने।""",
            crops = listOf("Maize"),
            readTimeMinutes = 6
        ),
        KnowledgeArticle(
            id = "art_soil_fertility",
            title = "Soil Health & Balanced NPK Fertilization in Nepal",
            nepaliTitle = "माटोको उर्वराशक्ति र सन्तुलित मलखाद व्यवस्थापन",
            category = "Soil Fertility",
            summary = "Understanding soil testing, organic manure incorporation, liming acidic soils, and micro-nutrient balance.",
            nepaliSummary = "माटो परीक्षण, कम्पोस्ट मल, कृषि चुनको प्रयोग र सूक्ष्म तत्वहरूको महत्त्व।",
            content = """Continuous use of urea without organic manure has acidified extensive agricultural soils across Nepal's mid-hills and Terai.

Restoring Soil Health:
- Apply 10-15 tons of well-decomposed Farmyard Manure (FYM) or Vermicompost per hectare.
- Conduct annual soil pH testing. If pH is below 5.5, apply agricultural lime (कृषि चुन) according to recommended rates.
- Practice green manuring with Dhaincha (Sesbania) before paddy transplantation.
- Incorporate bio-fertilizers like Rhizobium for pulses and Trichoderma for root health.""",
            nepaliContent = """रसायनिक मलको असन्तुलित प्रयोगले गर्दा माटोमा अम्लियपन बढी उर्वराशक्ति घट्दै गएको छ।

सुझावहरू:
- पर्याप्त पाकेको गोठेमल वा गँड्यौला मल प्रयोग गर्ने।
- माटोको pH परीक्षण गरी आवश्यकता अनुसार कृषि चुन हाल्ने।
- हरियो मलको रूपमा ढैंचा वा सनई प्रयोग गर्ने।
- दलहन बाली समावेश गरेर बाली चक्र अपनाउने।""",
            crops = listOf("Rice", "Maize", "Wheat", "Tomato", "Cauliflower"),
            readTimeMinutes = 4
        )
    )

    val expertsList = listOf(
        ExpertProfile(
            id = "exp_1",
            name = "Dr. Suresh Sharma",
            specialization = "Plant Pathology & Fungal Diseases",
            affiliation = "Nepal Agricultural Research Council (NARC)",
            location = "Khumaltar, Lalitpur",
            languages = listOf("Nepali", "English", "Newari"),
            rating = 4.9,
            experienceYears = 14,
            isAvailable = true,
            consultationFee = "Free Agricultural Advisory"
        ),
        ExpertProfile(
            id = "exp_2",
            name = "Er. Manisha Thapa",
            specialization = "Entomology & Integrated Pest Management",
            affiliation = "Agriculture Knowledge Center, Chitwan",
            location = "Bharatpur, Chitwan",
            languages = listOf("Nepali", "English", "Tharu"),
            rating = 4.8,
            experienceYears = 9,
            isAvailable = true,
            consultationFee = "Free Agricultural Advisory"
        ),
        ExpertProfile(
            id = "exp_3",
            name = "Bikash Adhikari, MSc Ag",
            specialization = "Horticulture & Commercial Polyhouse Farming",
            affiliation = "Horticulture Development Center",
            location = "Pokhara, Gandaki",
            languages = listOf("Nepali", "English"),
            rating = 4.7,
            experienceYears = 7,
            isAvailable = true,
            consultationFee = "Free Agricultural Advisory"
        ),
        ExpertProfile(
            id = "exp_4",
            name = "Dr. Rabindra Yadav",
            specialization = "Soil Fertility & Plant Nutrition Specialist",
            affiliation = "Faculty of Agriculture, AFU Rampur",
            location = "Rampur, Chitwan",
            languages = listOf("Nepali", "Bhojpuri", "Maithili", "English"),
            rating = 4.9,
            experienceYears = 16,
            isAvailable = false,
            consultationFee = "Free Agricultural Advisory"
        )
    )

    val nepalProvinces = listOf(
        "Koshi Province",
        "Madhesh Province",
        "Bagmati Province",
        "Gandaki Province",
        "Lumbini Province",
        "Karnali Province",
        "Sudurpashchim Province"
    )

    val nepalDistrictsByProvince = mapOf(
        "Koshi Province" to listOf("Jhapa", "Morang", "Sunsari", "Ilam", "Udayapur", "Dhankuta"),
        "Madhesh Province" to listOf("Dhanusha", "Parsa", "Bara", "Rautahat", "Sarlahi", "Siraha", "Saptari", "Mahottari"),
        "Bagmati Province" to listOf("Chitwan", "Kavrepalanchok", "Kathmandu", "Lalitpur", "Bhaktapur", "Makwanpur", "Dhading", "Nuwakot"),
        "Gandaki Province" to listOf("Kaski", "Tanahun", "Gorkha", "Syangja", "Nawalpur", "Lamjung", "Baglung"),
        "Lumbini Province" to listOf("Rupandehi", "Kapilvastu", "Dang", "Banke", "Bardiya", "Palpa"),
        "Karnali Province" to listOf("Surkhet", "Jumla", "Dailekh", "Salyan", "Kalikot"),
        "Sudurpashchim Province" to listOf("Kailali", "Kanchanpur", "Dadeldhura", "Doti", "Baitadi")
    )
}
