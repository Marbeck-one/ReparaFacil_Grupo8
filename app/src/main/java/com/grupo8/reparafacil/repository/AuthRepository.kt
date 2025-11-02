package com.grupo8.reparafacil.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Usuario
import com.grupo8.reparafacil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension para DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService

    // Keys para DataStore
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROL_KEY = stringPreferencesKey("user_rol")
        private val USER_TELEFONO_KEY = stringPreferencesKey("user_telefono")
        private val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")
    }

    // ========== API CALLS ==========

    suspend fun registro(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        rol: String
    ): Result<AuthResponse> {
        return try {
            val body = mapOf(
                "name" to nombre,
                "email" to email,
                "password" to password,
                "telefono" to telefono,
                "rol" to rol
            )
            val response = apiService.registro(body)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Guardar token y usuario
                guardarSesion(authResponse.authToken, authResponse.user)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Error en registro: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val body = mapOf(
                "email" to email,
                "password" to password
            )
            val response = apiService.login(body)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Guardar token y usuario
                guardarSesion(authResponse.authToken, authResponse.user)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPerfil(): Result<Usuario> {
        return try {
            val token = obtenerToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay sesión activa"))
            }

            val response = apiService.obtenerPerfil("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener perfil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== DATASTORE (Persistencia local) ==========

    private suspend fun guardarSesion(token: String, usuario: Usuario) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = usuario.id.toString()
            preferences[USER_EMAIL_KEY] = usuario.email
            preferences[USER_NAME_KEY] = usuario.name
            preferences[USER_ROL_KEY] = usuario.rol
            preferences[USER_TELEFONO_KEY] = usuario.telefono
        }
    }

    suspend fun guardarAvatarUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[AVATAR_URI_KEY] = uri
        }
    }

    fun obtenerAvatarUri(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[AVATAR_URI_KEY]
        }
    }

    private suspend fun obtenerToken(): String? {
        var token: String? = null
        context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.collect { token = it }
        return token
    }

    fun obtenerUsuarioGuardado(): Flow<Usuario?> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[USER_ID_KEY]?.toIntOrNull()
            val email = preferences[USER_EMAIL_KEY]
            val name = preferences[USER_NAME_KEY]
            val rol = preferences[USER_ROL_KEY]
            val telefono = preferences[USER_TELEFONO_KEY]

            if (id != null && email != null && name != null) {
                Usuario(
                    id = id,
                    email = email,
                    name = name,
                    rol = rol ?: "",
                    telefono = telefono ?: ""
                )
            } else {
                null
            }
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}