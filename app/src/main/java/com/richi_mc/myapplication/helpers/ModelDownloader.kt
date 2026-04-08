package com.richi_mc.myapplication.helpers

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ModelDownloader {

    // ✅ URL oficial de Google MediaPipe samples — sin autenticación
    private const val MODEL_URL =
        "https://huggingface.co/google/gemma-2b-it-tflite/resolve/main/gemma-2b-it-cpu-int4.bin"

    private const val MODEL_FILENAME =
        "gemma-2b-it-cpu-int4.bin"

    fun getModelFile(context: Context): File {
        val dir = File(context.filesDir, "llm").apply { mkdirs() }
        return File(dir, MODEL_FILENAME)
    }

    fun isModelDownloaded(context: Context): Boolean {
        val file = getModelFile(context)
        return file.exists() && file.length() > 50_000_000L
    }

    suspend fun downloadModel(
        context: Context,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val modelFile = getModelFile(context)
            val tempFile = File(modelFile.parent, "${modelFile.name}.tmp")

            val hfToken = "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"
            val connection = (URL(MODEL_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 30_000
                readTimeout = 60_000
                requestMethod = "GET"
                instanceFollowRedirects = true
                setRequestProperty("Authorization", "Bearer $hfToken")
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                error("HTTP ${connection.responseCode}: ${connection.responseMessage}")
            }

            val totalBytes = connection.contentLengthLong
            var downloadedBytes = 0L

            connection.inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            onProgress((downloadedBytes * 100 / totalBytes).toInt())
                        }
                    }
                }
            }

            tempFile.renameTo(modelFile)
            modelFile
        }
    }
}