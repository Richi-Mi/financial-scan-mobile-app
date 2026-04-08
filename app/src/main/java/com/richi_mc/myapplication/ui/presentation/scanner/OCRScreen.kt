package com.richi_mc.myapplication.ui.presentation.scanner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.*
import com.richi_mc.myapplication.helpers.TextRecognitionHelper
import kotlinx.coroutines.launch

@Composable
fun OcrScreen(
    viewModel: OCRViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as androidx.activity.ComponentActivity

    // ── Estados Locales (Solo UI y ML Kit) ──
    var scannedUri by remember { mutableStateOf<Uri?>(null) }
    var extractedText by remember { mutableStateOf("") }
    var isExtracting by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) } // Renombrado para no chocar con el del ViewModel

    // ── Estados del ViewModel (Red y Lógica de Negocio) ──
    val apiResponse by viewModel.apiResponse.collectAsStateWithLifecycle()
    val isSendingToApi by viewModel.isSendingToApi.collectAsStateWithLifecycle()
    val apiError by viewModel.errorMessage.collectAsStateWithLifecycle()

    // ── 1. Configurar el Document Scanner ──────────────────────────────────
    val scannerOptions = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setPageLimit(1)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .build()

    val scanner = GmsDocumentScanning.getClient(scannerOptions)

    // Launcher que recibe el Intent del scanner
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val firstPageUri = scanningResult?.pages?.firstOrNull()?.imageUri

            if (firstPageUri != null) {
                scannedUri = firstPageUri
                isExtracting = true
                extractedText = ""
                localErrorMessage = null

                // Opcional: Podrías crear una función en el ViewModel para limpiar el response anterior
                viewModel.clearResponse()

                scope.launch {
                    try {
                        extractedText = TextRecognitionHelper.extractText(context, firstPageUri)
                    } catch (e: Exception) {
                        localErrorMessage = "Error al extraer texto: ${e.message}"
                    } finally {
                        isExtracting = false
                    }
                }
            }
        } else if (result.resultCode != android.app.Activity.RESULT_CANCELED) {
            localErrorMessage = "Error al escanear el documento"
        }
    }

    // (La función local sendTextToBackend fue eliminada por completo)

    // ── 2. UI ───────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            "Lector OCR",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Escanea un documento.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Vista previa del documento escaneado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (scannedUri != null) {
                AsyncImage(
                    model = scannedUri,
                    contentDescription = "Documento escaneado",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗒️", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sin documento",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Botón de Escanear
        Button(
            onClick = {
                scanner.getStartScanIntent(activity)
                    .addOnSuccessListener { intentSender ->
                        scannerLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    }
                    .addOnFailureListener { e ->
                        localErrorMessage = "No se pudo iniciar el scanner: ${e.message}"
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = !isExtracting && !isSendingToApi
        ) {
            Text(
                text = if (scannedUri == null) "Escanear documento" else "Escanear otro",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Loading local (ML Kit)
        AnimatedVisibility(visible = isExtracting) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Extrayendo texto...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Manejo consolidado de Errores (Muestra el error local o el de red)
        val displayError = localErrorMessage ?: apiError
        displayError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        // Botón para procesar texto extraído con el Backend
        AnimatedVisibility(visible = extractedText.isNotEmpty() && apiResponse == null) {
            Button(
                onClick = { viewModel.sendTextToBackend(extractedText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                enabled = !isSendingToApi
            ) {
                if (isSendingToApi) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Analizando con IA...", color = MaterialTheme.colorScheme.onSecondary)
                } else {
                    Text(
                        "Procesar Ticket (Cloud)",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Resultado Final del Backend
        AnimatedVisibility(
            visible = apiResponse != null,
            enter = fadeIn() + slideInVertically()
        ) {
            apiResponse?.let { response ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Análisis Exitoso",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${response.procesadoEnMs} ms",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        "Comercio: ${response.ticket.comercio}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Total: $${response.ticket.total}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Gasto Hormiga: $${response.ticket.gastoHormiga}",
                        color = Color(0xFFFFA500) // Consistente con HomeScreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = response.ticket.mensajeEducativo,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        "Nuevo Score IA: ${response.score.valor} (${response.score.nivel})",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}