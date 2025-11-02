package com.grupo8.reparafacil.network

import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== AUTENTICACIÓN ==========

    @POST("auth/signup")
    suspend fun registro(
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @GET("auth/me")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<Usuario>

    // ========== SERVICIOS ==========

    @GET("servicios")
    suspend fun obtenerServicios(
        @Header("Authorization") token: String
    ): Response<List<Servicio>>

    @POST("servicios")
    suspend fun crearServicio(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Response<Servicio>

    @GET("servicios/{id}")
    suspend fun obtenerServicio(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Servicio>

    @PATCH("servicios/{id}")
    suspend fun actualizarServicio(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: Map<String, Any>
    ): Response<Servicio>
}