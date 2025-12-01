package com.grupo8.reparafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupo8.reparafacil.navigation.AppNavigation
import com.grupo8.reparafacil.ui.theme.ReparaFacilTheme
import com.grupo8.reparafacil.viewmodel.AuthViewModel
import com.grupo8.reparafacil.viewmodel.PerfilViewModel
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReparaFacilApp()
        }
    }
}

@Composable
fun ReparaFacilApp() {
    // Los ViewModels se crean aquí dentro del contexto @Composable
    val authViewModel: AuthViewModel = viewModel()
    val serviciosViewModel: ServiciosViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()

    ReparaFacilTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(
                authViewModel = authViewModel,
                serviciosViewModel = serviciosViewModel,
                perfilViewModel = perfilViewModel
            )
        }
    }
}