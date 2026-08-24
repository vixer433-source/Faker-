package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FlashcardEntity
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
fun SmartReviewScreen(
    flashcards: List<FlashcardEntity>,
    currentIndex: Int,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onNext: (Int) -> Unit,
    onPrev: (Int) -> Unit,
    onBack: () -> Unit
) {
    val currentCard = flashcards.getOrNull(currentIndex)
    val total = flashcards.size

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "card_flip"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Bar
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
                text = "المراجعة الذكية • Smart Review",
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
                    text = "${if (total > 0) currentIndex + 1 else 0} / $total",
                    color = EmeraldVibrant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Indicator
        LinearProgressIndicator(
            progress = { if (total > 0) (currentIndex + 1).toFloat() / total else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = EmeraldVibrant,
            trackColor = Slate800
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (currentCard == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد بطاقات متاحة للمراجعة حالياً.",
                    color = Slate400,
                    fontSize = 14.sp
                )
            }
        } else {
            // Interactive 3D Flip Flashcard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { onFlip() }
                    .testTag("flashcard_box")
            ) {
                if (rotation <= 90f) {
                    // Front of card (Concept / Question)
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = EmeraldVibrant.copy(alpha = 0.2f))
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.linearGradient(listOf(EmeraldVibrant.copy(alpha = 0.5f), Slate700))
                                ),
                                RoundedCornerShape(28.dp)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldDark.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = currentCard.conceptTag,
                                        color = EmeraldVibrant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "الواجهة • سؤال/مفهوم",
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = EmeraldVibrant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = currentCard.front,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = "Flip",
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "المس البطاقة لإظهار الشرح والتذكر",
                                    color = EmeraldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    // Back of card (Answer / Explanation) - flipped 180 so it appears normal
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                            .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = EmeraldVibrant.copy(alpha = 0.3f))
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.linearGradient(listOf(EmeraldPrimary, Slate700))
                                ),
                                RoundedCornerShape(28.dp)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate850)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, EmeraldVibrant.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "التذكر الفعّال",
                                        color = EmeraldVibrant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "الخلفية • الشرح",
                                    color = Slate400,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = currentCard.back,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )

                            Text(
                                text = "المس للرجوع إلى السؤال",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onPrev(total) },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Slate900, CircleShape)
                        .border(1.dp, Slate700, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }

                Button(
                    onClick = { onFlip() },
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f)),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flip,
                        contentDescription = null,
                        tint = EmeraldVibrant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFlipped) "السؤال" else "اقلب البطاقة",
                        color = EmeraldVibrant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { onNext(total) },
                    modifier = Modifier
                        .size(50.dp)
                        .background(EmeraldPrimary, CircleShape)
                        .shadow(8.dp, CircleShape, spotColor = EmeraldVibrant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
