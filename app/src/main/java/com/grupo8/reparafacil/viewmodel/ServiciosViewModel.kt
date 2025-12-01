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

    private val _serviciosState = MutableStateFlow<UiState<List<Servicio>>>(UiState.Idle)
    val serviciosState: StateFlow<UiState<List<Servicio>>> = _serviciosState.asStateFlow()

    private val _solicitudState = MutableStateFlow(SolicitudServicioState())
    val solicitudState: StateFlow<SolicitudServicioState> = _solicitudState.asStateFlow()

    private val _solicitudErrores = MutableStateFlow(SolicitudServicioErrores())
    val solicitudErrores: StateFlow<SolicitudServicioErrores> = _solicitudErrores.asStateFlow()

    fun cargarServicios() {
        viewModelScope.launch {
            _serviciosState.value = UiState.Loading
            _serviciosState.value = UiState.Success(emptyList())
        }
    }

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

        if (state.tipo.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                tipoError = "Selecciona un tipo de servicio"
            )
            esValido = false
        }

        if (state.descripcion.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                descripcionError = "La descripción es requerida"
            )
            esValido = false
        } else if (state.descripcion.length < 10) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                descripcionError = "Mínimo 10 caracteres"
            )
            esValido = false
        }

        if (state.direccion.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(
                direccionError = "La dirección es requerida"
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
            kotlinx.coroutines.delay(1000)
            _solicitudState.value = SolicitudServicioState()
            _solicitudErrores.value = SolicitudServicioErrores()
            cargarServicios()
        }
    }
}