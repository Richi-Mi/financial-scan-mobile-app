package com.richi_mc.myapplication.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.richi_mc.myapplication.data.model.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Insert
    fun insertTicket(ticketEntity: TicketEntity) : Long

    @Update
    fun updateTicket(ticketEntity: TicketEntity)

    @Query("SELECT * FROM tickets ORDER BY date DESC;")
    fun getAllTickets() : Flow<List<TicketEntity>>

    @Delete
    fun deleteTicket(ticketEntity: TicketEntity)

    @Query("DELETE FROM tickets")
    fun deleteAllTickets()

}