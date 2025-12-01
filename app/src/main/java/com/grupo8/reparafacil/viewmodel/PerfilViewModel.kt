package com.grupo8.reparafacil.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val _perfilState = MutableStateFlow(PerfilState())
    val perfilState: StateFlow<PerfilState> = _perfilState.asStateFlow()

    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    fun cargarPerfil() {
        viewModelScope.launch {
            _perfilState.value = _perfilState.value.copy(isLoading = true)

            DataStoreManager.obtenerUsuario(getApplication()).collect { usuario ->
                _perfilState.value = PerfilState(
                    usuario = usuario,
                    isLoading = false
                )
            }
        }
    }

    fun actualizarImagenDesdeGaleria(uri: Uri) {
        _imagenUri.value = uri
        // TODO: Subir imagen al servidor
    }

    fun actualizarImagenDesdeCamara(uri: Uri) {
        _imagenUri.value = uri
        // TODO: Subir imagen al servidor
    }
}