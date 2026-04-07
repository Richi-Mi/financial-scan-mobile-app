package com.richi_mc.myapplication.ui.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richi_mc.myapplication.data.model.TicketEntity
import com.richi_mc.myapplication.domain.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    val tickets: StateFlow<List<TicketEntity>> = ticketRepository.getAllTickets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}