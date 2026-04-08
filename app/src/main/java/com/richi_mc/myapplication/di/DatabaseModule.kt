package com.richi_mc.myapplication.di

import android.content.Context
import androidx.room.Room
import com.richi_mc.myapplication.data.FinantialScanDatabase
import com.richi_mc.myapplication.data.daos.TicketDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFinantialScanDatabase(
        @ApplicationContext
        context: Context
    ): FinantialScanDatabase {
        return Room.databaseBuilder(
            context,
            FinantialScanDatabase::class.java,
            "finantial_database"
        ).build()
    }

    @Provides
    fun providesTicketDao(finantialScanDatabase: FinantialScanDatabase) : TicketDao {
        return finantialScanDatabase.ticketDao()
    }
}