package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ImportStage
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun ImportingProgressDialog(
    stage: ImportStage,
    fileName: String
) {
    // Intercept back button during processing
    BackHandler(enabled = true) {}

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_icon"
    )

    val currentStep = stage.stepNumber
    val progress = (currentStep.toFloat() / 7f).coerceIn(0.1f, 1.0f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE020617))
            .testTag("importing_progress_dialog"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = EmeraldVibrant.copy(alpha = 0.4f))
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(EmeraldVibrant.copy(alpha = 0.6f), Slate700, EmeraldDark)
                        )
                    ),
                    RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Brush.linearGradient(listOf(EmeraldDark, Slate800)),
                            shape = CircleShape
                        )
                        .border(2.dp, EmeraldLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (stage is ImportStage.Completed) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Success",
                            tint = EmeraldVibrant,
                            modifier = Modifier.size(42.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Processing",
                            tint = EmeraldVibrant,
                            modifier = Modifier
                                .size(38.dp)
                                .rotate(if (stage.stepNumber % 2 == 0) rotation else 0f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (stage is ImportStage.Completed) "تم استيراد ملفك بنجاح 🎉" else "جاري استيراد وتحليل الملف...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = fileName,
                    color = EmeraldVibrant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = EmeraldVibrant,
                    trackColor = Slate800,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Steps list
                val stagesList = listOf(
                    1 to "اختيار والتحقق من الملف",
                    2 to "استخراج النص والملاحظات",
                    3 to "تحليل المحتوى الذكي",
                    4 to "توليد أسئلة التذكر الفعّال",
                    5 to "حفظ البيانات في SQLite",
                    6 to "التأكد من اكتمال الحفظ بنجاح"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    stagesList.forEach { (index, title) ->
                        val isDone = currentStep > index
                        val isCurrent = currentStep == index

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = EmeraldVibrant,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isCurrent) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = EmeraldVibrant,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = "Pending",
                                    tint = Slate700,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = title,
                                color = when {
                                    isDone -> Color.White
                                    isCurrent -> EmeraldVibrant
                                    else -> Slate600
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessCelebrationView(
    fileName: String,
    questionCount: Int
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(400)) + scaleIn(tween(400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF0020617)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(32.dp, RoundedCornerShape(32.dp), spotColor = EmeraldVibrant)
                    .border(2.dp, EmeraldVibrant, RoundedCornerShape(32.dp)),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 54.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تم استيراد ملفك بنجاح 🎉",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = fileName,
                        color = EmeraldVibrant,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "تم إنشاء $questionCount سؤال وبطاقة تذكر ذكية جاهزة للاختبار الآن!",
                        color = Slate400,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
