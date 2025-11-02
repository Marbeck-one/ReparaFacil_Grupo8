package com.grupo8.reparafacil.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : ViewModel() {

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun navigateTo(route: String) {
        _navigationEvent.value = NavigationEvent.NavigateTo(route)
    }

    fun navigateBack() {
        _navigationEvent.value = NavigationEvent.NavigateBack
    }

    fun navigateToWithPopUp(route: String, popUpTo: String) {
        _navigationEvent.value = NavigationEvent.NavigateToWithPopUp(route, popUpTo)
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }
}