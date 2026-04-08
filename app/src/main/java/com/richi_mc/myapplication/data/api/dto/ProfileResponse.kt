package com.richi_mc.myapplication.data.api.dto

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val ok: Boolean,
    val score: ProfileScoreData, // En tu JSON se llama "score"
    @SerializedName("analisis_general")
    val analisisGeneral: EstadoAnalisisGeneral?
)