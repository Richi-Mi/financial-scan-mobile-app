package com.richi_mc.myapplication.ui.presentation.history

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.*
import com.google.gson.GsonBuilder
import com.richi_mc.myapplication.data.api.dto.TicketLocalJson
import com.richi_mc.myapplication.helpers.LlmHelper
import com.richi_mc.myapplication.helpers.ModelDownloader
import kotlinx.coroutines.launch

@Composable
fun LlmScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userInput by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isInitializing by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var initError by remember { mutableStateOf<String?>(null) }

    val llmHelper = remember { LlmHelper(context) }

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0) }
    var isModelReady by remember { mutableStateOf(false) }

// Al entrar, verifica si el modelo ya está descargado
    LaunchedEffect(Unit) {
        isModelReady = ModelDownloader.isModelDownloaded(context)
        if (isModelReady) {
            // Ya está descargado, inicializa directo
            llmHelper.initialize()
                .onSuccess { isInitializing = false }
                .onFailure { e ->
                    isInitializing = false
                    initError = "Error al cargar el modelo: ${e.message}"
                }
        } else {
            isInitializing = false // No inicializa aún, espera descarga
        }
    }

    DisposableEffect(Unit) {
        onDispose { llmHelper.close() }
    }

    fun sendPrompt() {
        if (userInput.isBlank() || isLoading) return
        val prompt = userInput.trim()
        isLoading = true
        response = ""
        errorMessage = null

        scope.launch {
            llmHelper.generateTicketJson(prompt)
                .onSuccess { rawJson ->
                    try {
                        // 1. Configuramos Gson para leer y formatear bonito
                        val gson = GsonBuilder().setPrettyPrinting().create()

                        // 2. Convertimos el JSON crudo del LLM a nuestro objeto Kotlin
                        var ticketParcial = gson.fromJson(rawJson, TicketLocalJson::class.java)

                        // 3. LA MAGIA: Hacemos la matemática determinista
                        val gastoHormigaCalculado = ticketParcial.productos
                            .filter { it.esGastoHormiga }
                            .sumOf { it.precio }

                        val ahorroProyectado = gastoHormigaCalculado * 4

                        val mensajeLocal = if (gastoHormigaCalculado > 0) {
                            "Identificamos $${gastoHormigaCalculado} en gastos hormiga. Si los evitas, podrías ahorrar $${ahorroProyectado} al mes."
                        } else {
                            "¡Excelente compra! No detectamos gastos hormiga en este ticket."
                        }

                        // 4. Actualizamos el objeto con los valores perfectos
                        ticketParcial.gastoHormigaTicket = gastoHormigaCalculado
                        ticketParcial.ahorroTotalHormigaMensual = ahorroProyectado
                        ticketParcial.mensajeEducativo = mensajeLocal

                        // 5. Lo volvemos a convertir a texto para mostrarlo en la UI
                        response = gson.toJson(ticketParcial)
                        isLoading = false

                    } catch (e: Exception) {
                        // Si el LLM alucina y el JSON se rompe, mostramos el error y el texto crudo
                        errorMessage = "Error al estructurar JSON: ${e.message}"
                        response = "JSON CRUDO DEL LLM:\n$rawJson"
                        isLoading = false
                    }
                }
                .onFailure { e ->
                    errorMessage = "Error del LLM: ${e.message}"
                    isLoading = false
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text("Prueba", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Inferencia local con formato estructurado", fontSize = 14.sp, color = Color(0xFF888888))

        // Estado de inicialización
        AnimatedVisibility(visible = isInitializing) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A1A))
                    .padding(16.dp)
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4ADE80),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text("Cargando modelo Gemma...", color = Color.White, fontSize = 14.sp)
                    Text("Solo tarda la primera vez", color = Color(0xFF555555), fontSize = 12.sp)
                }
            }
        }

        // Error de inicialización
        initError?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2D1515))
                    .padding(16.dp)
            ) {
                Text(it, color = Color(0xFFFF6B6B), fontSize = 14.sp)
            }
        }

        // Bloque de descarga — se muestra si el modelo no está listo
        AnimatedVisibility(visible = !isModelReady && !isInitializing) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⬇️", fontSize = 36.sp)
                Text(
                    "El modelo Gemma (~1.5GB) no está descargado",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    "Solo se descarga una vez. Requiere WiFi.",
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                if (isDownloading) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF4ADE80),
                            trackColor = Color(0xFF333333)
                        )
                        Text(
                            "$downloadProgress%",
                            color = Color(0xFF4ADE80),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isDownloading = true
                            errorMessage = null
                            scope.launch {
                                ModelDownloader.downloadModel(context) { progress ->
                                    downloadProgress = progress
                                }.onSuccess {
                                    isModelReady = true
                                    isDownloading = false
                                    isInitializing = true
                                    llmHelper.initialize()
                                        .onSuccess { isInitializing = false }
                                        .onFailure { e ->
                                            isInitializing = false
                                            initError = "Error al cargar: ${e.message}"
                                        }
                                }.onFailure { e ->
                                    isDownloading = false
                                    errorMessage = "Error al descargar: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
                    ) {
                        Text("Descargar modelo", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Entrada de texto
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Pega aquí el texto crudo del OCR del ticket...", color = Color(0xFF555555)) },
            label = { Text("Texto del Ticket", color = Color(0xFF888888)) },
            minLines = 3,
            maxLines = 6,
            enabled = !isInitializing && initError == null,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4ADE80),
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF4ADE80),
                disabledBorderColor = Color(0xFF222222),
                disabledTextColor = Color(0xFF444444)
            ),
            shape = RoundedCornerShape(16.dp)
        )

        // Botón enviar
        Button(
            onClick = { sendPrompt() },
            enabled = !isInitializing && !isLoading && userInput.isNotBlank() && initError == null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4ADE80),
                disabledContainerColor = Color(0xFF1F3D2A)
            )
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4ADE80),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Generando respuesta...", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    "Extraer Datos (JSON)",
                    color = if (!isInitializing && userInput.isNotBlank()) Color.Black else Color(0xFF4ADE80),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Error de generación
        errorMessage?.let {
            Text(it, color = Color(0xFFFF6B6B), fontSize = 13.sp)
        }

        // Respuesta del LLM
        AnimatedVisibility(
            visible = response.isNotEmpty(),
            enter = fadeIn() + slideInVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🤖", fontSize = 16.sp)
                    Text(
                        "JSON resultante",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
                HorizontalDivider(color = Color(0xFF2A2A2A))
                Text(
                    text = response,
                    color = Color(0xFFEEEEEE),
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    lineHeight = 26.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}