package com.richi_mc.myapplication.ui.presentation.history

import com.richi_mc.myapplication.ui.presentation.home.getCategoryColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.richi_mc.myapplication.data.model.TicketEntity
import com.richi_mc.myapplication.ui.theme.FinantialScanTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisTicketsScreen() {
    val historyViewModel: HistoryViewModel = hiltViewModel()

    // Observamos los tickets desde el ViewModel
    val allTickets by historyViewModel.tickets.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredTickets = remember(selectedFilter, allTickets) {
        when (selectedFilter) {
            "Este mes" -> {
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)
                allTickets.filter { ticket ->
                    val ticketCal = Calendar.getInstance().apply { time = ticket.date }
                    ticketCal.get(Calendar.MONTH) == currentMonth &&
                            ticketCal.get(Calendar.YEAR) == currentYear
                }
            }
            "Con hormiga" -> allTickets.filter { it.category.equals("Hormiga", ignoreCase = true) }
            else -> allTickets
        }
    }

    val currentMonthName = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Título de la pantalla
            Text(
                text = "Mis tickets",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
            )

            // Chips de Filtro
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // Tarjeta de Resumen (Mes actual o filtro seleccionado)
            val summaryTitle = when (selectedFilter) {
                "Este mes" -> currentMonthName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                "Con hormiga" -> "Gastos hormiga"
                else -> "Todos los registros"
            }

            SummaryCard(
                month = summaryTitle,
                ticketCount = filteredTickets.size,
                total = filteredTickets.sumOf { it.price.toDouble() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Tickets
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Espacio extra al final
            ) {
                items(filteredTickets) { ticket ->
                    Ticket(ticket = ticket)
                }
            }
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val chips = listOf("Todos", "Este mes", "Con hormiga")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips) { chip ->
            val isSelected = selectedFilter == chip

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onFilterSelected(chip) },
                // Si está seleccionado usa el color primario, sino usa un color de superficie
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = chip,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SummaryCard(month: String, ticketCount: Int, total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(
            // Un tono tenue para el fondo de la tarjeta resumen (puedes ajustarlo a tu tema)
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = month,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$ticketCount tickets  ·  $${"%,.0f".format(total)} total",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

// Helper para obtener un ícono dependiendo del comercio
@Composable
fun getIconForTrade(trade: String): ImageVector {
    return when (trade.lowercase()) {
        "oxxo" -> Icons.Outlined.Storefront
        "walmart" -> Icons.Outlined.ShoppingCart
        "7-eleven" -> Icons.Outlined.LocalCafe
        "farmacias guadalajara" -> Icons.Outlined.MedicalServices
        else -> Icons.Outlined.Receipt
    }
}

// Tu componente Ticket actualizado con el ícono izquierdo
@Composable
fun Ticket(
    ticket: TicketEntity,
    modifier: Modifier = Modifier
) {
    // Estas funciones asumen que están importadas de tu archivo de utilidades
    val categoryColor = getCategoryColor(ticket.category)
    // Formato de fecha: solo día y mes (ej: 15 Oct)
    val formattedDate = remember(ticket.date) {
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(ticket.date)
    }

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

            // --- NUEVO: Ícono del comercio ---
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForTrade(ticket.trade),
                    contentDescription = ticket.trade,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información del ticket
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = ticket.trade,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            }

            // Columna para Categoría (arriba) y Precio (abajo)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Badge de categoría
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    color = categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = ticket.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Precio
                Text(
                    text = "$${"%.2f".format(ticket.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true)
@Composable
fun MisTicketsScreenPreview() {
    FinantialScanTheme {
        MisTicketsScreen()
    }
}