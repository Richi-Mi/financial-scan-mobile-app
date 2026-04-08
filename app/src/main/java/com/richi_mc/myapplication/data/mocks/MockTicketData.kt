package com.richi_mc.myapplication.data.mocks

import com.richi_mc.myapplication.data.model.TicketEntity
import java.util.Date
import java.util.concurrent.TimeUnit

object MockTicketData {

    /**
     * Obtiene una lista de tickets falsos para previsualización
     */
    fun getMockTickets(): List<TicketEntity> {
        val now = Date()

        return listOf(
            // Hace 2 horas
            TicketEntity(
                id = 1,
                date = Date(now.time - TimeUnit.HOURS.toMillis(2)),
                trade = "OXXO",
                productName = "Café con leche",
                price = 87.50f,
                category = "Hormiga"
            ),

            // Ayer
            TicketEntity(
                id = 2,
                date = Date(now.time - TimeUnit.DAYS.toMillis(1)),
                trade = "Walmart",
                productName = "Despensa semanal",
                price = 432.00f,
                category = "Comida"
            ),

            // Hace 3 días
            TicketEntity(
                id = 3,
                date = Date(now.time - TimeUnit.DAYS.toMillis(3)),
                trade = "Starbucks",
                productName = "Venti Iced Coffee",
                price = 125.00f,
                category = "Hormiga"
            ),

            // Hace 5 días
            TicketEntity(
                id = 4,
                date = Date(now.time - TimeUnit.DAYS.toMillis(5)),
                trade = "McDonald's",
                productName = "Big Mac Combo",
                price = 189.50f,
                category = "Comida"
            ),

            // Hace 1 semana
            TicketEntity(
                id = 5,
                date = Date(now.time - TimeUnit.DAYS.toMillis(7)),
                trade = "Cinépolis",
                productName = "2 Entradas + Palomitas",
                price = 560.00f,
                category = "Entretenimiento"
            ),

            // Hace 8 días
            TicketEntity(
                id = 6,
                date = Date(now.time - TimeUnit.DAYS.toMillis(8)),
                trade = "Uber",
                productName = "Viaje Centro-Casa",
                price = 234.75f,
                category = "Transporte"
            ),

            // Hace 10 días
            TicketEntity(
                id = 7,
                date = Date(now.time - TimeUnit.DAYS.toMillis(10)),
                trade = "Farmacias del Dr Surtido",
                productName = "Medicinas varias",
                price = 356.25f,
                category = "Salud"
            ),

            // Hace 12 días
            TicketEntity(
                id = 8,
                date = Date(now.time - TimeUnit.DAYS.toMillis(12)),
                trade = "Amazon",
                productName = "Funda para celular",
                price = 287.99f,
                category = "Compras"
            ),

            // Hace 15 minutos
            TicketEntity(
                id = 9,
                date = Date(now.time - TimeUnit.MINUTES.toMillis(15)),
                trade = "7-Eleven",
                productName = "Refresco + Snack",
                price = 45.50f,
                category = "Hormiga"
            ),

            // Hace 1 hora
            TicketEntity(
                id = 10,
                date = Date(now.time - TimeUnit.HOURS.toMillis(1)),
                trade = "Costco",
                productName = "Compra mensual",
                price = 1245.80f,
                category = "Comida"
            ),

            // Hace 2 días
            TicketEntity(
                id = 11,
                date = Date(now.time - TimeUnit.DAYS.toMillis(2)),
                trade = "Spotify",
                productName = "Suscripción Premium",
                price = 159.00f,
                category = "Entretenimiento"
            ),

            // Hace 4 días
            TicketEntity(
                id = 12,
                date = Date(now.time - TimeUnit.DAYS.toMillis(4)),
                trade = "Pemex",
                productName = "Gasolina Premium 40L",
                price = 892.40f,
                category = "Transporte"
            )
        )
    }

    /**
     * Retorna un solo ticket para previsualización individual
     */
    fun getSingleMockTicket(): TicketEntity {
        val now = Date()
        return TicketEntity(
            id = 1,
            date = Date(now.time - TimeUnit.HOURS.toMillis(2)),
            trade = "OXXO",
            productName = "Café con leche",
            price = 87.50f,
            category = "Hormiga"
        )
    }

    /**
     * Calcula el gasto total y hormiga para previsualización
     */
    fun calculateExpenseSummary(tickets: List<TicketEntity>): Pair<Float, Float> {
        val totalExpense = tickets.sumOf { it.price.toDouble() }.toFloat()
        val antExpense = tickets
            .filter { it.category.lowercase() == "hormiga" }
            .sumOf { it.price.toDouble() }
            .toFloat()

        return Pair(totalExpense, antExpense)
    }

    /**
     * Calcula el porcentaje de salud financiera basado en los gastos
     * (Mayor gasto hormiga = menor salud)
     */
    fun calculateHealthPercentage(tickets: List<TicketEntity>): Int {
        val (totalExpense, antExpense) = calculateExpenseSummary(tickets)

        return if (totalExpense == 0f) {
            100
        } else {
            val antPercentage = (antExpense / totalExpense) * 100
            val health = 100 - antPercentage.toInt()
            health.coerceIn(0, 100)
        }
    }
}