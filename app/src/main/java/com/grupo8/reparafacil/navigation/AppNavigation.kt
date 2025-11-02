package com.grupo8.reparafacil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grupo8.reparafacil.ui.screens.*
import com.grupo8.reparafacil.viewmodel.AuthViewModel
import com.grupo8.reparafacil.viewmodel.PerfilViewModel
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel
import com.grupo8.reparafacil.ui.screens.HomeTecnicoScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    perfilViewModel: PerfilViewModel,
    serviciosViewModel: ServiciosViewModel,
    navigationViewModel: NavigationViewModel = viewModel()
) {
    // Observar eventos de navegación
    val navigationEvent by navigationViewModel.navigationEvent.collectAsState()

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route)
                }
                is NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is NavigationEvent.NavigateToWithPopUp -> {
                    navController.navigate(event.route) {
                        popUpTo(event.popUpTo) { inclusive = true }
                    }
                }
            }
            navigationViewModel.onNavigationHandled()
        }
    }

    // Observar usuario actual para redirigir automáticamente
    val usuarioActual by authViewModel.usuarioActual.collectAsState()
    val startDestination = if (usuarioActual != null) {
        if (usuarioActual?.rol == "tecnico") {
            AppRoutes.HomeTecnico.route
        } else {
            AppRoutes.HomeCliente.route
        }
    } else {
        AppRoutes.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(AppRoutes.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegistro = {
                    navigationViewModel.navigateTo(AppRoutes.Registro.route)
                },
                onNavigateToHome = { rol ->
                    if (rol == "tecnico") {
                        navigationViewModel.navigateToWithPopUp(
                            AppRoutes.HomeTecnico.route,
                            AppRoutes.Login.route
                        )
                    } else {
                        navigationViewModel.navigateToWithPopUp(
                            AppRoutes.HomeCliente.route,
                            AppRoutes.Login.route
                        )
                    }
                }
            )
        }

        // Pantalla de Registro
        composable(AppRoutes.Registro.route) {
            RegistroScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navigationViewModel.navigateBack() },
                onRegistroExitoso = { rol ->
                    if (rol == "tecnico") {
                        navigationViewModel.navigateToWithPopUp(
                            AppRoutes.HomeTecnico.route,
                            AppRoutes.Login.route
                        )
                    } else {
                        navigationViewModel.navigateToWithPopUp(
                            AppRoutes.HomeCliente.route,
                            AppRoutes.Login.route
                        )
                    }
                }
            )
        }

        // Home Cliente
        composable(AppRoutes.HomeCliente.route) {
            HomeClienteScreen(
                authViewModel = authViewModel,
                serviciosViewModel = serviciosViewModel,
                onNavigateToPerfil = {
                    navigationViewModel.navigateTo(AppRoutes.Perfil.route)
                },
                onNavigateToSolicitud = {
                    navigationViewModel.navigateTo(AppRoutes.SolicitudServicio.route)
                },
                onLogout = {
                    navigationViewModel.navigateToWithPopUp(
                        AppRoutes.Login.route,
                        AppRoutes.HomeCliente.route
                    )
                }
            )
        }

        // Home Técnico
        composable(AppRoutes.HomeTecnico.route) {
            HomeTecnicoScreen(
                authViewModel = authViewModel,
                serviciosViewModel = serviciosViewModel,
                onNavigateToPerfil = {
                    navigationViewModel.navigateTo(AppRoutes.Perfil.route)
                },
                onLogout = {
                    navigationViewModel.navigateToWithPopUp(
                        AppRoutes.Login.route,
                        AppRoutes.HomeTecnico.route
                    )
                }
            )
        }

        // Perfil
        composable(AppRoutes.Perfil.route) {
            PerfilScreen(
                perfilViewModel = perfilViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navigationViewModel.navigateBack() }
            )
        }

        // Solicitud de Servicio
        composable(AppRoutes.SolicitudServicio.route) {
            SolicitudServicioScreen(
                serviciosViewModel = serviciosViewModel,
                onNavigateBack = { navigationViewModel.navigateBack() },
                onServicioCreado = { navigationViewModel.navigateBack() }
            )
        }
    }
}