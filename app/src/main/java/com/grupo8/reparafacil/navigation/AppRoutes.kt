package com.grupo8.reparafacil.navigation

// Sealed class para seguridad de tipos en las rutas
sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("login")
    object Registro : AppRoutes("registro")
    object HomeCliente : AppRoutes("home_cliente")
    object HomeTecnico : AppRoutes("home_tecnico")
    object Perfil : AppRoutes("perfil")
    object SolicitudServicio : AppRoutes("solicitud_servicio")
    object ListaServicios : AppRoutes("lista_servicios")
}