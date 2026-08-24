package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestionEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun QuizScreen(
    questions: List<QuestionEntity>,
    currentIndex: Int,
    score: Int,
    selectedAnswer: Int?,
    isFinished: Boolean,
    onSelectAnswer: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
    onBack: () -> Unit
) {
    val total = questions.size
    val currentQ = questions.getOrNull(currentIndex)

    if (isFinished || (currentQ == null && total > 0)) {
        // Quiz Results Screen
        QuizResultsView(
            score = score,
            total = total,
            onRestart = onRestartQuiz,
            onBack = onBack
        )
        return
    }

    if (currentQ == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950),
            contentAlignment = Alignment.Center
        ) {
            Text("لا توجد أسئلة متاحة حالياً.", color = Slate400)
        }
        return
    }

    val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "اختبار التذكر الفعّال",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Slate850,
                border = BorderStroke(1.dp, Slate700)
            ) {
                Text(
                    text = "${currentIndex + 1} / $total",
                    color = EmeraldVibrant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = EmeraldVibrant,
            trackColor = Slate800
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = EmeraldVibrant.copy(alpha = 0.2f))
                .border(1.dp, Slate700, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldDark.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = currentQ.conceptTag,
                        color = EmeraldVibrant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentQ.questionText,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options List
        options.forEachIndexed { index, optionText ->
            val isSelected = selectedAnswer == index
            val isCorrect = index == currentQ.correctAnswerIndex
            val showFeedback = selectedAnswer != null

            val borderColor = when {
                showFeedback && isCorrect -> EmeraldVibrant
                showFeedback && isSelected && !isCorrect -> AccentCoral
                isSelected -> EmeraldLight
                else -> Slate800
            }

            val cardBg = when {
                showFeedback && isCorrect -> EmeraldDark.copy(alpha = 0.4f)
                showFeedback && isSelected && !isCorrect -> AccentCoral.copy(alpha = 0.15f)
                isSelected -> Slate800
                else -> Slate900
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable(enabled = selectedAnswer == null) { onSelectAnswer(index) }
                    .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                    .testTag("quiz_option_$index"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                when {
                                    showFeedback && isCorrect -> EmeraldPrimary
                                    showFeedback && isSelected && !isCorrect -> AccentCoral
                                    else -> Slate800
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showFeedback && isCorrect) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        } else if (showFeedback && isSelected && !isCorrect) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                text = "${('A'.code + index).toChar()}",
                                color = if (isSelected) EmeraldVibrant else Slate400,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = optionText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected || (showFeedback && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Explanation and Next Button if answered
        if (selectedAnswer != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate700, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate850)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = EmeraldVibrant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedAnswer == currentQ.correctAnswerIndex) "إجابة صحيحة! أحسنت 🎉" else "توضيح الإجابة الصحيحة:",
                            color = if (selectedAnswer == currentQ.correctAnswerIndex) EmeraldVibrant else AccentAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentQ.explanation,
                        color = Slate400,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = EmeraldVibrant)
                    .testTag("quiz_next_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(
                    text = if (currentIndex + 1 < total) "السؤال التالي ⬅" else "عرض نتيجة الاختبار 🏆",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun QuizResultsView(
    score: Int,
    total: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    val percent = if (total > 0) (score * 100) / total else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = EmeraldVibrant)
                .border(2.dp, EmeraldLight, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(EmeraldDark, CircleShape)
                        .border(2.dp, EmeraldVibrant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = AccentAmber,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "اكتمل الاختبار!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "حققت $score من أصل $total أسئلة",
                    color = Slate400,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Slate800,
                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "نسبة الاستيعاب: $percent%",
                        color = EmeraldVibrant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إعادة الاختبار", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800)
                ) {
                    Text("العودة للرئيسية", color = Slate400, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
