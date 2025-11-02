package com.grupo8.reparafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.grupo8.reparafacil.navigation.AppNavigation
import com.grupo8.reparafacil.ui.theme.ReparaFacil_Grupo8Theme
import com.grupo8.reparafacil.viewmodel.AuthViewModel
import com.grupo8.reparafacil.viewmodel.PerfilViewModel
import com.grupo8.reparafacil.viewmodel.ServiciosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReparaFacil_Grupo8Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // ViewModels compartidos
                    val authViewModel: AuthViewModel = viewModel()
                    val perfilViewModel: PerfilViewModel = viewModel()
                    val serviciosViewModel: ServiciosViewModel = viewModel()

                    // Sistema de navegación
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        perfilViewModel = perfilViewModel,
                        serviciosViewModel = serviciosViewModel
                    )
                }
            }
        }
    }
}