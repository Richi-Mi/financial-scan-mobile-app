package com.richi_mc.myapplication.ui.presentation.record

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.richi_mc.myapplication.data.model.TicketEntity
import com.richi_mc.myapplication.ui.components.TicketItem

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpeechScreen() {

    val viewModel: SpeechViewModel = hiltViewModel()

    val isListening = viewModel.isListening
    val partialText = viewModel.partialText
    val finalText = viewModel.finalText
    val errorMessage = viewModel.errorMessage
    val isLoading = viewModel.isLoading
    val lastTicket = viewModel.lastCreatedTicket

    var showEditDialog by remember { mutableStateOf(false) }

    val audioPermission = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    // Animación del botón mientras escucha
    val pulseScale by animateFloatAsState(
        targetValue = if (isListening) 1.15f else 1f,
        animationSpec = if (isListening) infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ) else tween(300),
        label = "pulse"
    )

    if (showEditDialog && lastTicket != null) {
        EditTicketDialog(
            ticket = lastTicket,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedTicket ->
                viewModel.updateTicket(updatedTicket)
                showEditDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            "Dictado de voz",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Habla y convierte tu voz a texto",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Contenedor principal que ocupa el espacio central
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Área de texto (Visible solo cuando se escucha o se procesa)
            AnimatedVisibility(
                visible = isListening || isLoading,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            width = 1.dp,
                            color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = if (finalText.isEmpty() && partialText.isEmpty()) Arrangement.Center else Arrangement.Top,
                        horizontalAlignment = if (finalText.isEmpty() && partialText.isEmpty()) Alignment.CenterHorizontally else Alignment.Start
                    ) {
                        if (finalText.isEmpty() && partialText.isEmpty() && isListening) {
                            Text(
                                text = "Escuchando...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Texto confirmado
                            if (finalText.isNotEmpty()) {
                                Text(
                                    finalText,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp
                                )
                            }
                            // Texto parcial
                            if (partialText.isNotEmpty()) {
                                Text(
                                    text = if (finalText.isEmpty()) partialText else " $partialText",
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp
                                )
                            }
                        }

                        if (isLoading) {
                            Spacer(Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Procesando con IA...",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Resultado del ticket (Visible cuando no se escucha ni se procesa, y hay un ticket)
            AnimatedVisibility(
                visible = !isListening && !isLoading && lastTicket != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "¡Ticket creado exitosamente!",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if( lastTicket != null) {
                        TicketItem(ticket = lastTicket!!)
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            OutlinedButton(
                                onClick = { showEditDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Actualizar")
                            }
                            
                            Button(
                                onClick = { viewModel.deleteTicket() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }

                }
            }
        }

        // Error
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, textAlign = TextAlign.Center)
        }

        // Botón micrófono
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable {
                        if (!audioPermission.status.isGranted) {
                            audioPermission.launchPermissionRequest()
                            return@clickable
                        }
                        if (isListening) viewModel.stopListening() else viewModel.startListening()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isListening) "Detener" else "Hablar",
                    tint = if (isListening) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = if (isListening) "Toca para detener" else "Toca para hablar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTicketDialog(
    ticket: TicketEntity,
    onDismiss: () -> Unit,
    onConfirm: (TicketEntity) -> Unit
) {
    var trade by remember { mutableStateOf(ticket.trade) }
    var productName by remember { mutableStateOf(ticket.productName) }
    var price by remember { mutableStateOf(ticket.price.toString()) }
    var category by remember { mutableStateOf(ticket.category) }

    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("comida", "alimentos", "transporte", "hormiga", "entretenimiento", "salud", "compras")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Ticket", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = trade,
                    onValueChange = { trade = it },
                    label = { Text("Comercio") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Producto") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Menú desplegable para Categoría
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedPrice = price.toFloatOrNull() ?: ticket.price
                    onConfirm(
                        ticket.copy(
                            trade = trade,
                            productName = productName,
                            price = updatedPrice,
                            category = category
                        )
                    )
                }
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}