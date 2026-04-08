package com.richi_mc.myapplication.ui.presentation.record

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.richi_mc.myapplication.data.api.FinancialScanApiService
import com.richi_mc.myapplication.domain.TicketRepository
import com.richi_mc.myapplication.helpers.SpeechRecognizerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import androidx.lifecycle.viewModelScope
import com.richi_mc.myapplication.data.api.dto.ScanRequest
import com.richi_mc.myapplication.data.localimport.UserPreferences
import com.richi_mc.myapplication.data.model.TicketEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val speechRecognizerHelper: SpeechRecognizerHelper,
    private val financialScanApiService: FinancialScanApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var isListening by mutableStateOf(false)
        private set

    var partialText by mutableStateOf("")
        private set

    var finalText by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var lastCreatedTicket by mutableStateOf<TicketEntity?>(null)
        private set

    fun startListening() {
        // Limpiar estado anterior para un nuevo registro
        finalText = ""
        partialText = ""
        lastCreatedTicket = null
        errorMessage = null

        speechRecognizerHelper.start(
            onPartialResult = { partial ->
                partialText = partial
                errorMessage = null
            },
            onFinalResult = { text ->
                if (text.isNotBlank()) {
                    finalText = if (finalText.isEmpty()) text else "$finalText $text"
                    processSpeechWithApi(text)
                }
                partialText = ""
            },
            onError = { error ->
                errorMessage = error
                partialText = ""
            },
            onStateChange = { listening ->
                isListening = listening
            }
        )
    }

    private fun processSpeechWithApi(text: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val realUserId = userPreferences.userIdFlow.first() ?: return@launch

                // Usamos scanTicket con tipoDePeticion = 1 (Voz) ya que es el flujo para dictado
                val response = financialScanApiService.scanTicket(
                    ScanRequest(
                        userId = realUserId,
                        tipoDePeticion = 1,
                        texto = text
                    )
                )

                if (response.isSuccessful && response.body()?.ok == true) {
                    val ticketData = response.body()!!.ticket
                    val newTicket = TicketEntity(
                        date = Date(),
                        trade = ticketData.comercio,
                        productName = ticketData.productos?.firstOrNull()?.nombre ?: "Gasto por voz",
                        price = ticketData.total.toFloat(),
                        category = ticketData.productos?.firstOrNull()?.categoria ?: "Gasto por voz"
                    )
                    withContext(Dispatchers.IO) {
                        val id = ticketRepository.insertTicket(newTicket)
                        newTicket.id = id
                        lastCreatedTicket = newTicket
                    }
                } else {
                    Log.e("ERROR", "Error procesando texto ${response.toString()}")
                    errorMessage = "Error al procesar: ${response.toString()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e}"
            } finally {
                isLoading = false
            }
        }
    }

    fun stopListening() {
        speechRecognizerHelper.stop()
    }

    fun updateTicket(updatedTicket: TicketEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            ticketRepository.updateTicket(updatedTicket)
            Log.d("TicketViewModel", "Ticket actualizado: $updatedTicket")
            withContext(Dispatchers.Main) {
                lastCreatedTicket = updatedTicket
            }
        }
    }

    fun deleteTicket() {
        val ticket = lastCreatedTicket ?: return
        viewModelScope.launch(Dispatchers.IO) {
            ticketRepository.deleteTicket(ticket)
            withContext(Dispatchers.Main) {
                lastCreatedTicket = null
            }
        }
    }

    fun clearText() {
        finalText = ""
        partialText = ""
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizerHelper.destroy()
    }
}