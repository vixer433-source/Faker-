package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.FlashcardEntity
import com.example.data.local.QuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AnalysisResult(
    val summary: String,
    val questions: List<QuestionEntity>,
    val flashcards: List<FlashcardEntity>
)

object StudyAnalyzer {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    suspend fun analyzeDocument(
        fileId: Long,
        fileName: String,
        text: String,
        imageBase64: String? = null
    ): AnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val aiResult = callGeminiForAnalysis(fileId, fileName, text, imageBase64, apiKey)
                if (aiResult != null && aiResult.questions.isNotEmpty()) {
                    return@withContext aiResult
                }
            } catch (_: Exception) {}
        }

        // Local smart fallback analyzer
        return@withContext generateLocalAnalysis(fileId, fileName, text)
    }

    private fun callGeminiForAnalysis(
        fileId: Long,
        fileName: String,
        text: String,
        imageBase64: String?,
        apiKey: String
    ): AnalysisResult? {
        val prompt = """
            أنت خبير تعليمي في تطبيق 'FAKER?' للمراجعة الذكية والتذكر الفعّال.
            حلل المحتوى الدراسي التالي للملف '$fileName' وقم بإنشاء:
            1. ملخص مركز وشامل (summary).
            2. قائمة من 5 إلى 10 أسئلة اختيار من متعدد تفاعلية (questions) لاختبار التذكر الفعّال.
            3. قائمة من 5 إلى 8 بطاقات استذكار flashcards للمفاهيم الأساسية.
            
            أجب بصيغة JSON فقط بهذا الشكل الدقيق:
            {
              "summary": "ملخص المادة الدراسية...",
              "questions": [
                {
                  "questionText": "نص السؤال؟",
                  "optionA": "الخيار 1",
                  "optionB": "الخيار 2",
                  "optionC": "الخيار 3",
                  "optionD": "الخيار 4",
                  "correctAnswerIndex": 0,
                  "explanation": "شرح الإجابة...",
                  "conceptTag": "اسم المفهوم",
                  "difficulty": "Medium"
                }
              ],
              "flashcards": [
                {
                  "front": "المفهوم أو السؤال",
                  "back": "التعريف أو الشرح المركز",
                  "conceptTag": "اسم المفهوم"
                }
              ]
            }
            
            نص المحتوى:
            ${text.take(8000)}
        """.trimIndent()

        val jsonRequest = JSONObject()
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()

        val textPart = JSONObject()
        textPart.put("text", prompt)
        partsArray.put(textPart)

        if (imageBase64 != null) {
            val imgPart = JSONObject()
            val inlineData = JSONObject()
            inlineData.put("mimeType", "image/jpeg")
            inlineData.put("data", imageBase64)
            imgPart.put("inlineData", inlineData)
            partsArray.put(imgPart)
        }

        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        jsonRequest.put("contents", contentsArray)

        // Request JSON mode
        val genConfig = JSONObject()
        val respFormat = JSONObject()
        respFormat.put("mimeType", "application/json")
        genConfig.put("responseFormat", respFormat)
        jsonRequest.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonRequest.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        val respJson = JSONObject(responseBody)
        val candidates = respJson.optJSONArray("candidates") ?: return null
        val firstCandidate = candidates.optJSONObject(0) ?: return null
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        val rawAiText = parts.optJSONObject(0)?.optString("text") ?: return null

        // Parse generated JSON
        val cleanedJson = rawAiText.replace("```json", "").replace("```", "").trim()
        val parsed = JSONObject(cleanedJson)

        val summary = parsed.optString("summary", "ملخص المحتوى الدراسي لملف $fileName")
        val questionsList = mutableListOf<QuestionEntity>()
        val qArray = parsed.optJSONArray("questions")
        if (qArray != null) {
            for (i in 0 until qArray.length()) {
                val q = qArray.optJSONObject(i) ?: continue
                questionsList.add(
                    QuestionEntity(
                        fileId = fileId,
                        questionText = q.optString("questionText", "سؤال حول $fileName"),
                        optionA = q.optString("optionA", "الخيار الأول"),
                        optionB = q.optString("optionB", "الخيار الثاني"),
                        optionC = q.optString("optionC", "الخيار الثالث"),
                        optionD = q.optString("optionD", "الخيار الرابع"),
                        correctAnswerIndex = q.optInt("correctAnswerIndex", 0).coerceIn(0, 3),
                        explanation = q.optString("explanation", "إجابة مستخرجة من محتوى الملف"),
                        conceptTag = q.optString("conceptTag", "مفاهيم أساسية"),
                        difficulty = q.optString("difficulty", "Medium")
                    )
                )
            }
        }

        val flashcardsList = mutableListOf<FlashcardEntity>()
        val fArray = parsed.optJSONArray("flashcards")
        if (fArray != null) {
            for (i in 0 until fArray.length()) {
                val f = fArray.optJSONObject(i) ?: continue
                flashcardsList.add(
                    FlashcardEntity(
                        fileId = fileId,
                        front = f.optString("front", "مفهوم"),
                        back = f.optString("back", "شرح"),
                        conceptTag = f.optString("conceptTag", "مفاهيم")
                    )
                )
            }
        }

        return AnalysisResult(summary, questionsList, flashcardsList)
    }

    private fun generateLocalAnalysis(fileId: Long, fileName: String, text: String): AnalysisResult {
        val lines = text.split("\n")
            .map { it.trim() }
            .filter { it.length > 5 }

        val paragraphs = if (lines.isNotEmpty()) lines else listOf(
            "تم استيراد المستند بنجاح ويحتوي على مفاهيم وملاحظات دراسية هامة.",
            "المراجعة الدورية والتذكر الفعّال يسهمان في تثبيت المعلومات في الذاكرة طويلة المدى.",
            "التطبيق يولد أسئلة لاختبار الاستيعاب ومتابعة نقاط القوة ونقاط الضعف."
        )

        val cleanTitle = fileName.substringBeforeLast(".")
        val summary = if (paragraphs.size > 2) {
            "يغطي ملف '$cleanTitle' مواضيع هامة تتضمن: ${paragraphs.take(3).joinToString(" - ")}"
        } else {
            "ملخص دراسي لملف '$cleanTitle' مع تركيز على المفاهيم الجوهرية والتعاريف والأسئلة المفتاحية."
        }

        val questions = mutableListOf<QuestionEntity>()
        val flashcards = mutableListOf<FlashcardEntity>()

        // Generate smart questions from text paragraphs
        val samplePool = paragraphs.take(12)
        samplePool.forEachIndexed { index, sentence ->
            val words = sentence.split(" ").filter { it.length > 3 }
            val keyWord = words.getOrNull(words.size / 2) ?: "المفهوم"
            val conceptName = words.take(2).joinToString(" ").ifEmpty { "مفهوم ${index + 1}" }

            val questionText = when (index % 4) {
                0 -> "ما هي النقطة الأساسية المتعلقة بـ '$conceptName' في المحتوى؟"
                1 -> "وفقاً للنص، أي من العبارات التالية تصف بدقة: '$keyWord'؟"
                2 -> "ما الفائدة أو النتيجة المترتبة على: '$conceptName'؟"
                else -> "كيف يوضح المستند علاقة '$conceptName' بباقي المفاهيم؟"
            }

            val correctAns = sentence.take(80) + if (sentence.length > 80) "..." else ""
            val distractor1 = "يرتبط بعامل خارجي غير مذكور بالتفصيل في النص الرئيسي."
            val distractor2 = "يعد إجراءً ثانوياً لا يؤثر على النتائج النهائية."
            val distractor3 = "يتطلب شروطاً مغايرة تماماً للسياق المطروح."

            val options = listOf(correctAns, distractor1, distractor2, distractor3).shuffled()
            val correctIdx = options.indexOf(correctAns).coerceAtLeast(0)

            questions.add(
                QuestionEntity(
                    fileId = fileId,
                    questionText = questionText,
                    optionA = options[0],
                    optionB = options[1],
                    optionC = options[2],
                    optionD = options[3],
                    correctAnswerIndex = correctIdx,
                    explanation = "مستنتج مباشرة من الملاحظة: $sentence",
                    conceptTag = conceptName,
                    difficulty = if (index % 3 == 0) "Hard" else if (index % 2 == 0) "Medium" else "Easy"
                )
            )

            flashcards.add(
                FlashcardEntity(
                    fileId = fileId,
                    front = "ما هو المعنى أو الفكرة الرئيسية لـ '$conceptName'؟",
                    back = sentence,
                    conceptTag = conceptName
                )
            )
        }

        if (questions.isEmpty()) {
            questions.add(
                QuestionEntity(
                    fileId = fileId,
                    questionText = "ما هو الهدف الأساسي من دراسة ملف '$cleanTitle'؟",
                    optionA = "استيعاب ومراجعة كافة الأفكار والمفاهيم الرئيسية",
                    optionB = "حفظ العناوين دون فهم المحتوى",
                    optionC = "تأجيل المذاكرة لوقت لاحق",
                    optionD = "قراءة عابرة دون اختبار التذكر",
                    correctAnswerIndex = 0,
                    explanation = "المراجعة الذكية والتذكر الفعال هما أساس تثبيت المعلومة.",
                    conceptTag = cleanTitle,
                    difficulty = "Easy"
                )
            )
            flashcards.add(
                FlashcardEntity(
                    fileId = fileId,
                    front = cleanTitle,
                    back = "ملف دراسي تم استيراده للمراجعة والتذكر الفعّال في FAKER?",
                    conceptTag = cleanTitle
                )
            )
        }

        return AnalysisResult(summary, questions, flashcards)
    }
}
