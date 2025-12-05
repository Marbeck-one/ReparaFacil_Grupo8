package com.grupo8.reparafacil.navigation

import com.grupo8.reparafacil.utils.MainDispatcherRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NavigationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModel = NavigationViewModel()

    @Test
    fun `navigateTo actualiza el evento de navegacion`() {
        viewModel.navigateTo("home")
        val event = viewModel.navigationEvent.value
        assertTrue(event is NavigationEvent.NavigateTo)
        assertEquals("home", (event as NavigationEvent.NavigateTo).route)
    }

    @Test
    fun `navigateBack actualiza evento`() {
        viewModel.navigateBack()
        assertEquals(NavigationEvent.NavigateBack, viewModel.navigationEvent.value)
    }

    @Test
    fun `onNavigationHandled limpia el evento`() {
        viewModel.navigateTo("home")
        viewModel.onNavigationHandled()
        assertNull(viewModel.navigationEvent.value)
    }
}