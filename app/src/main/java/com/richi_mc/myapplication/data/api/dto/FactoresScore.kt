package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class FactoresScore(
    @SerializedName("base_acumulativa") val baseAcumulativa: Int,
    val frecuencia: Int,
    @SerializedName("control_hormiga") val controlHormiga: Int,
    val tendencia: Int,
    @SerializedName("diversidad_categorias") val diversidadCategorias: Int
)