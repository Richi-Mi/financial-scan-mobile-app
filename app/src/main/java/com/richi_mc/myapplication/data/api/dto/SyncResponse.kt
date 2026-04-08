package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class SyncResponse(
    val ok: Boolean,
    @SerializedName("ticket_id") val ticketId: String,
    val score: ScoreData,
    @SerializedName("procesado_en_ms") val procesadoEnMs: Long
)
