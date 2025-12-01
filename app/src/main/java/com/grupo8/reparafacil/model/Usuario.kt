package com.grupo8.reparafacil.model

import com.google.gson.annotations.SerializedName

// ========== MODELO USUARIO ==========
data class Usuario(
    @SerializedName("_id") val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String? = null,
    val rol: String = "",
    val direccion: String? = null,
    val especialidad: String? = null,
    val certificaciones: List<String>? = null,
    val fotoPerfil: String? = null,
    val activo: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// ========== RESPUESTA DE AUTENTICACIÓN ==========
data class AuthResponse(
    @SerializedName("authToken") val authToken: String = "",
    @SerializedName("user") val user: Usuario? = null
)

// ========== ESTADO UI ==========
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ========== ESTADO UI PARA REGISTRO ==========
data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val rol: String = "cliente",
    val direccion: String = "",
    val especialidad: String = "",
    val isLoading: Boolean = false
)

// ========== ERRORES DE VALIDACIÓN ==========
data class RegistroErrores(
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val telefonoError: String? = null,
    val especialidadError: String? = null
)