package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlurredLockScreen(
    onPickFileClick: () -> Unit
) {
    // Intercept Android Back Button so user cannot bypass the lock
    BackHandler(enabled = true) {
        // Do nothing - lock screen is strictly mandatory
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_pulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("blurred_lock_screen")
    ) {
        // 1. Ghost Background: Simulated Dashboard under heavy blur
        GhostDashboardPreview(
            modifier = Modifier
                .fillMaxSize()
                .blur(22.dp)
        )

        // 2. Dark Translucent Vignette / Glass Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xCC0F172A),
                            Color(0xF0020617)
                        )
                    )
                )
        )

        // Subtle decorative background glows
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldPrimary.copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // 3. Central Modern Glass Card (The Mandatory Lock Popup)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .shadow(
                        elevation = 28.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = EmeraldVibrant.copy(alpha = 0.3f),
                        ambientColor = Color.Black
                    )
                    .border(
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                colors = listOf(
                                    EmeraldVibrant.copy(alpha = 0.6f),
                                    Slate700.copy(alpha = 0.4f),
                                    EmeraldDark.copy(alpha = 0.8f)
                                )
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Slate900.copy(alpha = 0.94f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Brand Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Slate800,
                        border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = EmeraldVibrant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FAKER? • فاكر؟",
                                color = EmeraldVibrant,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Memory & Document Center Hero Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Slate800, Slate850)
                                ),
                                shape = CircleShape
                            )
                            .border(
                                2.dp,
                                Brush.sweepGradient(
                                    listOf(EmeraldVibrant, EmeraldDark, EmeraldLight, EmeraldVibrant)
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Memory Study",
                            tint = EmeraldVibrant,
                            modifier = Modifier.size(52.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .background(EmeraldPrimary, CircleShape)
                                .border(2.dp, Slate900, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Upload",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mandatory Headline
                    Text(
                        text = "ارفع ملفك الأول\nلكي تبدأ في التذكر",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtitle explaining active recall conversion
                    Text(
                        text = "استورد ملفك الدراسي، وسنحوّله إلى أسئلة ومراجعات ذكية تساعدك على معرفة ما تتذكره فعلًا.",
                        color = Slate400,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Big Primary Action Button
                    Button(
                        onClick = onPickFileClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .scale(pulseScale)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = EmeraldVibrant
                            )
                            .testTag("upload_first_file_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "📂 ارفع ملفك الأول",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Supported Formats Pills
                    Text(
                        text = "الصيغ المدعومة للاستيراد المباشر:",
                        color = Slate500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("PDF", "TXT", "DOCX", "JPG", "JPEG", "PNG").forEach { format ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate800.copy(alpha = 0.8f),
                                border = BorderStroke(1.dp, Slate700),
                                modifier = Modifier.padding(horizontal = 3.dp)
                            ) {
                                Text(
                                    text = format,
                                    color = EmeraldVibrant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ghost preview of the Dashboard to show through the blur overlay,
 * giving the user a subtle feeling of the app waiting for their file.
 */
@Composable
private fun GhostDashboardPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Slate950)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ghost Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 32.dp)
                    .background(EmeraldDark.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Slate800, CircleShape)
            )
        }

        // Ghost Metric Card
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(EmeraldPrimary.copy(alpha = 0.2f), CircleShape)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(120.dp, 16.dp).background(Slate700, RoundedCornerShape(4.dp)))
                    Box(modifier = Modifier.size(80.dp, 24.dp).background(EmeraldVibrant.copy(alpha = 0.5f), RoundedCornerShape(4.dp)))
                }
            }
        }

        // Ghost Quick Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f).height(90.dp).background(Slate900, RoundedCornerShape(16.dp)))
            Box(modifier = Modifier.weight(1f).height(90.dp).background(Slate900, RoundedCornerShape(16.dp)))
        }

        // Ghost Recent Files list
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(Slate900.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
