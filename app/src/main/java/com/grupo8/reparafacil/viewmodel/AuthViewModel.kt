package com.grupo8.reparafacil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grupo8.reparafacil.model.*
import com.grupo8.reparafacil.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application.applicationContext)

    // Estado del formulario de registro
    private val _registroState = MutableStateFlow(RegistroUiState())
    val registroState: StateFlow<RegistroUiState> = _registroState.asStateFlow()

    // Errores de validación
    private val _registroErrores = MutableStateFlow(RegistroErrores())
    val registroErrores: StateFlow<RegistroErrores> = _registroErrores.asStateFlow()

    // Estado del login
    private val _loginState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthResponse>> = _loginState.asStateFlow()

    // Estado del usuario actual
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    init {
        cargarUsuarioGuardado()
    }

    // ========== REGISTRO ==========

    fun actualizarNombre(nombre: String) {
        _registroState.value = _registroState.value.copy(nombre = nombre)
        _registroErrores.value = _registroErrores.value.copy(nombreError = null)
    }

    fun actualizarEmail(email: String) {
        _registroState.value = _registroState.value.copy(email = email)
        _registroErrores.value = _registroErrores.value.copy(emailError = null)
    }

    fun actualizarPassword(password: String) {
        _registroState.value = _registroState.value.copy(password = password)
        _registroErrores.value = _registroErrores.value.copy(passwordError = null)
    }

    fun actualizarTelefono(telefono: String) {
        _registroState.value = _registroState.value.copy(telefono = telefono)
        _registroErrores.value = _registroErrores.value.copy(telefonoError = null)
    }

    fun actualizarRol(rol: String) {
        _registroState.value = _registroState.value.copy(rol = rol)
    }

    fun validarYRegistrar() {
        val state = _registroState.value
        var esValido = true

        // Validaciones (sin cambios)...
        if (state.nombre.isBlank()) {
            _registroErrores.value = _registroErrores.value.copy(
                nombreError = "El nombre es requerido"
            )
            esValido = false
        }
        if (state.email.isBlank()) {
            _registroErrores.value = _registroErrores.value.copy(
                emailError = "El email es requerido"
            )
            esValido = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _registroErrores.value = _registroErrores.value.copy(
                emailError = "Email inválido"
            )
            esValido = false
        }
        if (state.password.isBlank()) {
            _registroErrores.value = _registroErrores.value.copy(
                passwordError = "La contraseña es requerida"
            )
            esValido = false
        } else if (state.password.length < 6) {
            _registroErrores.value = _registroErrores.value.copy(
                passwordError = "Mínimo 6 caracteres"
            )
            esValido = false
        }
        if (state.telefono.isBlank()) {
            _registroErrores.value = _registroErrores.value.copy(
                telefonoError = "El teléfono es requerido"
            )
            esValido = false
        }

        if (esValido) {
            registrar()
        }
    }

    private fun registrar() {
        viewModelScope.launch {
            _registroState.value = _registroState.value.copy(isLoading = true)
            _loginState.value = UiState.Loading

            val result = repository.registro(
                nombre = _registroState.value.nombre,
                email = _registroState.value.email,
                password = _registroState.value.password,
                telefono = _registroState.value.telefono,
                rol = _registroState.value.rol
            )

            result.fold(
                onSuccess = { authResponse ->
                    // --- LÓGICA SIMPLIFICADA ---
                    // El 'authResponse' ahora SÍ tiene el 'user' gracias al repo
                    _loginState.value = UiState.Success(authResponse)
                    _usuarioActual.value = authResponse.user
                    _registroState.value = _registroState.value.copy(isLoading = false)

                    // --- YA NO SE NECESITA EL LOGIN MANUAL ---
                    /*
                    if (authResponse.user == null) {
                        login(_registroState.value.email, _registroState.value.password)
                    }
                    */
                },
                onFailure = { error ->
                    _loginState.value = UiState.Error(error.message ?: "Error desconocido")
                    _registroState.value = _registroState.value.copy(isLoading = false)
                }
            )
        }
    }

    // ========== LOGIN ==========

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            // El repositorio ahora hace todo el trabajo (login + /me + guardar)
            val result = repository.login(email, password)

            result.fold(
                onSuccess = { authResponse ->
                    _loginState.value = UiState.Success(authResponse)
                    _usuarioActual.value = authResponse.user
                },
                onFailure = { error ->
                    _loginState.value = UiState.Error(error.message ?: "Error en login")
                }
            )
        }
    }

    // ========== CARGAR USUARIO GUARDADO ==========

    private fun cargarUsuarioGuardado() {
        viewModelScope.launch {
            repository.obtenerUsuarioGuardado().collect { usuario ->
                _usuarioActual.value = usuario
            }
        }
    }

    // ========== CERRAR SESIÓN ==========

    fun cerrarSesion() {
        viewModelScope.launch {
            repository.cerrarSesion()
            _usuarioActual.value = null
            _loginState.value = UiState.Idle
        }
    }

    // ========== RESET ESTADOS ==========

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }
}