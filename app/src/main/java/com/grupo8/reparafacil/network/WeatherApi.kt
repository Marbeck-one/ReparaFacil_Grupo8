package com.grupo8.reparafacil.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Modelo de datos simple para Open-Meteo
data class WeatherResponse(
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double
)

// Interfaz Retrofit independiente
interface WeatherApiService {
    @GET("v1/forecast?current_weather=true")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double
    ): WeatherResponse

    companion object {
        fun create(): WeatherApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}