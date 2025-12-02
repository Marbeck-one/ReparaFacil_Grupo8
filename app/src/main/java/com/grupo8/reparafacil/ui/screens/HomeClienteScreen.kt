package com.grupo8.reparafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

    // Estados del ViewModel (Original y Filtrados)
    val serviciosState by serviciosViewModel.serviciosState.collectAsState()
    val serviciosFiltrados by serviciosViewModel.serviciosFiltrados.collectAsState() // Asegúrate de tener esto en el VM
    val searchQuery by serviciosViewModel.busquedaQuery.collectAsState()
    val activeFilter by serviciosViewModel.filtroEstado.collectAsState()

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
                            text = "Hola, ${usuarioActual?.nombre ?: "Cliente"}",
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

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // --- SECCIÓN DE FILTROS (Solo si hay datos cargados con éxito) ---
            if (serviciosState is UiState.Success && (serviciosState as UiState.Success).data.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Barra de Búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { serviciosViewModel.onBusquedaChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por descripción o tipo...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    // 2. Chips de Categoría/Estado
                    val filtros = listOf("Todos", "Pendiente", "Asignado", "En_Proceso", "Completado")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtros) { filtro ->
                            FilterChip(
                                selected = activeFilter.equals(filtro, ignoreCase = true),
                                onClick = { serviciosViewModel.onFiltroEstadoChange(filtro) },
                                label = { Text(filtro.replace("_", " ")) },
                                leadingIcon = if (activeFilter.equals(filtro, ignoreCase = true)) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
                Divider()
            }

            // --- ESTADOS DE LA UI ---
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
                    // Verificar si la lista FILTRADA está vacía
                    if (serviciosFiltrados.isEmpty()) {
                        // Caso 1: No hay servicios en absoluto (ni siquiera en la lista original)
                        if ((serviciosState as UiState.Success).data.isEmpty()) {
                            EmptyStateScreen(
                                modifier = Modifier.fillMaxSize(),
                                mensaje = "No tienes servicios",
                                descripcion = "Solicita tu primera reparación ahora mismo",
                                onAction = onNavigateToSolicitud,
                                actionLabel = "Nueva Reparación"
                            )
                        } else {
                            // Caso 2: Hay servicios, pero el filtro no encontró coincidencias
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron resultados para tu búsqueda.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // Caso 3: Mostrar lista filtrada
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Resultados (${serviciosFiltrados.size})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(serviciosFiltrados) { servicio ->
                                ServicioCard(servicio = servicio)
                            }
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