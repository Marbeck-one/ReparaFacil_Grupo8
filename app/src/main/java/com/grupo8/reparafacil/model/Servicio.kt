package com.grupo8.reparafacil.model

import com.google.gson.annotations.SerializedName

data class Servicio(
    @SerializedName("_id") val id: String = "",
    val clienteId: String = "",
    val tecnicoId: String? = null,
    val tipo: String = "", // NUEVO
    val descripcion: String = "",
    val direccion: String = "", // NUEVO
    val estado: String = "pendiente",
    val fechaSolicitud: String = "",
    val fechaCompletado: String? = null,
    val costo: Double? = null,
    val garantia: Boolean = false, // NUEVO
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ServicioRequest(
    val tipo: String,
    val descripcion: String,
    val direccion: String,
    val clienteId: String
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