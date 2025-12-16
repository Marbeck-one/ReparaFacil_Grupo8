package com.grupo8.reparafacil.model

import com.google.gson.annotations.SerializedName

data class Servicio(
    @SerializedName("_id") val id: String = "",
    // CORRECCIÓN: Mapeamos 'usuario' como Objeto Usuario (para recibir el populate del backend)
    // Esto arregla el error de "Unresolved reference: usuario" en AdminAuditScreen
    val usuario: Usuario? = null,
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

// Modelo para enviar la solicitud (POST)
data class ServicioRequest(
    val tipo: String,
    val descripcion: String,
    val direccion: String
)

// Estado UI para el formulario de solicitud
data class SolicitudServicioState(
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