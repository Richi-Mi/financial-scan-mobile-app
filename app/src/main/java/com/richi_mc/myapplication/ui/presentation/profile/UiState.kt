package com.richi_mc.myapplication.ui.presentation.profile

import com.richi_mc.myapplication.data.api.dto.ScoreFactores

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val userName: String,
        val miembroDesde: String,
        val scoreFactores: ScoreFactores,
        val resumenIA: String,
        val tips: List<MejoraTip>
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

// Actualizamos MejoraTip para incluir el ahorro
data class MejoraTip(
    val iconId: Int,
    val titulo: String,
    val texto: String,
    val ahorroEstimado: Int
)