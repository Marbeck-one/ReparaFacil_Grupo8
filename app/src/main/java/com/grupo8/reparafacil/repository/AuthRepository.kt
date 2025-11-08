package com.grupo8.reparafacil.repository

import android.content.Context
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.*
import com.grupo8.reparafacil.network.ApiService
import com.grupo8.reparafacil.network.LoginRequest
import com.grupo8.reparafacil.network.RegistroRequest
import com.grupo8.reparafacil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import java.io.IOException

/**
 * Repositorio para manejar la autenticación, perfil de usuario y sesión.
 * Combina el origen de datos de la API (Retrofit) y local (DataStore).
 */
class AuthRepository(
    internal val context: Context
) {
    private val apiService: ApiService = RetrofitClient.apiService

    // --- MÉTODOS DE API (NETWORK) ---

    /**
     * Registra un usuario.
     * 1. Llama a /signup para obtener token.
     * 2. Llama a /me para obtener el objeto Usuario.
     * 3. Guarda la sesión completa en DataStore.
     */
    suspend fun registro(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        rol: String
    ): Result<AuthResponse> {
        return try {
            val body = RegistroRequest(email, password, nombre)
            // Nota: La API no parece usar 'telefono' o 'rol' en el body de registro.

            // 1. Llama a /signup
            val response = apiService.registro(body)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(IOException("Error en el registro: ${response.message()}"))
            }

            val authResponse = response.body()!!
            val token = authResponse.authToken

            // 2. Llama a /me
            // (La API de /signup debería devolver el usuario, pero como no lo hace,
            // llamamos a /me para obtener los datos)
            val usuarioResult = obtenerPerfilConToken(token)

            if (usuarioResult.isSuccess) {
                val usuario = usuarioResult.getOrThrow()
                // 3. Guarda la sesión completa
                DataStoreManager.guardarSesion(context, token, usuario)
                // Devuelve la respuesta original, pero con el 'user' rellenado
                Result.success(authResponse.copy(user = usuario))
            } else {
                Result.failure(IOException("Registro exitoso, pero falló al obtener el perfil."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión de un usuario.
     * 1. Llama a /login para obtener token.
     * 2. Llama a /me para obtener el objeto Usuario.
     * 3. Guarda la sesión completa en DataStore.
     */
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val body = LoginRequest(email, password)

            // 1. Llama a /login
            val response = apiService.login(body)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(IOException("Credenciales incorrectas"))
            }

            val authResponse = response.body()!!
            val token = authResponse.authToken

            // 2. Llama a /me
            val usuarioResult = obtenerPerfilConToken(token)

            if (usuarioResult.isSuccess) {
                val usuario = usuarioResult.getOrThrow()
                // 3. Guarda la sesión completa
                DataStoreManager.guardarSesion(context, token, usuario)
                // Devuelve la respuesta original, pero con el 'user' rellenado
                Result.success(authResponse.copy(user = usuario))
            } else {
                Result.failure(IOException("Login exitoso, pero falló al obtener el perfil."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene el perfil del usuario (auth/me) desde la API usando el token guardado.
     */
    suspend fun obtenerPerfil(): Result<Usuario> {
        val token = DataStoreManager.obtenerToken(context)
        if (token == null) {
            return Result.failure(IOException("No hay token de sesión"))
        }
        return obtenerPerfilConToken(token)
    }

    /**
     * Función interna para obtener el perfil usando un token específico.
     * Es reutilizable para login y registro.
     */
    private suspend fun obtenerPerfilConToken(token: String): Result<Usuario> {
        return try {
            val response = apiService.obtenerPerfil("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Error al obtener perfil: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // --- MÉTODOS DE DATASTORE (LOCAL) ---

    suspend fun cerrarSesion() {
        DataStoreManager.cerrarSesion(context)
    }

    fun obtenerUsuarioGuardado(): Flow<Usuario?> {
        return DataStoreManager.obtenerUsuarioGuardado(context)
    }

    suspend fun guardarAvatarUri(userId: Int, uri: String) {
        DataStoreManager.guardarAvatarUri(context, userId, uri)
    }

    fun obtenerAvatarUri(userId: Int): Flow<String?> {
        return DataStoreManager.obtenerAvatarUri(context, userId)
    }
}