package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class StatsMes(
    @SerializedName("mes_actual") val mesActual: String,
    @SerializedName("total_escaneado") val totalEscaneado: Double,
    @SerializedName("total_gasto_hormiga") val totalGastoHormiga: Double,
    @SerializedName("ahorro_proyectado") val ahorroProyectado: Double,
    @SerializedName("tickets_escaneados") val ticketsEscaneados: Int
)