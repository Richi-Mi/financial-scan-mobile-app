package com.richi_mc.myapplication.helpers

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LlmHelper(private val context: Context) {

    private var llmInference: LlmInference? = null
    private var isInitialized = false

    suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val modelFile = ModelDownloader.getModelFile(context)

            if (!modelFile.exists()) {
                error("Modelo no encontrado. Descárgalo primero.")
            }

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(1024)
                .setMaxTopK(40)         // ✅ Usa setMaxTopK en lugar de setTopK
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            isInitialized = true
        }
    }

    suspend fun generateResponse(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (!isInitialized) error("LLM no inicializado")

            // ✅ Formato genérico (DeepSeek no usa el formato <start_of_turn> de Gemma)
            llmInference?.generateResponse(prompt)
                ?: error("LLM no disponible")
        }
    }

    suspend fun generateTicketJson(ocrText: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (!isInitialized) error("LLM no inicializado")

            // 1. Usamos delimitadores visuales fuertes (---)
            // 2. Terminamos el prompt exactamente abriendo la llave '{' y la comilla del primer campo
            val strictPrompt = """
    Extrae los datos del TICKET y genera un JSON idéntico al EJEMPLO.
    Categorías válidas: alimentos, bebidas, snacks, otro.
    es_gasto_hormiga: true SOLO si es snack, dulce, refresco o café.

    EJEMPLO TICKET:
    WALMART
    05/04/2026
    LECHE 1L $28.00
    SABRITAS $20.00
    TOTAL: $48.00

    EJEMPLO JSON:
    {
      "comercio": "WALMART",
      "fecha": "2026-04-05",
      "total": 48.00,
      "productos": [
        {"nombre": "LECHE 1L", "precio": 28.00, "categoria": "alimentos", "es_gasto_hormiga": false},
        {"nombre": "SABRITAS", "precio": 20.00, "categoria": "snacks", "es_gasto_hormiga": true}
      ]
    }

    TICKET REAL:
    $ocrText

    JSON RESULTANTE:
""".trimIndent()

            // El modelo generará texto CONTINUANDO desde la comilla de "comercio": "
            val response = llmInference?.generateResponse(strictPrompt)
                ?: error("LLM no disponible")

            // 3. Reconstruimos el JSON porque nosotros le dimos el empujón inicial
            val rawJson = "{\n  \"comercio\": \"$response"

            // 4. Limpieza final para asegurar que termine en '}'
            val startIndex = rawJson.indexOf('{')
            val endIndex = rawJson.lastIndexOf('}')

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                rawJson.substring(startIndex, endIndex + 1)
            } else {
                rawJson.trim()
            }
        }
    }

    fun close() {
        llmInference?.close()
        llmInference = null
        isInitialized = false
    }
}