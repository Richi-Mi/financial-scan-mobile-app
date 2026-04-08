package com.richi_mc.myapplication.domain

import com.richi_mc.myapplication.data.daos.TicketDao
import com.richi_mc.myapplication.data.model.TicketEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TicketRepository @Inject constructor(
    private val ticketDao: TicketDao
) {
    suspend fun insertTicket(ticket: TicketEntity) : Long {
        return ticketDao.insertTicket(ticket)
    }

    suspend fun deleteTicket(ticketEntity: TicketEntity) {
        ticketDao.deleteTicket(ticketEntity)
    }
    suspend fun updateTicket(ticketEntity: TicketEntity) {
        ticketDao.updateTicket(ticketEntity)
    }
    fun getAllTickets(): Flow<List<TicketEntity>> {
        return ticketDao.getAllTickets()
    }

    fun dropTable() {
        ticketDao.deleteAllTickets()
    }

}