package com.grupo8.reparafacil.repository

import android.content.Context
import android.util.Log
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.network.RetrofitClient
import com.grupo8.reparafacil.network.ServicioRequest

class ServiciosRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService
    private val TAG = "ServiciosRepository"

    private suspend fun obtenerToken(): String? {
        return DataStoreManager.obtenerToken(context)
    }

    suspend fun obtenerServicios(): Result<List<Servicio>> {
        return try {
            val token = obtenerToken()
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "❌ No hay token guardado")
                return Result.failure(Exception("No hay sesión activa"))
            }

            Log.d(TAG, "🔵 Obteniendo lista de servicios...")

            val response = apiService.obtenerServicios("Bearer $token")

            Log.d(TAG, "🔵 Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ ${response.body()!!.size} servicios obtenidos")
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al obtener servicios"
                Log.e(TAG, "❌ Error: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun crearServicio(
        tipo: String,
        descripcion: String,
        direccion: String
    ): Result<Servicio> {
        return try {
            val token = obtenerToken()
            if (token.isNullOrEmpty()) {
                Log.e(TAG, "❌ No hay token guardado")
                return Result.failure(Exception("No hay sesión activa"))
            }

            Log.d(TAG, "🔵 Creando servicio: $tipo")

            val body = ServicioRequest(
                tipo = tipo,
                descripcion = descripcion,
                direccion = direccion,
                estado = "pendiente"
            )

            val response = apiService.crearServicio("Bearer $token", body)

            Log.d(TAG, "🔵 Response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Servicio creado exitosamente")
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear servicio"
                Log.e(TAG, "❌ Error: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}