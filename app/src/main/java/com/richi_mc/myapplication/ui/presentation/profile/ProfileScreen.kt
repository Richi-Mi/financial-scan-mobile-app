package com.richi_mc.myapplication.ui.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        // Usamos el color de fondo del tema
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is ProfileUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }
                }
                is ProfileUiState.Success -> {
                    SuccessProfileContent(state = state, onLogout = {})
                }
            }
        }
    }
}

@Composable
fun SuccessProfileContent(
    state: ProfileUiState.Success,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // --- SECCIÓN HEADER ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface) // Fondo de superficie para contraste
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circular con iniciales
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer), // Fondo suave primario
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.userName.take(1).uppercase() +
                                state.userName.substringAfter(" ", "").take(1).uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer // Texto sobre el contenedor primario
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Texto principal
                )
                Text(
                    text = "Miembro desde ${state.miembroDesde}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Texto secundario
                )
            }
        }

        // --- SECCIÓN RESUMEN IA ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Análisis de Gemini",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.resumenIA,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // --- SECCIÓN CÓMO SE CALCULA TU SCORE ---
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp)) {
                Text(
                    "Cómo se calcula tu score",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Grid 2x2 de factores
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ScoreFactorCard(
                        modifier = Modifier.weight(1f),
                        title = "Frecuencia",
                        score = state.scoreFactores.frecuencia,
                        maxScore = 35
                    )
                    ScoreFactorCard(
                        modifier = Modifier.weight(1f),
                        title = "Control hormiga",
                        score = state.scoreFactores.control_hormiga,
                        maxScore = 25
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ScoreFactorCard(
                        modifier = Modifier.weight(1f),
                        title = "Tendencia",
                        score = state.scoreFactores.tendencia,
                        maxScore = 25
                    )
                    ScoreFactorCard(
                        modifier = Modifier.weight(1f),
                        title = "Diversidad",
                        score = state.scoreFactores.diversidad_categorias,
                        maxScore = 15
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


            }
        }

        // --- SECCIÓN PARA MEJORAR TU SCORE ---
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    "Para mejorar tu score",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Mapeamos los tips
                state.tips.forEach { tip ->
                    TipCard(tip = tip)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Explicación de los parámetros
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ParameterExplanation(
                    title = "Frecuencia",
                    description = "Premia la constancia de registro. El objetivo ideal es escanear al menos 8 tickets al mes."
                )
                ParameterExplanation(
                    title = "Control hormiga",
                    description = "Mide el impacto de tus gastos pequeños. Mantenerlo por debajo del 40% del total es clave."
                )
                ParameterExplanation(
                    title = "Tendencia",
                    description = "Compara tu desempeño con el mes anterior. Reconoce tu esfuerzo por mejorar tus hábitos."
                )
                ParameterExplanation(
                    title = "Diversidad",
                    description = "Valora que registres gastos en 4 o más categorías para un análisis financiero completo."
                )
            }
        }
    }
}

// --- SUB-COMPONENTES DE UI ---

@Composable
fun ParameterExplanation(title: String, description: String) {
    Column (modifier = Modifier.alpha(0.5f)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun ScoreFactorCard(
    modifier: Modifier = Modifier,
    title: String,
    score: Int,
    maxScore: Int
) {
    val progress = if (maxScore > 0) score.toFloat() / maxScore else 0f
    val statusColor = when {
        progress >= 0.8f -> Color(0xFF4CAF50) // Verde (Excelente)
        progress >= 0.6f -> Color(0xFF8BC34A) // Lima (Saludable)
        progress >= 0.4f -> Color(0xFFFFC107) // Ámbar (Regular)
        progress >= 0.2f -> Color(0xFFFF9800) // Naranja (En riesgo)
        else -> Color(0xFFF44336)             // Rojo (Crítico)
    }

    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)

            Row(verticalAlignment = Alignment.Bottom) {
                Text("$score", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = statusColor)
                Text("/$maxScore", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
            }

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun TipCard(tip: MejoraTip) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = tip.iconId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(tip.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    if (tip.ahorroEstimado > 0) {
                        // Aquí mantenemos un verde fijo para el dinero, ya que Material3 no tiene un token "Success" por defecto,
                        // pero usamos Color(0xFF4CAF50) que se ve bien en ambos modos.
                        Text(
                            text = "Ahorro potencial: $${tip.ahorroEstimado} /mes",
                            color = Color(0xFF4CAF50),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(tip.texto, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
        }
    }
}