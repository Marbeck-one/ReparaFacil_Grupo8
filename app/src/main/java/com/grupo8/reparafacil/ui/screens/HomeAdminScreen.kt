package com.grupo8.reparafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grupo8.reparafacil.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAudit: () -> Unit // Nuevo parámetro
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrativo") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.cerrarSesion()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido, Administrador",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Tienes acceso total al sistema.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Herramientas de Gestión", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val opciones = listOf(
                "Gestionar Usuarios",
                "Auditoría de Servicios",
                "Reportes Financieros",
                "Configuración Global"
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(opciones.size) { index ->
                    val titulo = opciones[index]
                    ElevatedCard(
                        onClick = {
                            // Si es Auditoría, navegamos
                            if (titulo == "Auditoría de Servicios") {
                                onNavigateToAudit()
                            }
                        }
                    ) {
                        ListItem(
                            headlineContent = { Text(titulo) },
                            leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}