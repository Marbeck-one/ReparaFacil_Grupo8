package com.grupo8.reparafacil.model

data class Servicio(
    val id: Int = 0,
    val clienteId: Int = 0,
    val tecnicoId: Int? = null,
    val tipo: String = "", // "Refrigerador", "Lavadora", etc.
    val descripcion: String = "",
    val estado: String = "pendiente", // "pendiente", "asignado", "en_proceso", "completado"
    val fechaSolicitud: String = "",
    val fechaCompletado: String? = null,
    val direccion: String = "",
    val garantia: Boolean = false
)

// Estado UI para solicitar servicio
data class SolicitudServicioUiState(
    val tipo: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false
)

// Errores de validación
data class SolicitudServicioErrores(
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val direccionError: String? = null
)