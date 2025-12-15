package com.grupo8.reparafacil.ui.screens

import android.Manifest // Import necesario
import androidx.activity.compose.rememberLauncherForActivityResult // Import necesario
import androidx.activity.result.contract.ActivityResultContracts // Import necesario
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Import necesario
import androidx.compose.ui.unit.dp
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudServicioScreen(
    serviciosViewModel: ServiciosViewModel,
    onNavigateBack: () -> Unit,
    onServicioCreado: () -> Unit
) {
    val solicitudState by serviciosViewModel.solicitudState.collectAsState()
    val solicitudErrores by serviciosViewModel.solicitudErrores.collectAsState()

    // Variable para saber si el usuario ya intentó enviar el formulario.
    var formularioEnviado by rememberSaveable { mutableStateOf(false) }

    var expandedTipo by remember { mutableStateOf(false) }
    val tiposServicio = listOf(
        "Refrigerador",
        "Lavadora",
        "Secadora",
        "Aire Acondicionado",
        "Horno",
        "Microondas",
        "Lavavajillas",
        "Televisor",
        "Computadora",
        "Plomería",
        "Electricidad",
        "Otro"
    )

    // Navegar de vuelta SOLO si se ha enviado el formulario y el estado se limpió (éxito)
    LaunchedEffect(solicitudState.isLoading, solicitudState.tipo, solicitudState.descripcion) {
        if (formularioEnviado &&
            !solicitudState.isLoading &&
            solicitudState.tipo.isEmpty() &&
            solicitudState.descripcion.isEmpty()) {

            kotlinx.coroutines.delay(1500)
            onServicioCreado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Reparación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono decorativo
            Icon(
                Icons.Default.Build,
                contentDescription = "Reparación",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nueva Solicitud",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Completa los datos del servicio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de tipo de servicio
            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = !expandedTipo }
            ) {
                OutlinedTextField(
                    value = solicitudState.tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de servicio") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo)
                    },
                    isError = solicitudErrores.tipoError != null,
                    supportingText = {
                        if (solicitudErrores.tipoError != null) {
                            Text(solicitudErrores.tipoError!!)
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = "Tipo")
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    enabled = !solicitudState.isLoading
                )

                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    tiposServicio.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                serviciosViewModel.actualizarTipo(tipo)
                                expandedTipo = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción del problema
            OutlinedTextField(
                value = solicitudState.descripcion,
                onValueChange = { serviciosViewModel.actualizarDescripcion(it) },
                label = { Text("Descripción del problema") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = "Descripción")
                },
                isError = solicitudErrores.descripcionError != null,
                supportingText = {
                    if (solicitudErrores.descripcionError != null) {
                        Text(solicitudErrores.descripcionError!!)
                    } else {
                        Text("Mínimo 10 caracteres")
                    }
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                enabled = !solicitudState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INICIO BLOQUE DIRECCIÓN CON GPS ---
            val context = LocalContext.current

            // Lanzador de permisos (pide Fine y Coarse location)
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

                if (isGranted) {
                    serviciosViewModel.obtenerUbicacionActual(context)
                } else {
                    // Opcional: Manejar denegación
                }
            }

            OutlinedTextField(
                value = solicitudState.direccion,
                onValueChange = { serviciosViewModel.actualizarDireccion(it) },
                label = { Text("Dirección") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = "Dirección")
                },
                // AGREGAMOS EL BOTÓN DE GPS A LA DERECHA
                trailingIcon = {
                    IconButton(onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) {
                        Icon(
                            Icons.Default.MyLocation, // Ícono de mira/gps
                            contentDescription = "Usar ubicación actual",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                isError = solicitudErrores.direccionError != null,
                supportingText = {
                    if (solicitudErrores.direccionError != null) {
                        Text(solicitudErrores.direccionError!!)
                    } else {
                        Text("Ingresa manual o usa el botón GPS")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !solicitudState.isLoading
            )
            // --- FIN BLOQUE DIRECCIÓN ---

            Spacer(modifier = Modifier.height(24.dp))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Un técnico será asignado pronto y se pondrá en contacto contigo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de enviar
            Button(
                onClick = {
                    formularioEnviado = true // Marcamos que el usuario intentó enviar
                    serviciosViewModel.validarYCrearServicio()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !solicitudState.isLoading
            ) {
                if (solicitudState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando...")
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Solicitar Servicio")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animación de éxito
            AnimatedVisibility(
                visible = formularioEnviado &&
                        !solicitudState.isLoading &&
                        solicitudState.tipo.isEmpty() &&
                        solicitudState.descripcion.isEmpty()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "¡Servicio solicitado con éxito!",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}