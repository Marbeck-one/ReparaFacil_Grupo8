package com.grupo8.reparafacil.viewmodel

import android.app.Application
import android.net.Uri
import com.grupo8.reparafacil.data.DataStoreManager
import com.grupo8.reparafacil.model.Usuario
import com.grupo8.reparafacil.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private lateinit var viewModel: PerfilViewModel

    @Before
    fun setup() {
        // Importante: Mockeamos el objeto Singleton DataStoreManager
        mockkObject(DataStoreManager)

        // Configuramos un comportamiento por defecto para evitar crash si se llama en init (aunque aquí no se llama)
        every { DataStoreManager.obtenerUsuario(any()) } returns flowOf(null)

        viewModel = PerfilViewModel(application)
    }

    @After
    fun tearDown() {
        // Limpiamos el mock del objeto estático para no afectar otros tests
        unmockkObject(DataStoreManager)
    }

    @Test
    fun `cargarPerfil obtiene usuario exitosamente y actualiza el estado`() {
        // GIVEN: Simulamos un usuario proveniente del DataStore
        // Nota: Ajusta los parámetros del constructor de Usuario según tu modelo real.
        // Si Usuario es complejo, puedes usar mockk<Usuario>()
        val usuarioMock = mockk<Usuario>(relaxed = true)

        every { DataStoreManager.obtenerUsuario(any()) } returns flowOf(usuarioMock)

        // WHEN: Cargamos el perfil
        viewModel.cargarPerfil()

        // THEN: Verificamos que el estado tenga el usuario y no esté cargando
        val state = viewModel.perfilState.value
        assertEquals(usuarioMock, state.usuario)
        assertFalse(state.isLoading)

        // Verificamos que se llamó al DataStore
        verify { DataStoreManager.obtenerUsuario(any()) }
    }

    @Test
    fun `actualizarImagenDesdeGaleria actualiza el StateFlow de imagen`() {
        // GIVEN
        val uriMock = mockk<Uri>()

        // WHEN
        viewModel.actualizarImagenDesdeGaleria(uriMock)

        // THEN
        assertEquals(uriMock, viewModel.imagenUri.value)
    }

    @Test
    fun `actualizarImagenDesdeCamara actualiza el StateFlow de imagen`() {
        // GIVEN
        val uriMock = mockk<Uri>()

        // WHEN
        viewModel.actualizarImagenDesdeCamara(uriMock)

        // THEN
        assertEquals(uriMock, viewModel.imagenUri.value)
    }
}