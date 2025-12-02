package com.grupo8.reparafacil.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
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
fun HomeTecnicoScreen(
    authViewModel: AuthViewModel,
    serviciosViewModel: ServiciosViewModel,
    onNavigateToPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    val usuarioActual by authViewModel.usuarioActual.collectAsState()

    // Estados del ViewModel
    val serviciosState by serviciosViewModel.serviciosState.collectAsState()
    val serviciosFiltrados by serviciosViewModel.serviciosFiltrados.collectAsState()
    val searchQuery by serviciosViewModel.busquedaQuery.collectAsState()
    val activeFilter by serviciosViewModel.filtroEstado.collectAsState()

    var mostrarMenuUsuario by remember { mutableStateOf(false) }

    // Estado para el diálogo de cambio de estado
    var servicioSeleccionado by remember { mutableStateOf<Servicio?>(null) }
    var mostrarDialogoEstado by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        serviciosViewModel.cargarServicios()
    }

    // --- DIÁLOGO PARA CAMBIAR ESTADO ---
    if (mostrarDialogoEstado && servicioSeleccionado != null) {
        CambiarEstadoDialog(
            servicio = servicioSeleccionado!!,
            onDismiss = {
                mostrarDialogoEstado = false
                servicioSeleccionado = null
            },
            onEstadoSelected = { nuevoEstado ->
                serviciosViewModel.cambiarEstadoServicio(servicioSeleccionado!!.id, nuevoEstado)
                mostrarDialogoEstado = false
                servicioSeleccionado = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Técnico")
                        Text(
                            text = "Hola, ${usuarioActual?.nombre ?: "Técnico"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { serviciosViewModel.cargarServicios() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                    IconButton(onClick = { mostrarMenuUsuario = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }

                    DropdownMenu(
                        expanded = mostrarMenuUsuario,
                        onDismissRequest = { mostrarMenuUsuario = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi Perfil") },
                            onClick = { mostrarMenuUsuario = false; onNavigateToPerfil() },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = { mostrarMenuUsuario = false; authViewModel.cerrarSesion(); onLogout() },
                            leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // --- SECCIÓN DE FILTROS (Visible si hay datos) ---
            if (serviciosState is UiState.Success && (serviciosState as UiState.Success).data.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Barra de Búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { serviciosViewModel.onBusquedaChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por ID, cliente o falla...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            focusedLabelColor = MaterialTheme.colorScheme.secondary
                        )
                    )

                    // Chips de Estado
                    val filtros = listOf("Todos", "Pendiente", "Asignado", "En_Proceso", "Completado")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filtros) { filtro ->
                            FilterChip(
                                selected = activeFilter.equals(filtro, ignoreCase = true),
                                onClick = { serviciosViewModel.onFiltroEstadoChange(filtro) },
                                label = { Text(filtro.replace("_", " ")) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
                Divider()
            }

            // --- CONTENIDO PRINCIPAL ---
            when (serviciosState) {
                is UiState.Idle -> LoadingScreen(mensaje = "Preparando panel...")
                is UiState.Loading -> LoadingScreen(mensaje = "Sincronizando trabajos...")
                is UiState.Error -> ErrorScreen(
                    mensaje = (serviciosState as UiState.Error).message,
                    onRetry = { serviciosViewModel.cargarServicios() }
                )
                is UiState.Success -> {
                    if (serviciosFiltrados.isEmpty()) {
                        if ((serviciosState as UiState.Success).data.isEmpty()) {
                            EmptyStateScreen(
                                mensaje = "Sin asignaciones",
                                descripcion = "No hay reparaciones pendientes en el sistema."
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron reparaciones con ese filtro.")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Listado de Trabajos (${serviciosFiltrados.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            items(serviciosFiltrados) { servicio ->
                                ServicioTecnicoCard(
                                    servicio = servicio,
                                    onClick = {
                                        servicioSeleccionado = servicio
                                        mostrarDialogoEstado = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServicioTecnicoCard(
    servicio: Servicio,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick, // Hace toda la tarjeta clickeable
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Tipo y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = servicio.tipo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                EstadoChip(estado = servicio.estado)
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // Body
            Text(
                text = servicio.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )

            // Footer: Dirección y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(
                        text = servicio.direccion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = servicio.fechaSolicitud,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Indicador de acción
            Text(
                text = "Toca para actualizar estado",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun CambiarEstadoDialog(
    servicio: Servicio,
    onDismiss: () -> Unit,
    onEstadoSelected: (String) -> Unit
) {
    val estadosDisponibles = listOf("pendiente", "asignado", "en_proceso", "completado")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Actualizar Estado") },
        text = {
            Column {
                Text("Servicio: ${servicio.tipo}")
                Text("Estado actual: ${servicio.estado.uppercase()}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selecciona nuevo estado:", style = MaterialTheme.typography.labelMedium)

                estadosDisponibles.forEach { estado ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEstadoSelected(estado) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = servicio.estado.equals(estado, ignoreCase = true),
                            onClick = { onEstadoSelected(estado) }
                        )
                        Text(
                            text = estado.replace("_", " ").uppercase(),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}