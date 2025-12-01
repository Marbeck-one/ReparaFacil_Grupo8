package com.grupo8.reparafacil.repository

import android.content.Context
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.ServicioRequest
import com.grupo8.reparafacil.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ServiciosRepository(private val context: Context) {

    private val apiService = ApiService.create()

    // ========== CREAR SERVICIO ==========
    suspend fun crearServicio(
        tipo: String,
        descripcion: String,
        direccion: String,
        clienteId: String
    ): Result<Servicio> {
        return try {
            val body = ServicioRequest(
                tipo = tipo,
                descripcion = descripcion,
                direccion = direccion,
                clienteId = clienteId
            )
            // TODO: Implementar cuando tengas el endpoint
            Result.failure(IOException("Endpoint no implementado aún"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== OBTENER SERVICIOS ==========
    fun obtenerServicios(clienteId: String): Flow<List<Servicio>> = flow {
        try {
            // TODO: Implementar cuando tengas el endpoint
            emit(emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}