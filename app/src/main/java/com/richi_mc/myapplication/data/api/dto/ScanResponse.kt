package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class ScanResponse(
    val ok: Boolean,
    val ticket: TicketData,
    val score: ScoreData,
    @SerializedName("procesado_en_ms") val procesadoEnMs: Long
)
