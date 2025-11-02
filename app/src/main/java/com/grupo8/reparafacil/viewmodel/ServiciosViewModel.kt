package com.grupo8.reparafacil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo8.reparafacil.model.*
import com.grupo8.reparafacil.repository.ServiciosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiciosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServiciosRepository(application.applicationContext)

    // Lista de servicios
    private val _serviciosState = MutableStateFlow<UiState<List<Servicio>>>(UiState.Idle)
    val serviciosState: StateFlow<UiState<List<Servicio>>> = _serviciosState.asStateFlow()

    // Estado del formulario de solicitud
    private val _solicitudState = MutableStateFlow(SolicitudServicioUiState())
    val solicitudState: StateFlow<SolicitudServicioUiState> = _solicitudState.asStateFlow()

    // Errores de validación
    private val _solicitudErrores = MutableStateFlow(SolicitudServicioErrores())
    val solicitudErrores: StateFlow<SolicitudServicioErrores> = _solicitudErrores.asStateFlow()

    // ========== OBTENER SERVICIOS ==========

    fun cargarServicios() {
        viewModelScope.launch {
            _serviciosState.value = UiState.Loading

            val result = repository.obtenerServicios()

            result.fold(
                onSuccess = { servicios ->
                    _serviciosState.value = UiState.Success(servicios)
                },
                onFailure = { error ->
                    _serviciosState.value = UiState.Error(error.message ?: "Error al cargar servicios")
                }
            )
        }
    }

    // ========== FORMULARIO SOLICITUD ==========

    fun actualizarTipo(tipo: String) {
        _solicitudState.value = _solicitudState.value.copy(tipo = tipo)
        _solicitudErrores.value = _solicitudErrores.value.copy(tipoError = null)
    }

    fun actualizarDescripcion(descripcion: String) {
        _solicitudState.value = _solicitudState.value.copy(descripcion = descripcion)
        _solicitudErrores.value = _solicitudErrores.value.copy(descripcionError = null)
    }

    fun actualizarDireccion(direccion: String) {
        _solicitudState.value = _solicitudState.value.copy(direccion = direccion)
        _solicitudErrores.value = _solicitudErrores.value.copy(direccionError = null)
    }

    fun validarYCrearServicio() {
        val state = _solicitudState.value
        var esValido = true

        // Validar tipo
        if (state.tipo.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                tipoError = "Selecciona el tipo de servicio"
            )
            esValido = false
        }

        // Validar descripción
        if (state.descripcion.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                descripcionError = "Describe el problema"
            )
            esValido = false
        } else if (state.descripcion.length < 10) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                descripcionError = "Mínimo 10 caracteres"
            )
            esValido = false
        }

        // Validar dirección
        if (state.direccion.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                direccionError = "Ingresa tu dirección"
            )
            esValido = false
        }

        if (esValido) {
            crearServicio()
        }
    }

    private fun crearServicio() {
        viewModelScope.launch {
            _solicitudState.value = _solicitudState.value.copy(isLoading = true)

            val result = repository.crearServicio(
                tipo = _solicitudState.value.tipo,
                descripcion = _solicitudState.value.descripcion,
                direccion = _solicitudState.value.direccion
            )

            result.fold(
                onSuccess = {
                    _solicitudState.value = SolicitudServicioUiState() // Reset form
                    cargarServicios() // Recargar lista
                },
                onFailure = { error ->
                    _solicitudState.value = _solicitudState.value.copy(isLoading = false)
                    // Aquí podrías mostrar un error en la UI
                }
            )
        }
    }
}