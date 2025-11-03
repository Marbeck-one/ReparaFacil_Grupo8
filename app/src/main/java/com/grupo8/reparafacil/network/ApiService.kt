package com.grupo8.reparafacil.network

import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== AUTENTICACIÓN ==========

    @POST("auth/signup")
    @Headers("Content-Type: application/json")
    suspend fun registro(
        @Body body: RegistroRequest
    ): Response<AuthResponse>

    @POST("auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Body body: LoginRequest
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
    @Headers("Content-Type: application/json")
    suspend fun crearServicio(
        @Header("Authorization") token: String,
        @Body body: ServicioRequest
    ): Response<Servicio>

    @GET("servicios/{id}")
    suspend fun obtenerServicio(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Servicio>

    @PATCH("servicios/{id}")
    @Headers("Content-Type: application/json")
    suspend fun actualizarServicio(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: Map<String, Any>
    ): Response<Servicio>
}

// ========== DATA CLASSES PARA REQUESTS ==========

data class RegistroRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ServicioRequest(
    val tipo: String,
    val descripcion: String,
    val direccion: String,
    val estado: String = "pendiente"
)