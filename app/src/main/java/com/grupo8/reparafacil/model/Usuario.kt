package com.grupo8.reparafacil.model

data class Usuario(
    val id: Int = 0,
    val email: String = "",
    val name: String = "",
    val rol: String = "", // "cliente" o "tecnico"
    val telefono: String = "",
    val avatarUrl: String? = null
)

// Estado de UI para el formulario de registro
data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val rol: String = "cliente", // por defecto cliente
    val isLoading: Boolean = false
)

// Errores de validación
data class RegistroErrores(
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val telefonoError: String? = null
)

// Respuesta del login/registro desde la API
data class AuthResponse(
    val authToken: String,
    val user: Usuario
)