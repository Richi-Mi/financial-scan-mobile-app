package com.richi_mc.myapplication.ui.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richi_mc.myapplication.R
import com.richi_mc.myapplication.data.api.FinancialScanApiService
import com.richi_mc.myapplication.data.localimport.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: FinancialScanApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                // 1. Obtener datos locales (Nombre)
                val userName = userPreferences.userNameFlow.first() ?: "Usuario"

                val userId = userPreferences.userIdFlow.first()
                // 2. Obtener datos de la API (Scores)
                val response = apiService.getGeneral(userId?: "")

                if (response.isSuccessful && response.body()?.ok == true) {
                    val score = response.body()!!.score

                    // Usamos el nuevo nombre seguro: analisisGeneral
                    val analisis = response.body()!!.analisisGeneral

                    val tipsList = mutableListOf<MejoraTip>()

                    // Verificamos de forma segura
                    if (analisis != null && analisis.insights_clave != null) {
                        val rawInsights = analisis.insights_clave

                        try {
                            for (i in 0 until rawInsights.size step 2) {
                                if (i + 1 < rawInsights.size) {
                                    val tituloObj = rawInsights[i]
                                    val detalleObj = rawInsights[i + 1]

                                    tipsList.add(
                                        MejoraTip(
                                            iconId = com.richi_mc.myapplication.R.drawable.ic_launcher_foreground,
                                            titulo = tituloObj["titulo"] as? String ?: "Sugerencia",
                                            texto = detalleObj["explicacion"] as? String ?: "",
                                            ahorroEstimado = (detalleObj["impacto_estimado_mxn_mes"] as? Double)?.toInt() ?: 0
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            // Error silencioso
                        }
                    }

                    // Si la IA no devolvió tips, ponemos uno por defecto
                    if (tipsList.isEmpty()) {
                        tipsList.add(
                            MejoraTip(
                                iconId = com.richi_mc.myapplication.R.drawable.ic_launcher_foreground,
                                titulo = "Registra más gastos",
                                texto = "Necesitamos más información para darte consejos.",
                                ahorroEstimado = 0
                            )
                        )
                    }

                    // Actualizamos la UI
                    _uiState.value = ProfileUiState.Success(
                        userName = userName,
                        miembroDesde = "abril 2026",
                        scoreFactores = score!!.factores, // score asume que no es null porque el ok es true
                        resumenIA = analisis?.resumen_ejecutivo ?: "Sigue registrando tus gastos...",
                        mensajeMotivacional = analisis?.mensaje_motivacional ?: "¡Sigue adelante!",
                        tips = tipsList
                    )
                } else {
                    _uiState.value = ProfileUiState.Error("No se pudieron cargar los datos del servidor.")
                }

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}