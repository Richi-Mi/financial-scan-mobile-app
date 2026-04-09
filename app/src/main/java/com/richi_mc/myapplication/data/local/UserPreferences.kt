package com.richi_mc.myapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_SCORE_IA = stringPreferencesKey("user_score_ia")
        val USER_RESUMEN_IA = stringPreferencesKey("user_resumen_ia")
        val USER_SCORE_FACTORES = stringPreferencesKey("user_score_factores")
        val USER_TIPS = stringPreferencesKey("user_tips")
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val userNameFlow: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }
    val userScoreIaFlow: Flow<String?> = context.dataStore.data.map { it[USER_SCORE_IA] }
    val userResumenIaFlow: Flow<String?> = context.dataStore.data.map { it[USER_RESUMEN_IA] }
    val userScoreFactoresFlow: Flow<String?> = context.dataStore.data.map { it[USER_SCORE_FACTORES] }
    val userTipsFlow: Flow<String?> = context.dataStore.data.map { it[USER_TIPS] }

    suspend fun saveUserData(userId: String, userName: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
        }
    }

    suspend fun saveUserScoreIa(userScoreIa: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_SCORE_IA] = userScoreIa
        }
    }

    suspend fun saveProfileData(resumen: String, factoresJson: String, tipsJson: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_RESUMEN_IA] = resumen
            preferences[USER_SCORE_FACTORES] = factoresJson
            preferences[USER_TIPS] = tipsJson
        }
    }
}