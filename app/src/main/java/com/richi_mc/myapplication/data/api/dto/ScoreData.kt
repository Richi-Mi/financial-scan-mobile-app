package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class ScoreData(
    val valor: Int,
    val nivel: String,
    val factores: FactoresScore,
    @SerializedName("pct_hormiga_mes") val pctHormigaMes: Double? = null,
    @SerializedName("ultima_actualizacion") val ultimaActualizacion: String? = null
)