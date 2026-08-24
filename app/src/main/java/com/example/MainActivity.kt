package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppState
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.BlurredLockScreen
import com.example.ui.components.ImportingProgressDialog
import com.example.ui.components.SuccessCelebrationView
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FilesLibraryScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SmartReviewScreen
import com.example.ui.screens.WeakPointsScreen
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.FakerTheme
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FakerTheme(darkTheme = true) {
                // Support clean Arabic RTL Layout
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FakerApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FakerApp(viewModel: MainViewModel) {
    val appState by viewModel.appState.collectAsState()
    val files by viewModel.allFiles.collectAsState()
    val questions by viewModel.allQuestions.collectAsState()
    val weakPoints by viewModel.weakPoints.collectAsState()
    val flashcards by viewModel.allFlashcards.collectAsState()

    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val quizIndex by viewModel.quizIndex.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val quizFinished by viewModel.quizFinished.collectAsState()

    val activeFlashcardIndex by viewModel.activeFlashcardIndex.collectAsState()
    val isCardFlipped by viewModel.isCardFlipped.collectAsState()
    val selectedFileForDetail by viewModel.selectedFileForDetail.collectAsState()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importFile(uri)
        }
    }

    val openFilePicker: () -> Unit = {
        filePickerLauncher.launch(
            arrayOf(
                "application/pdf",
                "text/*",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/*",
                "*/*"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
    ) {
        when (val state = appState) {
            is AppState.Checking -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmeraldVibrant,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            is AppState.NoFile -> {
                // MANDATORY FIRST LAUNCH LOCKED STATE
                BlurredLockScreen(
                    onPickFileClick = openFilePicker
                )
            }

            is AppState.Importing -> {
                ImportingProgressDialog(
                    stage = state.stage,
                    fileName = state.fileName
                )
            }

            is AppState.SuccessImport -> {
                SuccessCelebrationView(
                    fileName = state.fileName,
                    questionCount = state.questionCount
                )
            }

            is AppState.Error -> {
                // Return to lock screen (or current view) and show error dialog
                BlurredLockScreen(
                    onPickFileClick = openFilePicker
                )

                AlertDialog(
                    onDismissRequest = { viewModel.dismissError() },
                    containerColor = Slate900,
                    title = {
                        Text(
                            text = "فشل استيراد الملف",
                            color = AccentCoral,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = state.message,
                            color = Slate400,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.dismissError()
                                openFilePicker()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("إعادة المحاولة", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { viewModel.dismissError() },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800)
                        ) {
                            Text("إلغاء", color = Slate400)
                        }
                    }
                )
            }

            is AppState.Ready -> {
                val currentTab = state.currentTab

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Slate950,
                    bottomBar = {
                        // Bottom Navigation Bar
                        NavigationBar(
                            containerColor = Slate900,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Slate800)
                                .testTag("main_bottom_nav")
                        ) {
                            val navItems = listOf(
                                Triple(AppTab.DASHBOARD, "الرئيسية", Icons.Default.Dashboard to Icons.Outlined.Dashboard),
                                Triple(AppTab.SMART_REVIEW, "المراجعة", Icons.Default.Psychology to Icons.Outlined.Psychology),
                                Triple(AppTab.QUIZ, "الاختبار", Icons.Default.FlashOn to Icons.Outlined.FlashOn),
                                Triple(AppTab.WEAK_POINTS, "نقاط الضعف", Icons.Default.Warning to Icons.Outlined.Warning),
                                Triple(AppTab.FILES_LIBRARY, "المكتبة", Icons.Default.Folder to Icons.Outlined.Folder)
                            )

                            navItems.forEach { (tab, label, icons) ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (tab == AppTab.QUIZ && !isSelected) {
                                            viewModel.startQuiz()
                                        } else {
                                            viewModel.navigateToTab(tab)
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) icons.first else icons.second,
                                            contentDescription = label,
                                            tint = if (isSelected) EmeraldVibrant else Slate500
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            color = if (isSelected) EmeraldVibrant else Slate500,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = EmeraldDark.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                AppTab.DASHBOARD -> {
                                    DashboardScreen(
                                        files = files,
                                        questions = questions,
                                        weakPoints = weakPoints,
                                        onNavigateToTab = { viewModel.navigateToTab(it) },
                                        onStartQuiz = { viewModel.startQuiz() },
                                        onPickNewFile = openFilePicker,
                                        onDeleteFile = { viewModel.deleteFile(it) },
                                        onViewFileDetails = {
                                            viewModel.viewFileDetails(it)
                                            viewModel.navigateToTab(AppTab.FILES_LIBRARY)
                                        }
                                    )
                                }

                                AppTab.SMART_REVIEW -> {
                                    SmartReviewScreen(
                                        flashcards = flashcards,
                                        currentIndex = activeFlashcardIndex,
                                        isFlipped = isCardFlipped,
                                        onFlip = { viewModel.flipCard() },
                                        onNext = { viewModel.nextFlashcard(it) },
                                        onPrev = { viewModel.prevFlashcard(it) },
                                        onBack = { viewModel.navigateToTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.QUIZ -> {
                                    QuizScreen(
                                        questions = quizQuestions,
                                        currentIndex = quizIndex,
                                        score = quizScore,
                                        selectedAnswer = selectedAnswer,
                                        isFinished = quizFinished,
                                        onSelectAnswer = { viewModel.selectQuizAnswer(it) },
                                        onNextQuestion = { viewModel.nextQuizQuestion() },
                                        onRestartQuiz = { viewModel.startQuiz() },
                                        onBack = { viewModel.navigateToTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.WEAK_POINTS -> {
                                    WeakPointsScreen(
                                        weakPoints = weakPoints,
                                        onStartTargetedQuiz = { weakList ->
                                            viewModel.startQuiz(weakList)
                                        },
                                        onBack = { viewModel.navigateToTab(AppTab.DASHBOARD) }
                                    )
                                }

                                AppTab.FILES_LIBRARY -> {
                                    FilesLibraryScreen(
                                        files = files,
                                        selectedFile = selectedFileForDetail,
                                        onSelectFile = { viewModel.viewFileDetails(it) },
                                        onPickNewFile = openFilePicker,
                                        onDeleteFile = { viewModel.deleteFile(it) },
                                        onBack = { viewModel.navigateToTab(AppTab.DASHBOARD) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
