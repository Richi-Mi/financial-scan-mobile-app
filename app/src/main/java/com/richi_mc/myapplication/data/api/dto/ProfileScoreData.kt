package com.richi_mc.myapplication.data.api.dto

data class ProfileScoreData(
    val valor: Int,
    val nivel: String,
    val factores: ScoreFactores,
    val ultima_actualizacion: String
)
