package com.example.data.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

data class ExtractedFileData(
    val fileName: String,
    val fileType: String,
    val fileSizeBytes: Long,
    val text: String,
    val imageBase64: String? = null
)

object FileExtractor {

    fun extractData(context: Context, uri: Uri): ExtractedFileData {
        val contentResolver = context.contentResolver
        var fileName = "document"
        var fileSize: Long = 0

        // Get file name and size from ContentResolver
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: "document"
                    }
                    if (sizeIndex != -1) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (_: Exception) {}

        val lowerName = fileName.lowercase()
        val mimeType = contentResolver.getType(uri) ?: ""

        val fileType = when {
            lowerName.endsWith(".pdf") || mimeType.contains("pdf") -> "PDF"
            lowerName.endsWith(".docx") || mimeType.contains("wordprocessingml") || mimeType.contains("docx") -> "DOCX"
            lowerName.endsWith(".txt") || lowerName.endsWith(".md") || mimeType.contains("text") -> "TXT"
            lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png") || mimeType.contains("image") -> "IMAGE"
            else -> "TXT"
        }

        var extractedText = ""
        var imageBase64: String? = null

        when (fileType) {
            "TXT" -> {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    extractedText = readTextFromStream(inputStream)
                }
            }
            "DOCX" -> {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    extractedText = readTextFromDocx(inputStream)
                }
            }
            "PDF" -> {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    extractedText = readTextFromPdf(inputStream)
                }
            }
            "IMAGE" -> {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        imageBase64 = bitmapToBase64(bitmap)
                        extractedText = "صورة مستند دراسي: $fileName"
                    }
                }
            }
        }

        if (extractedText.isBlank() && imageBase64 == null) {
            // Try general stream read as text
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    extractedText = readTextFromStream(inputStream)
                }
            } catch (_: Exception) {}
        }

        if (fileSize <= 0) {
            fileSize = extractedText.toByteArray().size.toLong()
        }

        return ExtractedFileData(
            fileName = fileName,
            fileType = fileType,
            fileSizeBytes = fileSize,
            text = extractedText.trim(),
            imageBase64 = imageBase64
        )
    }

    private fun readTextFromStream(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line).append("\n")
        }
        return sb.toString()
    }

    private fun readTextFromDocx(inputStream: InputStream): String {
        val sb = StringBuilder()
        try {
            val zip = ZipInputStream(inputStream)
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == "word/document.xml") {
                    val xmlContent = readTextFromStream(zip)
                    // Simple XML regex to extract text between <w:t> tags
                    val textRegex = Regex("<w:t[^>]*>(.*?)</w:t>")
                    val matches = textRegex.findAll(xmlContent)
                    for (match in matches) {
                        val text = match.groupValues[1]
                        sb.append(text).append(" ")
                    }
                    break
                }
                entry = zip.nextEntry
            }
        } catch (_: Exception) {}
        return sb.toString().trim()
    }

    private fun readTextFromPdf(inputStream: InputStream): String {
        val sb = StringBuilder()
        try {
            // PDF basic text stream scanner
            val bytes = inputStream.readBytes()
            val content = String(bytes, Charsets.ISO_8859_1)
            
            // Extract text enclosed in parentheses in BT ... ET blocks or general string tokens
            val btRegex = Regex("BT[\\s\\S]*?ET")
            val matches = btRegex.findAll(content)
            for (block in matches) {
                val tjRegex = Regex("\\((.*?)\\)\\s*Tj|\\[([^\\]]*)\\]\\s*TJ")
                val textMatches = tjRegex.findAll(block.value)
                for (tm in textMatches) {
                    val t = tm.groupValues[1].ifEmpty { tm.groupValues[2] }
                    if (t.isNotBlank()) {
                        sb.append(t.replace("\\(", "(").replace("\\)", ")")).append(" ")
                    }
                }
            }

            if (sb.length < 50) {
                // Fallback: look for readable ASCII / UTF-8 clusters
                val cleanLines = content.split("\n")
                    .map { it.replace(Regex("[^\\p{L}\\p{N}\\p{P}\\p{Z}]"), "").trim() }
                    .filter { it.length > 5 }
                sb.append(cleanLines.take(100).joinToString("\n"))
            }
        } catch (_: Exception) {}
        return sb.toString().trim()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Resize if too big
        val scaled = if (bitmap.width > 1200 || bitmap.height > 1200) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newWidth = if (ratio > 1) 1200 else (1200 * ratio).toInt()
            val newHeight = if (ratio > 1) (1200 / ratio).toInt() else 1200
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
