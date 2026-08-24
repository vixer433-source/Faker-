package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_files")
data class StudyFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val fileUri: String,
    val fileType: String, // PDF, TXT, DOCX, IMAGE
    val fileSizeBytes: Long,
    val extractedText: String,
    val summary: String,
    val importedAt: Long = System.currentTimeMillis(),
    val questionCount: Int = 0,
    val flashcardCount: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileId: Long,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswerIndex: Int, // 0..3
    val explanation: String,
    val conceptTag: String,
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val timesAnswered: Int = 0,
    val timesCorrect: Int = 0,
    val lastAnsweredAt: Long? = null,
    val isWeakPoint: Boolean = false
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileId: Long,
    val front: String,
    val back: String,
    val conceptTag: String,
    val masteryLevel: Int = 0, // 0 to 5
    val nextReviewAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "review_sessions")
data class ReviewSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalQuestions: Int,
    val correctCount: Int,
    val scorePercent: Int,
    val sessionType: String = "Active Recall"
)
