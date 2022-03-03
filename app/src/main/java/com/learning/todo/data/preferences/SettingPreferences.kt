package com.learning.todo.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException

data class FilterOption(val sortType: Boolean, val hideComplete: Boolean)

class SettingPreferences : KoinComponent {

    private val context: Context by inject()

    private val dataStore = context.createDataStore("")

    val settingFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.i("MyAppTag", "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortType = preferences[PreferencesKeys.SORT_ORDER] ?: false
            val hideComplete = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterOption(sortType = sortType, hideComplete = hideComplete)
        }

    suspend fun updateSortType(sortType: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortType
        }
    }

    suspend fun updateHideComplete(hideComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideComplete
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<Boolean>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}