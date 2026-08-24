package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FlashcardEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.ReviewSessionEntity
import com.example.data.local.StudyFileEntity
import com.example.data.repository.ImportStage
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppState {
    object Checking : AppState()
    object NoFile : AppState()
    data class Importing(val stage: ImportStage, val fileName: String) : AppState()
    data class SuccessImport(val fileName: String, val questionCount: Int) : AppState()
    data class Ready(val currentTab: AppTab = AppTab.DASHBOARD) : AppState()
    data class Error(val message: String) : AppState()
}

enum class AppTab {
    DASHBOARD,
    SMART_REVIEW,
    QUIZ,
    WEAK_POINTS,
    FILES_LIBRARY
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudyRepository(application)

    private val _appState = MutableStateFlow<AppState>(AppState.Checking)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    val allFiles: StateFlow<List<StudyFileEntity>> = repository.allFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filesCount: StateFlow<Int> = repository.filesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weakPoints: StateFlow<List<QuestionEntity>> = repository.weakPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFlashcards: StateFlow<List<FlashcardEntity>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<ReviewSessionEntity>> = repository.recentSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Quiz State
    private val _quizQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val quizQuestions = _quizQuestions.asStateFlow()

    private val _quizIndex = MutableStateFlow(0)
    val quizIndex = _quizIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore = _quizScore.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer = _selectedAnswer.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished = _quizFinished.asStateFlow()

    // Active Flashcard Review State
    private val _activeFlashcardIndex = MutableStateFlow(0)
    val activeFlashcardIndex = _activeFlashcardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped = _isCardFlipped.asStateFlow()

    // File Details Viewing
    private val _selectedFileForDetail = MutableStateFlow<StudyFileEntity?>(null)
    val selectedFileForDetail = _selectedFileForDetail.asStateFlow()

    init {
        checkInitialDatabaseState()
        observeFilesCount()
    }

    private fun checkInitialDatabaseState() {
        viewModelScope.launch {
            val count = repository.getFilesCountDirect()
            if (count > 0) {
                _appState.value = AppState.Ready(AppTab.DASHBOARD)
            } else {
                _appState.value = AppState.NoFile
            }
        }
    }

    private fun observeFilesCount() {
        viewModelScope.launch {
            repository.filesCount.collect { count ->
                val current = _appState.value
                if (count == 0 && current !is AppState.Importing && current !is AppState.Checking) {
                    _appState.value = AppState.NoFile
                } else if (count > 0 && current is AppState.NoFile) {
                    _appState.value = AppState.Ready(AppTab.DASHBOARD)
                }
            }
        }
    }

    fun navigateToTab(tab: AppTab) {
        val current = _appState.value
        if (current is AppState.Ready) {
            _appState.value = AppState.Ready(tab)
        }
    }

    fun importFile(uri: Uri, displayHintName: String = "document") {
        viewModelScope.launch {
            _appState.value = AppState.Importing(ImportStage.Selecting, displayHintName)
            
            val result = repository.processAndImportFile(uri) { stage ->
                _appState.value = AppState.Importing(stage, displayHintName)
            }

            result.fold(
                onSuccess = { importedFile ->
                    _appState.value = AppState.SuccessImport(
                        fileName = importedFile.fileName,
                        questionCount = importedFile.questionCount
                    )
                    // Celebration delay then proceed to Ready Dashboard
                    kotlinx.coroutines.delay(1800)
                    _appState.value = AppState.Ready(AppTab.DASHBOARD)
                },
                onFailure = { error ->
                    _appState.value = AppState.Error(
                        error.message ?: "حدث خطأ غير متوقع أثناء استيراد الملف."
                    )
                }
            )
        }
    }

    fun dismissError() {
        viewModelScope.launch {
            val count = repository.getFilesCountDirect()
            if (count > 0) {
                _appState.value = AppState.Ready(AppTab.DASHBOARD)
            } else {
                _appState.value = AppState.NoFile
            }
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            repository.deleteFile(fileId)
            val remainingCount = repository.getFilesCountDirect()
            if (remainingCount == 0) {
                _selectedFileForDetail.value = null
                _appState.value = AppState.NoFile
            }
        }
    }

    fun viewFileDetails(file: StudyFileEntity?) {
        _selectedFileForDetail.value = file
    }

    // Quiz & Recall Interactions
    fun startQuiz(questions: List<QuestionEntity>? = null) {
        viewModelScope.launch {
            val pool = questions ?: repository.getRandomQuestions(8)
            if (pool.isNotEmpty()) {
                _quizQuestions.value = pool.shuffled()
                _quizIndex.value = 0
                _quizScore.value = 0
                _selectedAnswer.value = null
                _quizFinished.value = false
                navigateToTab(AppTab.QUIZ)
            }
        }
    }

    fun selectQuizAnswer(index: Int) {
        if (_selectedAnswer.value != null) return
        _selectedAnswer.value = index

        val q = _quizQuestions.value.getOrNull(_quizIndex.value) ?: return
        val isCorrect = index == q.correctAnswerIndex
        if (isCorrect) {
            _quizScore.value = _quizScore.value + 1
        }

        viewModelScope.launch {
            repository.updateQuestionAnswer(q.id, index, isCorrect)
        }
    }

    fun nextQuizQuestion() {
        val nextIdx = _quizIndex.value + 1
        if (nextIdx < _quizQuestions.value.size) {
            _quizIndex.value = nextIdx
            _selectedAnswer.value = null
        } else {
            _quizFinished.value = true
            // Save review session
            viewModelScope.launch {
                repository.recordReviewSession(
                    totalQuestions = _quizQuestions.value.size,
                    correctCount = _quizScore.value,
                    sessionType = "اختبار تذكر"
                )
            }
        }
    }

    // Flashcard Interactions
    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun nextFlashcard(totalCards: Int) {
        if (totalCards > 0) {
            _activeFlashcardIndex.value = (_activeFlashcardIndex.value + 1) % totalCards
            _isCardFlipped.value = false
        }
    }

    fun prevFlashcard(totalCards: Int) {
        if (totalCards > 0) {
            _activeFlashcardIndex.value = if (_activeFlashcardIndex.value - 1 < 0) totalCards - 1 else _activeFlashcardIndex.value - 1
            _isCardFlipped.value = false
        }
    }
}
