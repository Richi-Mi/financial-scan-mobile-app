package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class SyncRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("ticket_json") val ticketJson: TicketLocalJson
)