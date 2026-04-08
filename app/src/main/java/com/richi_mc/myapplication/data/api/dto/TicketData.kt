package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class TicketData(
    val id: String,
    val comercio: String,
    val fecha: String,
    val total: Double,
    val productos: List<Producto>? = null, // Puede ser null en el endpoint de dashboard
    @SerializedName("gasto_hormiga") val gastoHormiga: Double,
    @SerializedName("mensaje_educativo") val mensajeEducativo: String,
    val fuente: String,
    @SerializedName("ahorro_proyectado") val ahorroProyectado: Double? = null,
    @SerializedName("created_at") val createdAt: String? = null
)