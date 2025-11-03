package com.grupo8.reparafacil.repository

import android.content.Context
import android.util.Log
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Usuario
import com.grupo8.reparafacil.network.LoginRequest
import com.grupo8.reparafacil.network.RegistroRequest
import com.grupo8.reparafacil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService
    private val TAG = "AuthRepository"

    // ========== API CALLS ==========

    suspend fun registro(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        rol: String
    ): Result<AuthResponse> {
        return try {
            Log.d(TAG, "🔵 REGISTRO - Iniciando para email: $email, rol: $rol")

            val body = RegistroRequest(
                email = email,
                password = password,
                name = nombre
            )

            Log.d(TAG, "🔵 REGISTRO - Body: $body")
            val response = apiService.registro(body)
            Log.d(TAG, "🔵 REGISTRO - Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                // El backend no devuelve el objeto 'user' completo
                if (authResponse.user != null) {
                    Log.d(TAG, "✅ REGISTRO - Usuario completo recibido: ${authResponse.user.name}")
                    DataStoreManager.guardarSesion(context, authResponse.authToken, authResponse.user)
                } else {
                    Log.w(TAG, "⚠️ REGISTRO - Backend no devolvió 'user', solo guardando token")
                    DataStoreManager.guardarToken(context, authResponse.authToken)
                }

                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e(TAG, "❌ REGISTRO - Error: Code=${response.code()}, Body=$errorBody")
                Result.failure(Exception("Error en registro: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ REGISTRO - Excepción: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "🔵 LOGIN - Iniciando para email: $email")

            val body = LoginRequest(
                email = email,
                password = password
            )

            Log.d(TAG, "🔵 LOGIN - Body: $body")
            val response = apiService.login(body)
            Log.d(TAG, "🔵 LOGIN - Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                // El backend tampoco devuelve 'user' en login
                if (authResponse.user != null) {
                    Log.d(TAG, "✅ LOGIN - Exitoso con usuario: ${authResponse.user.name}")
                    DataStoreManager.guardarSesion(context, authResponse.authToken, authResponse.user)
                    Result.success(authResponse)
                } else {
                    Log.w(TAG, "⚠️ LOGIN - Backend no devolvió 'user', obteniendo con /me")

                    // Guardar el token primero
                    DataStoreManager.guardarToken(context, authResponse.authToken)

                    // Llamar a /me para obtener el usuario completo
                    val perfilResult = obtenerPerfil()
                    perfilResult.fold(
                        onSuccess = { usuario ->
                            Log.d(TAG, "✅ LOGIN - Usuario obtenido desde /me: ${usuario.name}")
                            // Ahora sí guardar la sesión completa
                            DataStoreManager.guardarSesion(context, authResponse.authToken, usuario)
                            // Crear un AuthResponse completo para devolver
                            val authCompleto = AuthResponse(
                                authToken = authResponse.authToken,
                                user = usuario,
                                user_id = authResponse.user_id
                            )
                            Result.success(authCompleto)
                        },
                        onFailure = { error ->
                            Log.e(TAG, "❌ LOGIN - Error al obtener perfil: ${error.message}")
                            Result.failure(error)
                        }
                    )
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Credenciales incorrectas"
                Log.e(TAG, "❌ LOGIN - Error: Code=${response.code()}, Body=$errorBody")
                Result.failure(Exception("Login fallido: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ LOGIN - Excepción: ${e.message}", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerPerfil(): Result<Usuario> {
        return try {
            val token = DataStoreManager.obtenerToken(context)
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "❌ PERFIL - No hay token guardado")
                return Result.failure(Exception("No hay sesión activa"))
            }

            Log.d(TAG, "🔵 PERFIL - Obteniendo perfil con token: ${token.take(20)}...")
            val response = apiService.obtenerPerfil("Bearer $token")
            Log.d(TAG, "🔵 PERFIL - Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ PERFIL - Obtenido exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener perfil"
                Log.e(TAG, "❌ PERFIL - Error: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ PERFIL - Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ========== DATASTORE (usando el Manager) ==========

    suspend fun guardarAvatarUri(uri: String) {
        DataStoreManager.guardarAvatarUri(context, uri)
    }

    fun obtenerAvatarUri(): Flow<String?> {
        return DataStoreManager.obtenerAvatarUri(context)
    }

    fun obtenerUsuarioGuardado(): Flow<Usuario?> {
        return DataStoreManager.obtenerUsuarioGuardado(context)
    }

    suspend fun cerrarSesion() {
        Log.d(TAG, "🔴 Cerrando sesión")
        DataStoreManager.cerrarSesion(context)
    }
}