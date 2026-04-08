package com.richi_mc.myapplication.helpers

import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object TextRecognitionHelper {

    suspend fun extractText(context: Context, imageUri: Uri): String =
        suspendCancellableCoroutine { continuation ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromFilePath(context, imageUri)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text
                    continuation.resume(
                        text.ifBlank { "No se detectó texto en la imagen." }
                    )
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

}