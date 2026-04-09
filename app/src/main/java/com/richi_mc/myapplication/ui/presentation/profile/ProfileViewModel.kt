package com.richi_mc.myapplication.ui.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.richi_mc.myapplication.R
import com.richi_mc.myapplication.data.api.FinancialScanApiService
import com.richi_mc.myapplication.data.api.dto.ProfileResponse
import com.richi_mc.myapplication.data.api.dto.ScoreFactores
import com.richi_mc.myapplication.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: FinancialScanApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val gson = Gson()

    // El estado se deriva directamente de las preferencias de usuario para que se actualice solo
    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferences.userNameFlow,
        userPreferences.userResumenIaFlow,
        userPreferences.userScoreFactoresFlow,
        userPreferences.userTipsFlow
    ) { name, resumen, factoresJson, tipsJson ->
        if (resumen == null || factoresJson == null || tipsJson == null) {
            ProfileUiState.Loading
        } else {
            try {
                val factores = gson.fromJson(factoresJson, ScoreFactores::class.java)
                val tips = gson.fromJson(tipsJson, Array<MejoraTip>::class.java).toList()

                ProfileUiState.Success(
                    userName = name ?: "Usuario",
                    miembroDesde = "abril 2024",
                    scoreFactores = factores,
                    resumenIA = resumen,
                    tips = tips
                )
            } catch (e: Exception) {
                ProfileUiState.Error("Error al procesar datos locales")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState.Loading
    )

    init {
        // Al iniciar, intentamos refrescar los datos en segundo plano
        refreshProfileData()
    }

    fun refreshProfileData() {
        viewModelScope.launch {
            try {
                val userId = userPreferences.userIdFlow.first() ?: return@launch
                val response = apiService.getGeneral(userId)

                if (response.isSuccessful && response.body()?.ok == true) {
                    saveResponseToPreferences(response.body()!!)
                }
            } catch (e: Exception) {
                // Error silencioso en background
            }
        }
    }

    private suspend fun saveResponseToPreferences(profile: ProfileResponse) {
        val score = profile.score
        val analisis = profile.analisisGeneral

        // 1. Guardar Score IA (usado en Home)
        userPreferences.saveUserScoreIa(score.valor.toString())

        // 2. Procesar Tips
        val tipsList = mutableListOf<MejoraTip>()
        if (analisis?.insights_clave != null) {
            val rawInsights = analisis.insights_clave
            try {
                for (i in 0 until rawInsights.size step 2) {
                    if (i + 1 < rawInsights.size) {
                        val tituloObj = rawInsights[i]
                        val detalleObj = rawInsights[i + 1]
                        tipsList.add(
                            MejoraTip(
                                iconId = R.drawable.ic_launcher_foreground,
                                titulo = tituloObj["titulo"] as? String ?: "Sugerencia",
                                texto = detalleObj["explicacion"] as? String ?: "",
                                ahorroEstimado = (detalleObj["impacto_estimado_mxn_mes"] as? Double)?.toInt() ?: 0
                            )
                        )
                    }
                }
            } catch (e: Exception) {}
        }

        if (tipsList.isEmpty()) {
            tipsList.add(MejoraTip(R.drawable.ic_launcher_foreground, "Registra más gastos", "Necesitamos más información.", 0))
        }

        // 3. Serializar y Guardar en DataStore
        userPreferences.saveProfileData(
            resumen = analisis?.resumen_ejecutivo ?: "Sigue registrando tus gastos...",
            factoresJson = gson.toJson(score.factores),
            tipsJson = gson.toJson(tipsList)
        )
    }
}