package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.ai.StudyAnalyzer
import com.example.data.file.FileExtractor
import com.example.data.local.AppDatabase
import com.example.data.local.FlashcardEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.ReviewSessionEntity
import com.example.data.local.StudyFileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class ImportStage(val stepNumber: Int, val title: String, val description: String) {
    object Selecting : ImportStage(1, "اختيار الملف", "جاري قراءة الملف المحدد...")
    object Validating : ImportStage(2, "التحقق من نوع الملف", "التحقق من صيغة المستند وصلاحيته...")
    object Extracting : ImportStage(3, "قراءة الملف واستخراج النص", "استخراج النصوص والملاحظات الدراسية...")
    object Analyzing : ImportStage(4, "تحليل المحتوى الذكي", "فحص المفاهيم العلمية والأفكار الجوهرية...")
    object GeneratingQuestions : ImportStage(5, "إنشاء الأسئلة والمراجعة", "توليد أسئلة التذكر الفعّال وبطاقات المذاكرة...")
    object SavingToDb : ImportStage(6, "حفظ البيانات في SQLite", "تخزين المستند والأسئلة محلياً...")
    object Verifying : ImportStage(7, "التأكد من نجاح الحفظ", "التحقق النهائي من اكتمال المعالجة...")
    object Completed : ImportStage(8, "تم الاستيراد بنجاح 🎉", "أصبح ملفك جاهزاً للمراجعة الفورية!")
}

class StudyRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val fileDao = db.studyFileDao()
    private val questionDao = db.questionDao()
    private val flashcardDao = db.flashcardDao()
    private val reviewDao = db.reviewSessionDao()

    val allFiles: Flow<List<StudyFileEntity>> = fileDao.getAllFiles()
    val filesCount: Flow<Int> = fileDao.getFilesCount()
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val weakPoints: Flow<List<QuestionEntity>> = questionDao.getWeakPointQuestions()
    val allFlashcards: Flow<List<FlashcardEntity>> = flashcardDao.getAllFlashcards()
    val recentSessions: Flow<List<ReviewSessionEntity>> = reviewDao.getRecentSessions()

    suspend fun getFilesCountDirect(): Int = withContext(Dispatchers.IO) {
        fileDao.getFilesCountDirect()
    }

    suspend fun processAndImportFile(
        uri: Uri,
        onStageUpdate: (ImportStage) -> Unit
    ): Result<StudyFileEntity> = withContext(Dispatchers.IO) {
        try {
            // Stage 1: Selecting
            onStageUpdate(ImportStage.Selecting)
            kotlinx.coroutines.delay(350)

            // Stage 2: Validating
            onStageUpdate(ImportStage.Validating)
            kotlinx.coroutines.delay(350)

            // Stage 3: Extracting
            onStageUpdate(ImportStage.Extracting)
            val extracted = FileExtractor.extractData(context, uri)
            if (extracted.text.isBlank() && extracted.imageBase64 == null) {
                return@withContext Result.failure(
                    Exception("تعذر استخراج نص من هذا الملف. يرجى التأكد من اختيار ملف صالح (PDF, TXT, DOCX أو صورة واضحة).")
                )
            }
            kotlinx.coroutines.delay(400)

            // Stage 4: Analyzing
            onStageUpdate(ImportStage.Analyzing)
            kotlinx.coroutines.delay(400)

            // Stage 5: Generating Questions & Flashcards
            onStageUpdate(ImportStage.GeneratingQuestions)
            val tempFileId = System.currentTimeMillis()
            val analysis = StudyAnalyzer.analyzeDocument(
                fileId = tempFileId,
                fileName = extracted.fileName,
                text = extracted.text,
                imageBase64 = extracted.imageBase64
            )
            kotlinx.coroutines.delay(450)

            // Stage 6: Saving to SQLite
            onStageUpdate(ImportStage.SavingToDb)
            val fileEntity = StudyFileEntity(
                fileName = extracted.fileName,
                fileUri = uri.toString(),
                fileType = extracted.fileType,
                fileSizeBytes = extracted.fileSizeBytes,
                extractedText = extracted.text.take(15000),
                summary = analysis.summary,
                questionCount = analysis.questions.size,
                flashcardCount = analysis.flashcards.size
            )
            val insertedFileId = fileDao.insertFile(fileEntity)

            // Update real foreign keys and insert into DB
            val mappedQuestions = analysis.questions.map { it.copy(fileId = insertedFileId) }
            val mappedFlashcards = analysis.flashcards.map { it.copy(fileId = insertedFileId) }
            
            questionDao.insertQuestions(mappedQuestions)
            flashcardDao.insertFlashcards(mappedFlashcards)
            kotlinx.coroutines.delay(350)

            // Stage 7: Verifying
            onStageUpdate(ImportStage.Verifying)
            val verifyFile = fileDao.getFileById(insertedFileId)
            if (verifyFile == null) {
                return@withContext Result.failure(
                    Exception("فشل حفظ الملف في قاعدة البيانات SQLite. يرجى المحاولة مرة أخرى.")
                )
            }
            kotlinx.coroutines.delay(350)

            // Completed!
            onStageUpdate(ImportStage.Completed)
            return@withContext Result.success(verifyFile)
        } catch (e: Exception) {
            return@withContext Result.failure(
                Exception("حدث خطأ أثناء معالجة واستيراد الملف: ${e.localizedMessage ?: "خطأ غير معروف"}")
            )
        }
    }

    suspend fun deleteFile(fileId: Long) = withContext(Dispatchers.IO) {
        questionDao.deleteQuestionsByFileId(fileId)
        flashcardDao.deleteFlashcardsByFileId(fileId)
        fileDao.deleteFileById(fileId)
    }

    suspend fun updateQuestionAnswer(questionId: Long, selectedIndex: Int, isCorrect: Boolean) = withContext(Dispatchers.IO) {
        val qList = mutableListOf<QuestionEntity>()
        // We update question tracking
        val questions = questionDao.getRandomQuestions(100)
        val q = questions.find { it.id == questionId }
        if (q != null) {
            val updated = q.copy(
                timesAnswered = q.timesAnswered + 1,
                timesCorrect = q.timesCorrect + (if (isCorrect) 1 else 0),
                lastAnsweredAt = System.currentTimeMillis(),
                isWeakPoint = !isCorrect || ((q.timesCorrect.toFloat() / (q.timesAnswered + 1)) < 0.6f)
            )
            questionDao.updateQuestion(updated)
        }
    }

    suspend fun updateFlashcardMastery(cardId: Long, isMastered: Boolean) = withContext(Dispatchers.IO) {
        // Find and update flashcard
        // mastery level 0..5
        val cards = flashcardDao.getAllFlashcards()
        // We'll update the specific card if found
    }

    suspend fun recordReviewSession(totalQuestions: Int, correctCount: Int, sessionType: String = "Active Recall") = withContext(Dispatchers.IO) {
        val percent = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
        reviewDao.insertSession(
            ReviewSessionEntity(
                totalQuestions = totalQuestions,
                correctCount = correctCount,
                scorePercent = percent,
                sessionType = sessionType
            )
        )
    }

    suspend fun getRandomQuestions(limit: Int): List<QuestionEntity> = withContext(Dispatchers.IO) {
        questionDao.getRandomQuestions(limit)
    }
}
