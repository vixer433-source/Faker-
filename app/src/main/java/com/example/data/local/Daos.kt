package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyFileDao {
    @Query("SELECT * FROM study_files ORDER BY importedAt DESC")
    fun getAllFiles(): Flow<List<StudyFileEntity>>

    @Query("SELECT COUNT(*) FROM study_files")
    fun getFilesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_files")
    suspend fun getFilesCountDirect(): Int

    @Query("SELECT * FROM study_files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: Long): StudyFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: StudyFileEntity): Long

    @Query("DELETE FROM study_files WHERE id = :id")
    suspend fun deleteFileById(id: Long)

    @Query("DELETE FROM study_files")
    suspend fun deleteAllFiles()
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE fileId = :fileId ORDER BY id ASC")
    fun getQuestionsByFile(fileId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE isWeakPoint = 1 ORDER BY timesAnswered DESC")
    fun getWeakPointQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(limit: Int): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE fileId = :fileId")
    suspend fun deleteQuestionsByFileId(fileId: Long)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY masteryLevel ASC, nextReviewAt ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE fileId = :fileId")
    fun getFlashcardsByFile(fileId: Long): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE fileId = :fileId")
    suspend fun deleteFlashcardsByFileId(fileId: Long)
}

@Dao
interface ReviewSessionDao {
    @Query("SELECT * FROM review_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ReviewSessionEntity>>

    @Query("SELECT * FROM review_sessions ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSessions(): Flow<List<ReviewSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ReviewSessionEntity): Long
}
