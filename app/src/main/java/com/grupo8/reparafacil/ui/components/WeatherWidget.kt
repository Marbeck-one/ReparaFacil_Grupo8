package com.grupo8.reparafacil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grupo8.reparafacil.network.WeatherApiService

@Composable
fun WeatherWidget() {
    // Estado local simple para no complicar el ViewModel principal
    var temperatura by remember { mutableStateOf<Double?>(null) }

    // Coordenadas fijas de Santiago (puedes cambiarlas luego con GPS)
    LaunchedEffect(Unit) {
        try {
            val api = WeatherApiService.create()
            val response = api.getWeather(-33.4489, -70.6693) // Santiago
            temperatura = response.current_weather.temperature
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (temperatura != null) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Santiago: $temperatura°C",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}