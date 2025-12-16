package com.grupo8.reparafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grupo8.reparafacil.model.UiState
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditScreen(
    viewModel: ServiciosViewModel,
    onNavigateBack: () -> Unit
) {
    val serviciosState by viewModel.serviciosState.collectAsState()

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarServicios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auditoría de Servicios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = serviciosState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}")
                    }
                }
                is UiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { servicio ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = servicio.tipo,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Cliente ID: ${servicio.usuario ?: "Desconocido"}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Estado: ${servicio.estado}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if(servicio.estado == "Pendiente") Color.Red else Color.Green
                                        )
                                    }

                                    // Botón Eliminar
                                    IconButton(
                                        onClick = { viewModel.eliminarServicio(servicio.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}