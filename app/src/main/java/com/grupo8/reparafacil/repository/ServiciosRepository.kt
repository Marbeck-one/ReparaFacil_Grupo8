package com.grupo8.reparafacil.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.network.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class ServiciosRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService

    private companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    private suspend fun obtenerToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }

    suspend fun obtenerServicios(): Result<List<Servicio>> {
        return try {
            val token = obtenerToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No hay sesión activa"))
            }

            val response = apiService.obtenerServicios("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener servicios"))
            }
        } catch (e: Exception) {
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
                return Result.failure(Exception("No hay sesión activa"))
            }

            val body = mapOf(
                "tipo" to tipo,
                "descripcion" to descripcion,
                "direccion" to direccion,
                "estado" to "pendiente"
            )

            val response = apiService.crearServicio("Bearer $token", body)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear servicio"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}