package com.grupo8.reparafacil.viewmodel

import android.app.Application
import com.grupo8.reparafacil.model.AuthResponse
import com.grupo8.reparafacil.model.UiState
import com.grupo8.reparafacil.model.Usuario
import com.grupo8.reparafacil.repository.AuthRepository
import com.grupo8.reparafacil.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)

    // MOCK DEL REPOSITORIO
    private val repository = mockk<AuthRepository>(relaxed = true)

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        // Configuramos comportamiento por defecto del repo para que init no falle
        every { repository.obtenerUsuarioGuardado() } returns flowOf(null)

        // Inyectamos el mock en el constructor
        viewModel = AuthViewModel(application, repository)
    }

    // --- TESTS EXISTENTES DE VALIDACIÓN (Se mantienen) ---
    @Test
    fun `actualizarNombre actualiza el estado correctamente`() {
        viewModel.actualizarNombre("Juan")
        assertEquals("Juan", viewModel.registroState.value.nombre)
    }

    @Test
    fun `validarRegistro genera error si nombre esta vacio`() {
        viewModel.actualizarNombre("")
        viewModel.validarYRegistrar()
        assertEquals("El nombre es requerido", viewModel.registroErrores.value.nombreError)
    }

    // --- NUEVOS TESTS DE ÉXITO (Para subir cobertura) ---

    @Test
    fun `registro exitoso llama al repositorio y actualiza estado`() {
        // GIVEN: Un formulario válido
        viewModel.actualizarNombre("Juan")
        viewModel.actualizarEmail("juan@test.com")
        viewModel.actualizarPassword("123456")
        viewModel.actualizarTelefono("123456789")
        viewModel.actualizarRol("cliente")
        viewModel.actualizarDireccion("Calle 1")

        // Simulamos respuesta exitosa del repositorio
        val usuarioMock = Usuario("1", "Juan", "juan@test.com", "cliente")
        val authResponse = AuthResponse("token123", usuarioMock)
        coEvery { repository.registro(any(), any(), any(), any(), any(), any(), any(), any()) } returns Result.success(authResponse)

        // WHEN
        viewModel.validarYRegistrar()

        // THEN
        assertTrue(viewModel.loginState.value is UiState.Success)
        assertEquals(usuarioMock, viewModel.usuarioActual.value)
        coVerify { repository.registro(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `login exitoso actualiza estado`() {
        // GIVEN
        val usuarioMock = Usuario("1", "Juan", "juan@test.com", "cliente")
        val authResponse = AuthResponse("token123", usuarioMock)
        coEvery { repository.login("test@test.com", "123456") } returns Result.success(authResponse)

        // WHEN
        viewModel.login("test@test.com", "123456")

        // THEN
        assertTrue(viewModel.loginState.value is UiState.Success)
        assertEquals(usuarioMock, viewModel.usuarioActual.value)
    }

    @Test
    fun `cerrarSesion limpia el usuario`() {
        viewModel.cerrarSesion()
        assertNull(viewModel.usuarioActual.value)
        coVerify { repository.cerrarSesion() }
    }
}