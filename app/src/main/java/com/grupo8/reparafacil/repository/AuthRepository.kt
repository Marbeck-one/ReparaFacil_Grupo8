package com.grupo8.reparafacil.repository

import android.content.Context
import android.net.Uri
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Usuario
import com.grupo8.reparafacil.network.ApiService
import com.grupo8.reparafacil.network.LoginRequest
import com.grupo8.reparafacil.network.RegistroRequest
import com.grupo8.reparafacil.network.UpdatePhotoRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AuthRepository(private val context: Context) {

    private val apiService = ApiService.create()

    // Login (Actualizado para traer perfil completo al inicio)
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val body = LoginRequest(email, password)
            val response = apiService.login(body)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.authToken

                // Obtenemos el perfil completo inmediatamente para tener la foto y datos
                val usuarioResult = obtenerPerfilConToken(token)

                if (usuarioResult.isSuccess) {
                    val usuario = usuarioResult.getOrThrow()
                    DataStoreManager.guardarSesion(context, token, usuario)
                    Result.success(authResponse.copy(user = usuario))
                } else {
                    Result.failure(IOException("Login exitoso pero falló la carga del perfil."))
                }
            } else {
                Result.failure(IOException("Error en login: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registro(nombre: String, email: String, password: String, telefono: String, rol: String, direccion: String? = null, especialidad: String? = null, certificaciones: List<String>? = null): Result<AuthResponse> {
        return try {
            val body = RegistroRequest(email, password, nombre, telefono, rol, direccion, especialidad, certificaciones)
            val response = apiService.registro(body)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.authToken

                val usuarioResult = obtenerPerfilConToken(token)
                if (usuarioResult.isSuccess) {
                    val usuario = usuarioResult.getOrThrow()
                    DataStoreManager.guardarSesion(context, token, usuario)
                    Result.success(authResponse.copy(user = usuario))
                } else {
                    Result.failure(IOException("Registro exitoso pero falló la carga del perfil."))
                }
            } else {
                Result.failure(IOException("Error en registro: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // === FUNCIÓN CLAVE PARA FOTO DE PERFIL ===
    suspend fun actualizarFotoPerfil(uri: Uri): Result<Usuario> {
        return try {
            val token = DataStoreManager.obtenerToken(context).first()
                ?: return Result.failure(Exception("No estás autenticado"))

            // 1. Preparar archivo desde URI
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("No se pudo abrir la imagen"))

            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, bytes.size)
            val body = MultipartBody.Part.createFormData("file", "perfil.jpg", requestFile)

            // 2. Subir imagen al servidor (Storage)
            val uploadResponse = apiService.subirImagen("Bearer $token", body)

            if (!uploadResponse.isSuccessful || uploadResponse.body() == null) {
                return Result.failure(Exception("Error al subir imagen: ${uploadResponse.code()}"))
            }

            // 3. Obtener URL completa
            val data = uploadResponse.body()!!
            val partialUrl = data.imagenThumbnail ?: data.imagen
            val baseUrl = "https://reparafacil-api.onrender.com/"
            val fullUrl = if (partialUrl.startsWith("http")) partialUrl else "$baseUrl$partialUrl"

            // 4. Actualizar el perfil del usuario con la nueva URL (Database)
            val updateResponse = apiService.actualizarFotoUsuario("Bearer $token", UpdatePhotoRequest(fullUrl))

            if (updateResponse.isSuccessful && updateResponse.body() != null) {
                val usuarioActualizado = updateResponse.body()!!

                // 5. Guardar cambios en local (DataStore)
                DataStoreManager.guardarSesion(context, token, usuarioActualizado)

                Result.success(usuarioActualizado)
            } else {
                Result.failure(Exception("Error al guardar URL en perfil: ${updateResponse.code()}"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private suspend fun obtenerPerfilConToken(token: String): Result<Usuario> {
        return try {
            val response = apiService.obtenerPerfil("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerUsuarioGuardado() = DataStoreManager.obtenerUsuario(context)
    fun obtenerTokenGuardado() = DataStoreManager.obtenerToken(context)
    suspend fun cerrarSesion() = DataStoreManager.limpiarSesion(context)
}