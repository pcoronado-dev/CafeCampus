package com.coronado.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.model.EstadoPedido

@Composable
fun StepperEstado(
    estado: EstadoPedido,
    modifier: Modifier = Modifier
) {
    val pasos = listOf(
        EstadoPedido.RECIBIDO to "Pedido recibido",
        EstadoPedido.PREPARANDO to "En preparación",
        EstadoPedido.LISTO to "Listo para recoger",
        EstadoPedido.ENTREGADO to "Entregado"
    )

    val ordenActual = estado.ordinal

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
        pasos.forEachIndexed { i, (pEstado, titulo) ->
            val completado = ordenActual >= pEstado.ordinal
            val esActual = ordenActual == pEstado.ordinal

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (completado) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (completado) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal,
                        color = if (completado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (i < pasos.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 13.dp)
                        .size(width = 2.dp, height = 24.dp)
                        .background(
                            if (ordenActual > pEstado.ordinal) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}
