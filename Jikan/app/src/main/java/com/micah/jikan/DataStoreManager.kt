package com.micah.jikan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val ALARMS_KEY = stringSetPreferencesKey("alarms")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "alarms_prefs")

object DataStoreManager {

    private lateinit var dataStore: DataStore<Preferences>

    fun init(context: Context) {
        if(!::dataStore.isInitialized) {
            dataStore = context.dataStore
        }
    }

    val alarmsFlow: Flow<Set<String>>
        get() = dataStore.data.map { preferences ->
            preferences[ALARMS_KEY] ?: emptySet()
        }

    suspend fun  saveAlarms(alarms: Set<String>) {
        dataStore.edit { preferences ->
            preferences[ALARMS_KEY] = alarms
        }
    }
}