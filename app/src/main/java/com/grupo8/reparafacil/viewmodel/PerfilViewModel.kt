package com.grupo8.reparafacil.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo8.reparafacil.model.PerfilUiState
import com.grupo8.reparafacil.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)

    // Estado del perfil
    private val _perfilState = MutableStateFlow(PerfilUiState())
    val perfilState: StateFlow<PerfilUiState> = _perfilState.asStateFlow()

    // URI de la imagen seleccionada/capturada
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    init {
        cargarPerfil()
        cargarImagenGuardada()
    }

    // ========== CARGAR PERFIL DESDE API ==========

    fun cargarPerfil() {
        viewModelScope.launch {
            _perfilState.value = _perfilState.value.copy(isLoading = true)

            val result = repository.obtenerPerfil()

            result.fold(
                onSuccess = { usuario ->
                    _perfilState.value = _perfilState.value.copy(
                        usuario = usuario,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _perfilState.value = _perfilState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            )
        }
    }

    // ========== GESTIÓN DE IMAGEN ==========

    fun actualizarImagenDesdeGaleria(uri: Uri) {
        _imagenUri.value = uri
        guardarImagenLocal(uri.toString())
    }

    fun actualizarImagenDesdeCamara(uri: Uri) {
        _imagenUri.value = uri
        guardarImagenLocal(uri.toString())
    }

    private fun guardarImagenLocal(uriString: String) {
        viewModelScope.launch {
            repository.guardarAvatarUri(uriString)
            _perfilState.value = _perfilState.value.copy(imagenUri = uriString)
        }
    }

    private fun cargarImagenGuardada() {
        viewModelScope.launch {
            repository.obtenerAvatarUri().collect { uriString ->
                if (uriString != null) {
                    _imagenUri.value = Uri.parse(uriString)
                    _perfilState.value = _perfilState.value.copy(imagenUri = uriString)
                }
            }
        }
    }
}