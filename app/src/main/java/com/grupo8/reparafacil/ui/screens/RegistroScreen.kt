package com.grupo8.reparafacil.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import com.grupo8.reparafacil.model.UiState
import com.grupo8.reparafacil.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegistroExitoso: (String) -> Unit
) {
    val registroState by authViewModel.registroState.collectAsState()
    val registroErrores by authViewModel.registroErrores.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }


// Manejar navegación cuando registro es exitoso
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            val authResponse = (loginState as UiState.Success).data
            // Safe access: el backend puede no devolver el objeto user en registro
            val usuario = authResponse.user
            val rol = usuario?.rol ?: "cliente" // Valor por defecto si user es null
            onRegistroExitoso(rol)
            authViewModel.resetLoginState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
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
            Text(
                text = "Únete a ReparaFácil",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = registroErrores.emailError != null,
                supportingText = {
                    if (registroErrores.emailError != null) {
                        Text(registroErrores.emailError!!)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Contraseña
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
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = registroErrores.passwordError != null,
                supportingText = {
                    if (registroErrores.passwordError != null) {
                        Text(registroErrores.passwordError!!)
                    }
                },
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = registroErrores.telefonoError != null,
                supportingText = {
                    if (registroErrores.telefonoError != null) {
                        Text(registroErrores.telefonoError!!)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selección de Rol
            Text(
                text = "Tipo de cuenta",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cliente
                FilterChip(
                    selected = registroState.rol == "cliente",
                    onClick = { authViewModel.actualizarRol("cliente") },
                    label = { Text("Cliente") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Cliente")
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
                        Icon(Icons.Default.Build, contentDescription = "Técnico")
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !registroState.isLoading
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = { authViewModel.validarYRegistrar() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !registroState.isLoading
            ) {
                if (registroState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Cuenta")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de error
            AnimatedVisibility(visible = loginState is UiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (loginState as? UiState.Error)?.message ?: "",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}