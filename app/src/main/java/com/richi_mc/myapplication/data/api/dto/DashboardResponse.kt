package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    val ok: Boolean,
    val score: ScoreData,
    @SerializedName("stats_mes") val statsMes: StatsMes,
    @SerializedName("tickets_recientes") val ticketsRecientes: List<TicketData>
)
