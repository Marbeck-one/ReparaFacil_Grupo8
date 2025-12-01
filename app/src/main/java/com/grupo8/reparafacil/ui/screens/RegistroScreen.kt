package com.grupo8.reparafacil.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupo8.reparafacil.model.UiState
import com.grupo8.reparafacil.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateToLogin: () -> Unit,
    onRegistroExitoso: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val registroState by authViewModel.registroState.collectAsState()
    val registroErrores by authViewModel.registroErrores.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    // Observar estado del login para navegar
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onRegistroExitoso()
            authViewModel.resetLoginState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "¡Bienvenido a ReparaFácil!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa tus datos para registrarte",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Nombre
            OutlinedTextField(
                value = registroState.nombre,
                onValueChange = { authViewModel.actualizarNombre(it) },
                label = { Text("Nombre completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Nombre")
                },
                isError = registroErrores.nombreError != null,
                supportingText = {
                    if (registroErrores.nombreError != null) {
                        Text(registroErrores.nombreError!!)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Email
            OutlinedTextField(
                value = registroState.email,
                onValueChange = { authViewModel.actualizarEmail(it) },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                isError = registroErrores.emailError != null,
                supportingText = {
                    if (registroErrores.emailError != null) {
                        Text(registroErrores.emailError!!)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Password
            OutlinedTextField(
                value = registroState.password,
                onValueChange = { authViewModel.actualizarPassword(it) },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Contraseña")
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Settings else Icons.Default.Lock,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = registroErrores.passwordError != null,
                supportingText = {
                    if (registroErrores.passwordError != null) {
                        Text(registroErrores.passwordError!!)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Teléfono
            OutlinedTextField(
                value = registroState.telefono,
                onValueChange = { authViewModel.actualizarTelefono(it) },
                label = { Text("Teléfono") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = "Teléfono")
                },
                isError = registroErrores.telefonoError != null,
                supportingText = {
                    if (registroErrores.telefonoError != null) {
                        Text(registroErrores.telefonoError!!)
                    }
                },
                placeholder = { Text("+56912345678") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Especialidad (solo visible para técnicos)
            AnimatedVisibility(visible = registroState.rol == "tecnico") {
                Column {
                    OutlinedTextField(
                        value = registroState.especialidad,
                        onValueChange = { authViewModel.actualizarEspecialidad(it) },
                        label = { Text("Especialidad") },
                        leadingIcon = {
                            Icon(Icons.Default.Build, contentDescription = "Especialidad")
                        },
                        isError = registroErrores.especialidadError != null,
                        supportingText = {
                            if (registroErrores.especialidadError != null) {
                                Text(registroErrores.especialidadError!!)
                            }
                        },
                        placeholder = { Text("Ej: Reparación de PCs, Plomería, etc.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !registroState.isLoading
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de Rol
            Text(
                text = "Tipo de cuenta",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Cliente
                FilterChip(
                    selected = registroState.rol == "cliente",
                    onClick = { authViewModel.actualizarRol("cliente") },
                    label = { Text("Cliente") },
                    leadingIcon = {
                        if (registroState.rol == "cliente") {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !registroState.isLoading
                )

                // Botón Técnico
                FilterChip(
                    selected = registroState.rol == "tecnico",
                    onClick = { authViewModel.actualizarRol("tecnico") },
                    label = { Text("Técnico") },
                    leadingIcon = {
                        if (registroState.rol == "tecnico") {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                        } else {
                            Icon(Icons.Default.Build, contentDescription = null)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !registroState.isLoading
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar error si existe
            if (loginState is UiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (loginState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón Registrarse
            Button(
                onClick = { authViewModel.validarYRegistrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !registroState.isLoading
            ) {
                if (registroState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link para ir a Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("¿Ya tienes cuenta? ")
                Text(
                    text = "Inicia sesión",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onNavigateToLogin)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}