package com.grupo8.reparafacil.model

// Estados generales para carga/éxito/error
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// Estado del perfil de usuario
data class PerfilUiState(
    val usuario: Usuario? = null,
    val imagenUri: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)