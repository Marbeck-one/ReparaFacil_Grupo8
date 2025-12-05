package com.grupo8.reparafacil.viewmodel

import android.app.Application
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.UiState
import com.grupo8.reparafacil.repository.ServiciosRepository
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

class ServiciosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<ServiciosRepository>(relaxed = true)

    private lateinit var viewModel: ServiciosViewModel

    @Before
    fun setup() {
        // Configuramos respuesta por defecto para cargarServicios en init
        every { repository.obtenerServicios(any()) } returns flowOf(emptyList())

        viewModel = ServiciosViewModel(application, repository)
    }

    @Test
    fun `cargarServicios actualiza el estado con lista exitosa`() {
        // GIVEN
        val servicio = Servicio("1", "tipo", "desc", "dir", "fecha", "cliente", "estado")
        val lista = listOf(servicio)
        every { repository.obtenerServicios(any()) } returns flowOf(lista)

        // WHEN
        viewModel.cargarServicios()

        // THEN
        val state = viewModel.serviciosState.value
        assertTrue(state is UiState.Success)
        if (state is UiState.Success) {
            assertEquals(1, state.data.size)
        }
    }

    @Test
    fun `crearServicio exitoso limpia formulario y recarga`() {
        // GIVEN
        viewModel.actualizarTipo("Gasfitería")
        viewModel.actualizarDescripcion("Reparación de fuga grande")
        viewModel.actualizarDireccion("Calle Test")

        coEvery { repository.crearServicio(any(), any(), any()) } returns Result.success(mockk())
        // Recarga posterior
        every { repository.obtenerServicios(any()) } returns flowOf(emptyList())

        // WHEN
        viewModel.validarYCrearServicio()

        // THEN
        assertNull(viewModel.solicitudErrores.value.tipoError)
        coVerify { repository.crearServicio(any(), any(), any()) }
    }

    @Test
    fun `validarCrearServicio falla con datos vacios`() {
        viewModel.actualizarTipo("")
        viewModel.validarYCrearServicio()
        assertEquals("Selecciona un tipo de servicio", viewModel.solicitudErrores.value.tipoError)
    }
}