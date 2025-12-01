package com.grupo8.reparafacil.network

import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Usuario
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ========== MODELOS DE REQUEST ==========

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistroRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val telefono: String? = null,
    val rol: String,
    val direccion: String? = null,
    val especialidad: String? = null,
    val certificaciones: List<String>? = null
)

// ========== INTERFACE API ==========

interface ApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/api/auth/signup")
    suspend fun registro(@Body request: RegistroRequest): Response<AuthResponse>

    @GET("/api/auth/me")
    suspend fun obtenerPerfil(@Header("Authorization") token: String): Response<Usuario>

    companion object {
        private const val BASE_URL = "https://reparafacil-api.onrender.com/"

        fun create(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}