package com.grupo8.reparafacil.repository

import android.content.Context
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Usuario
import com.grupo8.reparafacil.network.ApiService
import com.grupo8.reparafacil.network.LoginRequest
import com.grupo8.reparafacil.network.RegistroRequest
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class AuthRepository(private val context: Context) {

    private val apiService = ApiService.create()

    // ========== LOGIN ==========
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val body = LoginRequest(email = email, password = password)
            val response = apiService.login(body)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.authToken

                // Obtener perfil completo del usuario
                val usuarioResult = obtenerPerfilConToken(token)

                if (usuarioResult.isSuccess) {
                    val usuario = usuarioResult.getOrThrow()
                    // Guardar sesión completa
                    DataStoreManager.guardarSesion(context, token, usuario)
                    Result.success(authResponse.copy(user = usuario))
                } else {
                    Result.failure(IOException("Login exitoso, pero falló al obtener el perfil."))
                }
            } else {
                Result.failure(IOException("Error en el login: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== REGISTRO ==========
    suspend fun registro(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        rol: String,
        direccion: String? = null,
        especialidad: String? = null,
        certificaciones: List<String>? = null
    ): Result<AuthResponse> {
        return try {
            val body = RegistroRequest(
                email = email,
                password = password,
                nombre = nombre,
                telefono = telefono,
                rol = rol,
                direccion = direccion,
                especialidad = especialidad,
                certificaciones = certificaciones
            )

            // 1. Llama a /signup
            val response = apiService.registro(body)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(IOException("Error en el registro: ${response.message()}"))
            }

            val authResponse = response.body()!!
            val token = authResponse.authToken

            // 2. Llama a /me para obtener el perfil completo
            val usuarioResult = obtenerPerfilConToken(token)

            if (usuarioResult.isSuccess) {
                val usuario = usuarioResult.getOrThrow()
                // 3. Guarda la sesión completa
                DataStoreManager.guardarSesion(context, token, usuario)
                Result.success(authResponse.copy(user = usuario))
            } else {
                Result.failure(IOException("Registro exitoso, pero falló al obtener el perfil."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== OBTENER PERFIL CON TOKEN ==========
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

    // ========== OBTENER USUARIO GUARDADO ==========
    fun obtenerUsuarioGuardado(): Flow<Usuario?> {
        return DataStoreManager.obtenerUsuario(context)
    }

    // ========== OBTENER TOKEN GUARDADO ==========
    fun obtenerTokenGuardado(): Flow<String?> {
        return DataStoreManager.obtenerToken(context)
    }

    // ========== CERRAR SESIÓN ==========
    suspend fun cerrarSesion() {
        DataStoreManager.limpiarSesion(context)
    }
}