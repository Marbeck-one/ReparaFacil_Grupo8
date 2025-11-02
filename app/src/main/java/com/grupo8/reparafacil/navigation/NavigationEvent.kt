package com.grupo8.reparafacil.navigation

// Eventos de navegación que puede manejar el sistema
sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    data class NavigateToWithPopUp(val route: String, val popUpTo: String) : NavigationEvent()
}