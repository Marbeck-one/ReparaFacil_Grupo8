package com.grupo8.reparafacil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyStateScreen(
    mensaje: String = "No hay datos disponibles",
    descripcion: String? = null,
    modifier: Modifier = Modifier, // 1. Agregamos modifier para recibir el padding del Scaffold
    onAction: (() -> Unit)? = null, // 2. Callback para la acción
    actionLabel: String? = null     // 3. Texto del botón
) {
    // Usamos el modifier que nos pasan (importante para que no lo tape el menú de arriba)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = "Vacío",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )

            Text(
                text = mensaje,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (descripcion != null) {
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // 4. Agregamos el botón si nos pasan la acción
            if (onAction != null && actionLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onAction) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}