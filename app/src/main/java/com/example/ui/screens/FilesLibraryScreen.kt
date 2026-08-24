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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StudyFileEntity
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
fun FilesLibraryScreen(
    files: List<StudyFileEntity>,
    selectedFile: StudyFileEntity?,
    onSelectFile: (StudyFileEntity?) -> Unit,
    onPickNewFile: () -> Unit,
    onDeleteFile: (Long) -> Unit,
    onBack: () -> Unit
) {
    var fileToDelete by remember { mutableStateOf<StudyFileEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
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
                text = "مكتبة الملفات الدراسية",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onPickNewFile,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("library_add_file_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // If user is inspecting a specific file
        if (selectedFile != null) {
            FileDetailView(
                file = selectedFile,
                onCloseDetail = { onSelectFile(null) },
                onDelete = { fileToDelete = selectedFile }
            )
        } else {
            // Full files list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Info banner about file requirement
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        border = BorderStroke(1.dp, Slate800)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "يجب وجود ملف واحد على الأقل للاستمرار في استخدام التطبيق.",
                                color = Slate400,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                items(files) { file ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectFile(file) }
                            .border(1.dp, Slate800, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = EmeraldDark.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f)),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = file.fileType,
                                                color = EmeraldVibrant,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = file.fileName,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${file.questionCount} سؤال • ${file.flashcardCount} بطاقة تذكر",
                                            color = EmeraldLight,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                IconButton(onClick = { fileToDelete = file }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = Slate500
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = file.summary,
                                color = Slate400,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (fileToDelete != null) {
        val target = fileToDelete!!
        val isOnlyFile = files.size <= 1

        AlertDialog(
            onDismissRequest = { fileToDelete = null },
            containerColor = Slate900,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOnlyFile) Icons.Default.Lock else Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = AccentCoral
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnlyFile) "تنبيه قفل التطبيق الإجباري!" else "حذف الملف الدراسي",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "هل أنت متأكد من رغبتك في حذف ملف '${target.fileName}'؟",
                        color = Slate400,
                        fontSize = 13.sp
                    )
                    if (isOnlyFile) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "⚠️ هذا هو الملف الوحيد في التطبيق. حذفه سيعيد التطبيق فوراً إلى شاشة القفل 'ارفع ملفك الأول لكي تبدأ في التذكر'.",
                            color = AccentAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 17.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = target.id
                        fileToDelete = null
                        if (selectedFile?.id == id) {
                            onSelectFile(null)
                        }
                        onDeleteFile(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCoral)
                ) {
                    Text("نعم، احذف الملف", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { fileToDelete = null }) {
                    Text("إلغاء", color = Slate400)
                }
            }
        )
    }
}

@Composable
private fun FileDetailView(
    file: StudyFileEntity,
    onCloseDetail: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Slate700, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldDark.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = file.fileType,
                            color = EmeraldVibrant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCoral.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = AccentCoral, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف الملف", color = AccentCoral, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = file.fileName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "الملخص التحليلي:",
                    color = EmeraldVibrant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = file.summary,
                    color = Slate400,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "النص المستخرج من المستند:",
                    color = EmeraldVibrant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = file.extractedText.ifBlank { "لا يوجد نص إضافي" },
                    color = Slate500,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCloseDetail,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Slate800)
        ) {
            Text("العودة لقائمة الملفات", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
