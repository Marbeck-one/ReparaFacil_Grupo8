package com.grupo8.reparafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grupo8.reparafacil.model.Servicio
import com.grupo8.reparafacil.model.UiState
import com.grupo8.reparafacil.ui.components.EmptyStateScreen
import com.grupo8.reparafacil.ui.components.ErrorScreen
import com.grupo8.reparafacil.ui.components.LoadingScreen
import com.grupo8.reparafacil.viewmodel.AuthViewModel
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeClienteScreen(
    authViewModel: AuthViewModel,
    serviciosViewModel: ServiciosViewModel,
    onNavigateToPerfil: () -> Unit,
    onNavigateToSolicitud: () -> Unit,
    onLogout: () -> Unit
) {
    val usuarioActual by authViewModel.usuarioActual.collectAsState()
    val serviciosState by serviciosViewModel.serviciosState.collectAsState()

    var mostrarMenuUsuario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        serviciosViewModel.cargarServicios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ReparaFácil")
                        Text(
                            text = "Hola, ${usuarioActual?.name ?: "Cliente"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarMenuUsuario = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }

                    DropdownMenu(
                        expanded = mostrarMenuUsuario,
                        onDismissRequest = { mostrarMenuUsuario = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi Perfil") },
                            onClick = {
                                mostrarMenuUsuario = false
                                onNavigateToPerfil()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "Perfil")
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                mostrarMenuUsuario = false
                                authViewModel.cerrarSesion()
                                onLogout()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = "Salir")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToSolicitud,
                icon = { Icon(Icons.Default.Add, contentDescription = "Solicitar") },
                text = { Text("Nueva Reparación") }
            )
        }
    ) { paddingValues ->
        when (serviciosState) {
            is UiState.Idle -> {
                LoadingScreen(mensaje = "Preparando...")
            }

            is UiState.Loading -> {
                LoadingScreen(mensaje = "Cargando servicios...")
            }

            is UiState.Error -> {
                ErrorScreen(
                    mensaje = (serviciosState as UiState.Error).message,
                    onRetry = { serviciosViewModel.cargarServicios() }
                )
            }

            is UiState.Success -> {
                val servicios = (serviciosState as UiState.Success<List<Servicio>>).data

                if (servicios.isEmpty()) {
                    EmptyStateScreen(
                        mensaje = "No tienes servicios",
                        descripcion = "Solicita tu primera reparación presionando el botón +"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Mis Servicios",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(servicios) { servicio ->
                            ServicioCard(servicio = servicio)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServicioCard(servicio: Servicio) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header con tipo y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = servicio.tipo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                EstadoChip(estado = servicio.estado)
            }

            Divider()

            // Descripción
            Text(
                text = servicio.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Dirección
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = servicio.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Solicitado: ${servicio.fechaSolicitud}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Garantía
            if (servicio.garantia) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Garantía",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Con garantía",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
fun EstadoChip(estado: String) {
    val color = when (estado.lowercase()) {
        "pendiente" -> MaterialTheme.colorScheme.errorContainer
        "asignado" -> MaterialTheme.colorScheme.tertiaryContainer
        "en_proceso" -> MaterialTheme.colorScheme.secondaryContainer
        "completado" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when (estado.lowercase()) {
        "pendiente" -> MaterialTheme.colorScheme.onErrorContainer
        "asignado" -> MaterialTheme.colorScheme.onTertiaryContainer
        "en_proceso" -> MaterialTheme.colorScheme.onSecondaryContainer
        "completado" -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color
    ) {
        Text(
            text = estado.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}