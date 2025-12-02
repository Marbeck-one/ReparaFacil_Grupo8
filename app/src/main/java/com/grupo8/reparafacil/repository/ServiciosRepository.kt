package com.grupo8.reparafacil.repository

import android.content.Context
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.ServicioRequest
import com.grupo8.reparafacil.network.ApiService
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
        // REMOVED: clienteId from parameters as it is not needed
    ): Result<Servicio> {
        return try {
            val token = DataStoreManager.obtenerToken(context).first()
                ?: return Result.failure(Exception("No estás autenticado"))

            // UPDATE: Create object without clienteId
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
                // Parse error body if needed, but usually code is enough
                Result.failure(IOException("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ... (obtenerServicios remains the same) ...
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
}