package com.coronado.cafecampus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

    val infiniteTransition = rememberInfiniteTransition(label = "HaloPasoEnCurso")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloScale"
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloAlpha"
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
        pasos.forEachIndexed { i, (pEstado, titulo) ->
            val completado = ordenActual >= pEstado.ordinal
            val esActual = ordenActual == pEstado.ordinal

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    if (esActual) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .scale(haloScale)
                                .alpha(haloAlpha)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

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
                        this@Row.AnimatedVisibility(
                            visible = completado,
                            enter = scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
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
                val lineaAnimada by animateFloatAsState(
                    targetValue = if (ordenActual > pEstado.ordinal) 1f else 0f,
                    animationSpec = tween(600),
                    label = "LineaPasoAnimada"
                )

                Box(
                    modifier = Modifier
                        .padding(start = 13.dp)
                        .width(2.dp)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(24.dp * lineaAnimada)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}
