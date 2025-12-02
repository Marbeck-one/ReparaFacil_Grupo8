package com.grupo8.reparafacil.model

import com.google.gson.annotations.SerializedName

data class Servicio(
    @SerializedName("_id") val id: String = "",
    val clienteId: String = "", // This is fine for READING responses
    val tecnicoId: String? = null,
    val tipo: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val estado: String = "pendiente",
    val fechaSolicitud: String = "",
    val fechaCompletado: String? = null,
    val costo: Double? = null,
    val garantia: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// UPDATE THIS CLASS:
data class ServicioRequest(
    val tipo: String,
    val descripcion: String,
    val direccion: String
    // REMOVED: val clienteId: String
)

// Estado UI para solicitud de servicio
data class SolicitudServicioState(
    val tipo: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false
)

// Errores de validación para solicitud
data class SolicitudServicioErrores(
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val direccionError: String? = null
)