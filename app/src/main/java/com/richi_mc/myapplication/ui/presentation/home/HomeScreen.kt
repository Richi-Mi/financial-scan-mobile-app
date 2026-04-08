package com.richi_mc.myapplication.ui.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.hilt.navigation.compose.hiltViewModel
import com.richi_mc.myapplication.data.model.TicketEntity
import com.richi_mc.myapplication.ui.theme.FinantialScanTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "comida", "alimentos" -> Color(0xFFE91E63) // Rosa
        "transporte" -> Color(0xFF2196F3) // Azul
        "hormiga" -> Color(0xFFFFA500) // Naranja
        "entretenimiento" -> Color(0xFF9C27B0) // Púrpura
        "salud" -> Color(0xFF4CAF50) // Verde
        "compras" -> Color(0xFF00BCD4) // Cyan
        else -> Color(0xFF757575) // Gris
    }
}

/**
 * Convierte una fecha a tiempo relativo ("hace 2 horas", "ayer", etc)
 */
fun getRelativeTime(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return when {
        diffInMinutes < 60 -> "hace $diffInMinutes minutos"
        diffInHours < 24 -> "hace $diffInHours horas"
        diffInDays == 1L -> "ayer"
        diffInDays < 7 -> "hace $diffInDays días"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(date)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val homeViewModel : HomeViewModel = hiltViewModel()

    val tickets by homeViewModel.tickets.collectAsState()
    val totalExpense by homeViewModel.totalExpense.collectAsState()
    val antExpense by homeViewModel.antExpense.collectAsState()
    val healthPercentage by homeViewModel.healthPercentage.collectAsState()
    val categoryDistribution by homeViewModel.categoryDistribution.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    // TODO: Change with the real username
                    Column {
                        Text(
                            text = "Hola Carlos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = homeViewModel.getYearMonth(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Salud Financiera
            item {
                FinancialHealth(healthPercentage = healthPercentage)
            }
            item {
                PieChartDistribution(categoryDistribution)
            }

            // Card Info
            item {
                CardInfo(totalExpense, antExpense)
            }

            // Título Tickets Recientes
            item {
                Text(
                    text = "Tickets recientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Lista de Tickets (solo los primeros 3)
            val recentTickets = tickets.take(3)
            items(recentTickets.size) { index ->
                Ticket(ticket = recentTickets[index])
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FinancialHealth(
    healthPercentage: Int = 72,
    healthStatus: String = "Saludable",
    statusColor: Color = Color(0xFF4CAF50),
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Porcentaje
            Text(
                text = "$healthPercentage%",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Etiqueta "Salud financiera"
            Text(
                text = "Salud financiera",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )

            // Badge de estado
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp)),
                color = statusColor
            ) {
                Text(
                    text = healthStatus,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PieChartDistribution(
    categoryDistribution: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (categoryDistribution.isEmpty()) return

    val total = categoryDistribution.sumOf { it.second }
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f

            categoryDistribution.forEach { (category, amount) ->
                val sweepAngle = (amount / total * 360).toFloat()
                val color = getCategoryColor(category)

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = size.copy(
                        width = size.width * 0.7f,
                        height = size.height * 0.7f
                    ),
                    topLeft = Offset(
                        x = (size.width - size.width * 0.7f) / 2,
                        y = (size.height - size.height * 0.7f) / 2
                    )
                )

                startAngle += sweepAngle
            }

            // Círculo del color del fondo en el centro para efecto donut
            drawCircle(
                color = primaryColor,
                radius = size.minDimension * 0.25f,
                center = Offset(
                    x = size.width / 2,
                    y = size.height / 2
                )
            )
        }
    }
}


@Composable
fun CardInfo(
    totalExpense: Float = 3240f,
    antExpense: Float = 480f,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Gasto Total
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Gasto total",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "$${"%.0f".format(totalExpense)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Gasto Hormiga
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Gasto hormiga",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "$${"%.0f".format(antExpense)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA500) // Naranja
                )
            }
        }
    }
}
@Composable
fun Ticket(
    ticket: TicketEntity,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(ticket.category)
    val relativeTime = getRelativeTime(ticket.date)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del ticket (lado izquierdo)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = ticket.trade,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = relativeTime,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            }

            // Precio (centro)
            Text(
                text = "$${"%.2f".format(ticket.price)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Badge de categoría (lado derecho)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp)),
                color = categoryColor
            ) {
                Text(
                    text = ticket.category,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    FinantialScanTheme {
        HomeScreen()
    }
}