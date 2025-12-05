package com.grupo8.reparafacil.repository

import android.content.Context
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.ServicioRequest
import com.grupo8.reparafacil.network.ApiService
// AGREGAMOS ESTOS IMPORTS PARA QUE RECONOZCA LA RESPUESTA
import com.grupo8.reparafacil.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ServiciosRepository(private val context: Context) {

    private val apiService = ApiService.create()

    // ========== CREAR SERVICIO ==========
    suspend fun crearServicio(
        tipo: String,
        descripcion: String,
        direccion: String
    ): Result<Servicio> {
        return try {
            val token = DataStoreManager.obtenerToken(context).first()
                ?: return Result.failure(Exception("No estás autenticado"))

            val body = ServicioRequest(
                tipo = tipo,
                descripcion = descripcion,
                direccion = direccion
            )

            // Si ApiService no se ha actualizado en el build, esto marcará error.
            // Asegúrate de hacer "Clean Project"
            val response = apiService.crearServicio("Bearer $token", body)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                // apiResponse es GenericResponse<Servicio>
                if (apiResponse.success) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(IOException("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== ACTUALIZAR ESTADO (NUEVO) ==========
    suspend fun actualizarEstado(id: String, nuevoEstado: String): Result<Servicio> {
        return try {
            val token = DataStoreManager.obtenerToken(context).first()
                ?: return Result.failure(Exception("No estás autenticado"))

            // Preparamos el cuerpo solo con el campo que cambia
            val body = mapOf("estado" to nuevoEstado)

            // Llama a la función definida en ApiService.kt
            val response = apiService.actualizarServicio("Bearer $token", id, body)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Error al actualizar"))
                }
            } else {
                Result.failure(IOException("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== OBTENER SERVICIOS ==========
    fun obtenerServicios(clienteId: String): Flow<List<Servicio>> = flow {
        try {
            val token = DataStoreManager.obtenerToken(context).first()
            if (token != null) {
                val response = apiService.obtenerServicios("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    // response.body() es GenericListResponse<Servicio>
                    emit(response.body()!!.data)
                } else {
                    emit(emptyList())
                }
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}