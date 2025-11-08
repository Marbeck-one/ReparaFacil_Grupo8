package com.grupo8.reparafacil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grupo8.reparafacil.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension del Context para el DataStore (Singleton)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * Manager centralizado para el DataStore
 * Evita crear múltiples instancias del mismo DataStore
 */
object DataStoreManager {

    // Keys para DataStore (Sesión)
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_ROL_KEY = stringPreferencesKey("user_rol")
    private val USER_TELEFONO_KEY = stringPreferencesKey("user_telefono")

    // --- ELIMINAMOS ESTA LÍNEA ---
    // private val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")

    // +++ AÑADIMOS ESTA FUNCIÓN +++
    // Helper para crear una clave de avatar única por usuario
    private fun getAvatarKey(userId: Int) = stringPreferencesKey("avatar_uri_$userId")


    // ========== GUARDAR DATOS ==========

    suspend fun guardarSesion(context: Context, token: String, usuario: Usuario) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = usuario.id.toString()
            preferences[USER_EMAIL_KEY] = usuario.email
            preferences[USER_NAME_KEY] = usuario.name
            preferences[USER_ROL_KEY] = usuario.rol
            preferences[USER_TELEFONO_KEY] = usuario.telefono
        }
    }

    suspend fun guardarToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // +++ MODIFICAMOS LA FIRMA Y LÓGICA +++
    // Ahora acepta un userId para saber a quién pertenece la URI
    suspend fun guardarAvatarUri(context: Context, userId: Int, uri: String) {
        val key = getAvatarKey(userId)
        context.dataStore.edit { preferences ->
            preferences[key] = uri
        }
    }

    // ========== OBTENER DATOS ==========

    suspend fun obtenerToken(context: Context): String? {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }

    fun obtenerTokenFlow(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    fun obtenerUsuarioGuardado(context: Context): Flow<Usuario?> {
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

    // +++ MODIFICAMOS LA FIRMA Y LÓGICA +++
    // Ahora acepta un userId para obtener la URI correcta
    fun obtenerAvatarUri(context: Context, userId: Int): Flow<String?> {
        val key = getAvatarKey(userId)
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    // ========== LIMPIAR DATOS ==========

    // +++ MODIFICAMOS LA LÓGICA +++
    // Ya no usamos preferences.clear()
    // Borramos solo los datos de la sesión activa
    suspend fun cerrarSesion(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_ROL_KEY)
            preferences.remove(USER_TELEFONO_KEY)

            // Las claves "avatar_uri_X" se quedan guardadas
        }
    }
}