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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestionEntity
import com.example.data.local.StudyFileEntity
import com.example.ui.AppTab
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.EmeraldContainerDark
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
fun DashboardScreen(
    files: List<StudyFileEntity>,
    questions: List<QuestionEntity>,
    weakPoints: List<QuestionEntity>,
    onNavigateToTab: (AppTab) -> Unit,
    onStartQuiz: () -> Unit,
    onPickNewFile: () -> Unit,
    onDeleteFile: (Long) -> Unit,
    onViewFileDetails: (StudyFileEntity) -> Unit
) {
    val totalQuestionsCount = questions.size
    val answeredQuestions = questions.filter { it.timesAnswered > 0 }
    val correctAnswers = questions.sumOf { it.timesCorrect }
    val totalAnswers = questions.sumOf { it.timesAnswered }
    val memoryRetention = if (totalAnswers > 0) {
        ((correctAnswers.toFloat() / totalAnswers) * 100).toInt().coerceIn(0, 100)
    } else {
        75 // Initial readiness base
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Top Branding Bar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark)),
                                CircleShape
                            )
                            .border(1.5.dp, EmeraldVibrant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "FAKER?",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FAKER? • فاكر؟",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "منصة التذكر الفعّال للملفات الدراسية",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }

                // Add File Quick Button
                Button(
                    onClick = onPickNewFile,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("add_file_dashboard_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add File",
                        tint = EmeraldVibrant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "استيراد",
                        color = EmeraldVibrant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Memory Retention Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = EmeraldVibrant.copy(alpha = 0.25f))
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.linearGradient(listOf(EmeraldVibrant.copy(alpha = 0.5f), Slate700))
                    ),
                    RoundedCornerShape(26.dp)
                ),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = EmeraldDark.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "مؤشر التذكر الفعّال",
                                    color = EmeraldVibrant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "مستوى استيعابك للملفات",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${files.size} ملفات • $totalQuestionsCount سؤال ذكي مستخرج",
                            color = Slate400,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Circular Score Gauge
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { memoryRetention / 100f },
                            modifier = Modifier.size(80.dp),
                            color = EmeraldVibrant,
                            trackColor = Slate800,
                            strokeWidth = 8.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$memoryRetention%",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "تذكر",
                                color = EmeraldLight,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Text(
                text = "جلسات التذكر والمراجعة",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Smart Review Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(AppTab.SMART_REVIEW) }
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(EmeraldDark, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldVibrant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "المراجعة الذكية",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "بطاقات استذكار تفاعلية",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick Quiz Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStartQuiz() }
                        .border(1.dp, Slate700, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Slate800, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "اختبار تذكر",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "أسئلة اختيار فوري",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Weak Points Banner if exists
        if (weakPoints.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTab(AppTab.WEAK_POINTS) }
                        .border(1.dp, AccentCoral.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(AccentCoral.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AccentCoral,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "تم رصد ${weakPoints.size} نقاط تحتاج تثبيت",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "اضغط هنا لمراجعة المفاهيم التي أخطأت فيها",
                                    color = Slate400,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = AccentCoral,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Imported Files Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الملفات المستوردة (${files.size})",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "عرض الكل",
                    color = EmeraldVibrant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToTab(AppTab.FILES_LIBRARY) }
                )
            }
        }

        items(files) { file ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewFileDetails(file) }
                    .border(1.dp, Slate800, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldDark.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = file.fileType,
                                    color = EmeraldVibrant,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.fileName,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${file.questionCount} سؤال • ${file.flashcardCount} بطاقة",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Delete file button
                    IconButton(
                        onClick = { onDeleteFile(file.id) },
                        modifier = Modifier.testTag("delete_file_${file.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete File",
                            tint = Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
