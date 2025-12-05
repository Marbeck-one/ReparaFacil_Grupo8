package com.grupo8.reparafacil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo8.reparafacil.model.*
import com.grupo8.reparafacil.repository.ServiciosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Usamos @JvmOverloads para permitir la inyección del repositorio mock en los tests
class ServiciosViewModel @JvmOverloads constructor(
    application: Application,
    private val testRepository: ServiciosRepository? = null
) : AndroidViewModel(application) {

    // Si testRepository es null (app real), crea uno nuevo. Si no (test), usa el mock.
    private val repository = testRepository ?: ServiciosRepository(application.applicationContext)

    // ... (El resto de tus estados se mantienen igual) ...
    private val _serviciosState = MutableStateFlow<UiState<List<Servicio>>>(UiState.Idle)
    val serviciosState: StateFlow<UiState<List<Servicio>>> = _serviciosState.asStateFlow()

    private val _busquedaQuery = MutableStateFlow("")
    val busquedaQuery = _busquedaQuery.asStateFlow()

    private val _filtroEstado = MutableStateFlow("Todos")
    val filtroEstado = _filtroEstado.asStateFlow()

    val serviciosFiltrados: StateFlow<List<Servicio>> = combine(
        _serviciosState,
        _busquedaQuery,
        _filtroEstado
    ) { state, query, estadoFilter ->
        if (state is UiState.Success) {
            state.data.filter { servicio ->
                val coincideTexto = servicio.descripcion.contains(query, ignoreCase = true) ||
                        servicio.tipo.contains(query, ignoreCase = true)
                val coincideEstado = if (estadoFilter == "Todos") true else {
                    servicio.estado.equals(estadoFilter, ignoreCase = true)
                }
                coincideTexto && coincideEstado
            }
        } else {
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _solicitudState = MutableStateFlow(SolicitudServicioState())
    val solicitudState: StateFlow<SolicitudServicioState> = _solicitudState.asStateFlow()

    private val _solicitudErrores = MutableStateFlow(SolicitudServicioErrores())
    val solicitudErrores: StateFlow<SolicitudServicioErrores> = _solicitudErrores.asStateFlow()

    init {
        cargarServicios()
    }

    // Funciones
    fun onBusquedaChange(text: String) { _busquedaQuery.value = text }
    fun onFiltroEstadoChange(estado: String) { _filtroEstado.value = estado }

    fun cargarServicios() {
        viewModelScope.launch {
            _serviciosState.value = UiState.Loading
            repository.obtenerServicios("").collect { lista ->
                _serviciosState.value = UiState.Success(lista)
            }
        }
    }

    // ESTA ES LA FUNCIÓN QUE DABA PROBLEMAS
    // Asegúrate de que repository.actualizarEstado exista en ServiciosRepository.kt
    fun cambiarEstadoServicio(servicioId: String, nuevoEstado: String) {
        viewModelScope.launch {
            val result = repository.actualizarEstado(servicioId, nuevoEstado)

            result.onSuccess {
                cargarServicios()
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    // ... (Funciones de validación y creación siguen igual) ...
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
            _solicitudErrores.value = _solicitudErrores.value.copy(tipoError = "Selecciona un tipo de servicio")
            esValido = false
        }
        if (state.descripcion.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(descripcionError = "La descripción es requerida")
            esValido = false
        } else if (state.descripcion.length < 10) {
            _solicitudErrores.value = _solicitudErrores.value.copy(descripcionError = "Mínimo 10 caracteres")
            esValido = false
        }
        if (state.direccion.isBlank()) {
            _solicitudErrores.value = _solicitudErrores.value.copy(direccionError = "La dirección es requerida")
            esValido = false
        }

        if (esValido) crearServicio()
    }

    private fun crearServicio() {
        viewModelScope.launch {
            _solicitudState.value = _solicitudState.value.copy(isLoading = true)
            val currentState = _solicitudState.value
            val result = repository.crearServicio(currentState.tipo, currentState.descripcion, currentState.direccion)
            _solicitudState.value = _solicitudState.value.copy(isLoading = false)

            result.onSuccess {
                _solicitudState.value = SolicitudServicioState()
                _solicitudErrores.value = SolicitudServicioErrores()
                cargarServicios()
            }.onFailure { e ->
                _solicitudErrores.value = _solicitudErrores.value.copy(descripcionError = "Error: ${e.message}")
            }
        }
    }
}