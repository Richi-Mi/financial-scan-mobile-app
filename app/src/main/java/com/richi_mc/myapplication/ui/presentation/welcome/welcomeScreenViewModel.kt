package com.richi_mc.myapplication.ui.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richi_mc.myapplication.data.api.FinancialScanApiService
import com.richi_mc.myapplication.data.api.dto.CreateUserRequest
import com.richi_mc.myapplication.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class welcomeScreenViewModel @Inject constructor(
    private val apiService: FinancialScanApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _appState = MutableStateFlow<SplashState>(SplashState.Loading)
    val appState: StateFlow<SplashState> = _appState

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            try {
                val existingUserId = userPreferences.userIdFlow.first()

                if (!existingUserId.isNullOrBlank()) {
                    // Ya está registrado, pasamos al Home
                    _appState.value = SplashState.Success
                } else {
                    // No está registrado, pedimos el nombre en la UI
                    _appState.value = SplashState.RequireName
                }
            } catch (e: Exception) {
                _appState.value = SplashState.Error("Error al leer datos locales.")
            }
        }
    }

    fun registerUser(name: String) {
        if (name.isBlank()) return

        _appState.value = SplashState.Registering

        viewModelScope.launch {
            try {
                val request = CreateUserRequest(nombre = name)
                val response = apiService.createUser(request)

                if (response.isSuccessful && response.body() != null) {
                    val newUserId = response.body()!!.userId

                    // Guardamos ID y Nombre localmente
                    userPreferences.saveUserData(userId = newUserId, userName = name)

                    // Listo, pasamos a la app principal
                    _appState.value = SplashState.Success
                } else {
                    _appState.value = SplashState.Error("Error del servidor al registrar usuario.")
                }
            } catch (e: Exception) {
                _appState.value = SplashState.Error("Revisa tu conexión a internet.")
            }
        }
    }

    fun resetToInput() {
        _appState.value = SplashState.RequireName
    }
}

// Los posibles estados de nuestra pantalla inicial
sealed class SplashState {
    object Loading : SplashState()
    object RequireName : SplashState()
    object Registering : SplashState()
    object Success : SplashState()
    data class Error(val message: String) : SplashState()
}