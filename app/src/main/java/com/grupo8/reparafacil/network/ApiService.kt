package com.grupo8.reparafacil.network

import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.ServicioRequest
import com.grupo8.reparafacil.model.Usuario
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ========== RESPUESTAS GENÉRICAS ==========

data class GenericResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T
)

data class GenericListResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val total: Int? = null
)

// NOTA: Borramos 'data class UploadResponse' de aquí porque ya está en UploadResponse.kt

// ========== MODELOS DE REQUEST ==========

data class UpdatePhotoRequest(val fotoPerfil: String)

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

    // --- AUTENTICACIÓN ---
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/signup")
    suspend fun registro(@Body request: RegistroRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun obtenerPerfil(@Header("Authorization") token: String): Response<Usuario>

    // --- SUBIDA DE IMÁGENES ---
    @Multipart
    @POST("api/upload/image")
    suspend fun subirImagen(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    // --- VINCULAR URL AL PERFIL ---
    @PATCH("api/auth/me/photo")
    suspend fun actualizarFotoUsuario(
        @Header("Authorization") token: String,
        @Body body: UpdatePhotoRequest
    ): Response<Usuario>

    // --- SERVICIOS / REPARACIONES ---
    @POST("api/reparacion")
    suspend fun crearServicio(
        @Header("Authorization") token: String,
        @Body request: ServicioRequest
    ): Response<GenericResponse<Servicio>>

    @GET("api/reparacion")
    suspend fun obtenerServicios(
        @Header("Authorization") token: String
    ): Response<GenericListResponse<Servicio>>

    @PATCH("api/reparacion/{id}")
    suspend fun actualizarServicio(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<GenericResponse<Servicio>>

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