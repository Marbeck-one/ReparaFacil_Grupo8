package com.grupo8.reparafacil.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.grupo8.reparafacil.ui.components.ImagenInteligente
import com.grupo8.reparafacil.ui.components.LoadingScreen
import com.grupo8.reparafacil.viewmodel.AuthViewModel
import com.grupo8.reparafacil.viewmodel.PerfilViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PerfilScreen(
    perfilViewModel: PerfilViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val perfilState by perfilViewModel.perfilState.collectAsState()
    val imagenUri by perfilViewModel.imagenUri.collectAsState()
    val usuarioActual by authViewModel.usuarioActual.collectAsState()

    var mostrarDialogoImagen by remember { mutableStateOf(false) }
    var imageUriTemp by remember { mutableStateOf<Uri?>(null) }

    // +++ ESTE ES EL ARREGLO +++
    // Usamos LaunchedEffect para llamar a cargarPerfil() CADA VEZ
    // que esta pantalla entre en la composición.
    // El 'init' del ViewModel solo se ejecuta una vez, pero esto
    // se ejecutará cada vez que navegues aquí.
    LaunchedEffect(Unit) {
        perfilViewModel.cargarPerfil()
    }
    // +++++++++++++++++++++++++++

    // Permiso de cámara
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    // Launcher para galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            perfilViewModel.actualizarImagenDesdeGaleria(it)
        }
    }

    // Launcher para cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUriTemp != null) {
            perfilViewModel.actualizarImagenDesdeCamara(imageUriTemp!!)
        }
    }

    // Función para abrir cámara
    fun abrirCamara() {
        val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        imageUriTemp = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        camaraLauncher.launch(imageUriTemp)
    }

    // Diálogo de selección
    if (mostrarDialogoImagen) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoImagen = false },
            title = { Text("Seleccionar imagen") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            mostrarDialogoImagen = false
                            if (cameraPermission.status.isGranted) {
                                abrirCamara()
                            } else {
                                cameraPermission.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar foto")
                    }

                    TextButton(
                        onClick = {
                            mostrarDialogoImagen = false
                            galeriaLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Elegir de galería")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoImagen = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
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
        if (perfilState.isLoading) {
            LoadingScreen(mensaje = "Cargando perfil...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Imagen de perfil
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    ImagenInteligente(
                        // Usamos la imagen del estado del VM, que ahora sí
                        // se habrá cargado (o borrado) correctamente.
                        imagenUri = imagenUri,
                        size = 150.dp
                    )

                    // Botón flotante para cambiar imagen
                    FloatingActionButton(
                        onClick = { mostrarDialogoImagen = true },
                        modifier = Modifier.size(40.dp),
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Cambiar imagen",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Información del usuario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Nombre
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nombre:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                // Ahora usamos el usuario del PerfilState,
                                // que se habrá recargado.
                                text = perfilState.usuario?.name ?: "---",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Divider()

                        // Email
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Email:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = perfilState.usuario?.email ?: "---",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Divider()

                        // Teléfono
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Teléfono:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = perfilState.usuario?.telefono ?: "---",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Divider()

                        // Rol
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rol:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Chip(
                                label = {
                                    Text(
                                        text = (perfilState.usuario?.rol ?: "---").uppercase()
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mensaje de error si hay
                AnimatedVisibility(visible = perfilState.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = perfilState.error ?: "",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

// (La función Chip se queda igual)
@Composable
fun Chip(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            label()
        }
    }
}