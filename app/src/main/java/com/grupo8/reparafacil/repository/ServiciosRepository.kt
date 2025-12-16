package com.grupo8.reparafacil.repository

import android.content.Context
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.ServicioRequest
import com.grupo8.reparafacil.network.ApiService
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

            val response = apiService.crearServicio("Bearer $token", body)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
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

    // ========== ACTUALIZAR ESTADO ==========
    suspend fun actualizarEstado(id: String, nuevoEstado: String): Result<Servicio> {
        return try {
            val token = DataStoreManager.obtenerToken(context).first()
                ?: return Result.failure(Exception("No estás autenticado"))

            val body = mapOf("estado" to nuevoEstado)

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

    // ========== ELIMINAR SERVICIO (ADMIN) ==========
    suspend fun eliminarServicio(id: String): Result<Boolean> {
        return try {
            val token = DataStoreManager.obtenerToken(context).first()
                ?: return Result.failure(Exception("No autenticado"))

            val response = apiService.eliminarServicio("Bearer $token", id)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}