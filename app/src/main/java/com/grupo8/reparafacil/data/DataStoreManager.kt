package com.grupo8.reparafacil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.grupo8.reparafacil.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property para DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

object DataStoreManager {

    // Keys para DataStore
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USUARIO_KEY = stringPreferencesKey("usuario_json")

    private val gson = Gson()

    // ========== GUARDAR SESIÓN ==========
    suspend fun guardarSesion(context: Context, token: String, usuario: Usuario) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USUARIO_KEY] = gson.toJson(usuario)
        }
    }

    // ========== OBTENER TOKEN ==========
    fun obtenerToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    // ========== OBTENER USUARIO ==========
    fun obtenerUsuario(context: Context): Flow<Usuario?> {
        return context.dataStore.data.map { preferences ->
            val usuarioJson = preferences[USUARIO_KEY]
            if (usuarioJson != null) {
                try {
                    gson.fromJson(usuarioJson, Usuario::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    // ========== LIMPIAR SESIÓN (LOGOUT) ==========
    suspend fun limpiarSesion(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}