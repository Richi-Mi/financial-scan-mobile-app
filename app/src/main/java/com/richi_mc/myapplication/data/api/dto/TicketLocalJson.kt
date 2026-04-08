package com.richi_mc.myapplication.data.api.dto

import com.google.gson.annotations.SerializedName


// Este es el JSON que genera el SLM en el dispositivo
data class TicketLocalJson(
    val comercio: String,
    val fecha: String,
    val total: Double,
    val productos: List<Producto>,
    @SerializedName("gasto_hormiga_ticket") var gastoHormigaTicket: Double,
    @SerializedName("ahorro_total_hormiga_mensual") var ahorroTotalHormigaMensual: Double,
    @SerializedName("mensaje_educativo") var mensajeEducativo: String
)