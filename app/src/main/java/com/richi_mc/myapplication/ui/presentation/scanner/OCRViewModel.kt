package com.richi_mc.myapplication.ui.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richi_mc.myapplication.data.api.FinancialScanApiService
import com.richi_mc.myapplication.data.api.dto.ScanRequest
import com.richi_mc.myapplication.data.api.dto.ScanResponse
import com.richi_mc.myapplication.data.local.UserPreferences
import com.richi_mc.myapplication.data.model.TicketEntity
import com.richi_mc.myapplication.domain.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val apiService: FinancialScanApiService,
    private val ticketRepository: TicketRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _apiResponse = MutableStateFlow<ScanResponse?>(null)
    val apiResponse: StateFlow<ScanResponse?> = _apiResponse

    private val _scannedTickets = MutableStateFlow<List<TicketEntity>>(emptyList())
    val scannedTickets: StateFlow<List<TicketEntity>> = _scannedTickets

    private val _isSendingToApi = MutableStateFlow(false)
    val isSendingToApi: StateFlow<Boolean> = _isSendingToApi

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun sendTextToBackend(extractedText: String) {
        if (extractedText.isBlank()) return

        _isSendingToApi.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val realUserId = userPreferences.userIdFlow.first()

                if (realUserId.isNullOrBlank()) {
                    _errorMessage.value = "Error: Usuario no identificado."
                    _isSendingToApi.value = false
                    return@launch
                }

                val request = ScanRequest(
                    userId = realUserId,
                    tipoDePeticion = 0, // 0 = OCR
                    texto = extractedText
                )

                val response = apiService.scanTicket(request)

                if (response.isSuccessful && response.body()?.ok == true) {
                    val scanResponse = response.body()!!
                    val ticketData = scanResponse.ticket

                    // --- NUEVO: ACTUALIZAMOS EL SCORE IA EN DATASTORE ---
                    val nuevoScore = scanResponse.score.valor.toString()
                    userPreferences.saveUserScoreIa(nuevoScore)
                    // ----------------------------------------------------

                    val entities = mutableListOf<TicketEntity>()

                    withContext(Dispatchers.IO) {
                        // Iteramos sobre la lista de productos de la IA
                        val productos = ticketData.productos ?: emptyList()

                        productos.forEach { producto ->
                            val newTicket = TicketEntity(
                                id = 0, // Siempre 0 para autogenerar
                                date = Date(),
                                trade = ticketData.comercio,
                                productName = producto.nombre,
                                price = producto.precio.toFloat(),
                                category = producto.categoria
                            )
                            entities.add(newTicket)
                            ticketRepository.insertTicket(newTicket)
                        }
                    }

                    _scannedTickets.value = entities
                    _apiResponse.value = scanResponse

                } else {
                    _errorMessage.value = "Error del servidor: Code ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isSendingToApi.value = false
            }
        }
    }

    // Función útil por si escanean otro ticket y quieres limpiar la pantalla
    fun clearResponse() {
        _apiResponse.value = null
        _scannedTickets.value = emptyList()
        _errorMessage.value = null
    }
}