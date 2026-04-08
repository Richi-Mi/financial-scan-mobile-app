package com.richi_mc.myapplication.data.localimport

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.commons.lang3.SystemUtils.USER_NAME_KEY
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para inicializar DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name") // Nuevo
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val userNameFlow: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }

    suspend fun saveUserData(userId: String, userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
        }
    }
}