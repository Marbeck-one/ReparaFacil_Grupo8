package com.grupo8.reparafacil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grupo8.reparafacil.ui.screens.*
import com.grupo8.reparafacil.viewmodel.AuthViewModel
import com.grupo8.reparafacil.viewmodel.PerfilViewModel
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    serviciosViewModel: ServiciosViewModel = viewModel(),
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val usuarioActual by authViewModel.usuarioActual.collectAsState()

    // Determinar ruta inicial según rol
    val startDestination = when {
        usuarioActual == null -> AppRoutes.Login
        usuarioActual?.rol == "tecnico" -> AppRoutes.HomeTecnico
        usuarioActual?.rol == "admin" -> AppRoutes.HomeAdmin
        usuarioActual?.rol == "soporte" -> AppRoutes.HomeSoporte
        else -> AppRoutes.HomeCliente
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(AppRoutes.Login) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegistro = {
                    navController.navigate(AppRoutes.Registro)
                },
                onNavigateToRecuperar = {
                    navController.navigate(AppRoutes.RecuperarPassword)
                },
                onNavigateToHome = { rol ->
                    // Redirección inteligente al login
                    val route = when (rol) {
                        "tecnico" -> AppRoutes.HomeTecnico
                        "admin" -> AppRoutes.HomeAdmin
                        "soporte" -> AppRoutes.HomeSoporte
                        else -> AppRoutes.HomeCliente
                    }
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Login) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Recuperación
        composable(AppRoutes.RecuperarPassword) {
            RecuperarPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Registro
        composable(AppRoutes.Registro) {
            RegistroScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegistroExitoso = {
                    val usuario = authViewModel.usuarioActual.value
                    // Por defecto el registro solo crea clientes o técnicos
                    val route = if (usuario?.rol == "tecnico") {
                        AppRoutes.HomeTecnico
                    } else {
                        AppRoutes.HomeCliente
                    }
                    navController.navigate(route) {
                        popUpTo(AppRoutes.Login) { inclusive = true }
                    }
                }
            )
        }

        // Home Cliente
        composable(AppRoutes.HomeCliente) {
            HomeClienteScreen(
                authViewModel = authViewModel,
                serviciosViewModel = serviciosViewModel,
                onNavigateToPerfil = { navController.navigate(AppRoutes.Perfil) },
                onNavigateToSolicitud = { navController.navigate(AppRoutes.SolicitudServicio) },
                onLogout = {
                    navController.navigate(AppRoutes.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // Home Técnico
        composable(AppRoutes.HomeTecnico) {
            HomeTecnicoScreen(
                authViewModel = authViewModel,
                serviciosViewModel = serviciosViewModel,
                onNavigateToPerfil = { navController.navigate(AppRoutes.Perfil) },
                onLogout = {
                    navController.navigate(AppRoutes.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // NUEVO: Home Admin
        composable(AppRoutes.HomeAdmin) {
            HomeAdminScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(AppRoutes.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // NUEVO: Home Soporte
        composable(AppRoutes.HomeSoporte) {
            HomeSoporteScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(AppRoutes.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // Pantallas Comunes
        composable(AppRoutes.Perfil) {
            PerfilScreen(
                perfilViewModel = perfilViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.SolicitudServicio) {
            SolicitudServicioScreen(
                serviciosViewModel = serviciosViewModel,
                onNavigateBack = { navController.popBackStack() },
                onServicioCreado = { navController.popBackStack() }
            )
        }
    }
}