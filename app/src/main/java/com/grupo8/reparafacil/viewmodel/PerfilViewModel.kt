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
        // --- MODIFICADO ---
        // cargamos el perfil. Este se encargará de llamar a
        // cargarImagenGuardada DESPUÉS de que tengamos el ID de usuario.
        cargarPerfil()
        // --- LÍNEA ELIMINADA ---
        // cargarImagenGuardada() // (Se elimina de aquí)
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
                    // +++ AÑADIDO +++
                    // Ahora que tenemos el usuario (y su ID),
                    // cargamos la imagen guardada para ESE usuario.
                    cargarImagenGuardada(usuario.id)
                },
                onFailure = { error ->
                    _perfilState.value = _perfilState.value.copy(
                        isLoading = false,
                        error = error.message
                    )

                    // +++ AÑADIDO +++
                    // Si falla la API, intentamos cargar el usuario guardado localmente
                    // para al menos obtener su ID y cargar su foto local.
                    viewModelScope.launch {
                        // Usamos .collect() por si el usuario ya estaba en el flow
                        repository.obtenerUsuarioGuardado().collect { localUser ->
                            if (localUser != null) {
                                _perfilState.value = _perfilState.value.copy(usuario = localUser)
                                cargarImagenGuardada(localUser.id)
                            }
                        }
                    }
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

    // --- MODIFICADO ---
    private fun guardarImagenLocal(uriString: String) {
        // +++ AÑADIDO +++
        // Obtenemos el ID del usuario desde el estado
        val userId = _perfilState.value.usuario?.id

        if (userId == null) {
            _perfilState.value = _perfilState.value.copy(error = "Error al guardar: ID de usuario no encontrado.")
            return
        }

        viewModelScope.launch {
            // +++ MODIFICADO +++
            // Pasamos el ID al repositorio
            repository.guardarAvatarUri(userId, uriString)
            _perfilState.value = _perfilState.value.copy(imagenUri = uriString)
        }
    }

    // Ahora acepta el userId
    private fun cargarImagenGuardada(userId: Int) {
        viewModelScope.launch {
            // +++ MODIFICADO +++
            // Pasamos el ID al repositorio
            repository.obtenerAvatarUri(userId).collect { uriString ->
                if (uriString != null) {
                    _imagenUri.value = Uri.parse(uriString)
                    _perfilState.value = _perfilState.value.copy(imagenUri = uriString)
                } else {
                    // +++ AÑADIDO +++
                    // Limpiar la URI en el estado del ViewModel cuando
                    // el DataStore emita null (si el usuario no tiene foto
                    // o si es un usuario nuevo).
                    _imagenUri.value = null
                    _perfilState.value = _perfilState.value.copy(imagenUri = null)
                }
            }
        }
    }
}