package com.richi_mc.myapplication.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0,

    @ColumnInfo(name = "date")
    val date : Date,

    @ColumnInfo(name = "trade")
    val trade : String, // O comercio

    @ColumnInfo(name = "product_name")
    val productName : String,

    @ColumnInfo(name = "price")
    val price : Float,

    @ColumnInfo(name = "category")
    val category : String

)