package com.grupo8.reparafacil.network

data class UploadResponse(
    val message: String,
    val imagen: String, // Ruta de la imagen original (ej: "uploads/imagen.jpg")
    val imagenThumbnail: String? = null // Ruta de la miniatura (puede ser nula si falla la generación)
)