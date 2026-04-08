package com.richi_mc.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.richi_mc.myapplication.data.daos.TicketDao
import com.richi_mc.myapplication.data.model.TicketEntity

@Database(entities = [TicketEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FinantialScanDatabase : RoomDatabase() {
    abstract fun ticketDao() : TicketDao
}