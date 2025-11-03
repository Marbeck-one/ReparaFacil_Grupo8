package com.grupo8.reparafacil.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW/"

    // Interceptor para agregar headers necesarios
    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            // Si Xano requiere API Key, descomenta la siguiente línea:
            // .header("X-API-Key", "TU_API_KEY_AQUI")
            .method(originalRequest.method, originalRequest.body)

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    // Interceptor para ver las peticiones en el Logcat (debugging)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con timeouts y headers
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)  // Primero headers
        .addInterceptor(loggingInterceptor) // Luego logging
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API Service
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}