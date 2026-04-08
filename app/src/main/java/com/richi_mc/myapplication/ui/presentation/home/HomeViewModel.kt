package com.richi_mc.myapplication.ui.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.richi_mc.myapplication.data.mocks.MockTicketData
import com.richi_mc.myapplication.data.model.TicketEntity
import com.richi_mc.myapplication.domain.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    val tickets: StateFlow<List<TicketEntity>> = ticketRepository.getAllTickets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalExpense: StateFlow<Float> = tickets.map { tickets ->
        tickets.sumOf { it.price.toDouble() }.toFloat()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    val antExpense: StateFlow<Float> = tickets.map { tickets ->
        tickets.filter { it.category.lowercase() == "hormiga" }
            .sumOf { it.price.toDouble() }
            .toFloat()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    val healthPercentage: StateFlow<Int> = tickets.map { tickets ->
        MockTicketData.calculateHealthPercentage(tickets)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 100
    )

    val categoryDistribution: StateFlow<List<Pair<String, Double>>> = tickets.map { tickets ->
        tickets.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.price.toDouble() } }
            .toList()
            .sortedByDescending { it.second }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearMonth(): String {
        val actualDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
        return actualDate.format(formatter).uppercase()

    }
}