package com.richi_mc.myapplication.data.api.dto

import com.google.gson.annotations.SerializedName

data class Producto(
    val nombre: String,
    val precio: Double,
    val categoria: String,
    @SerializedName("es_gasto_hormiga") val esGastoHormiga: Boolean
)