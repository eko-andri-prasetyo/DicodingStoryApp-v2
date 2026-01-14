package com.dicoding.storyapp.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.dicoding.storyapp.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                name = preferences[NAME_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[STATE_KEY] = user.isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[STATE_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(dataStore).also { INSTANCE = it }
            }
        }
    }
}
