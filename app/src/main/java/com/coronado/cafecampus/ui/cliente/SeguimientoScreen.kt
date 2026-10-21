@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.StepperEstado
import com.coronado.cafecampus.ui.util.codigo
import com.coronado.cafecampus.ui.util.soles
import com.coronado.cafecampus.viewmodel.PedidosClienteViewModel

/** Se actualiza SOLO cuando el administrador cambia el estado (Flow de Room). */
@Composable
fun SeguimientoScreen(pedidoId: Int, vm: PedidosClienteViewModel, onBack: () -> Unit) {
    val flujo = remember(pedidoId) { vm.pedido(pedidoId) }
    val pedido by flujo.collectAsState()

    Scaffold(topBar = { AppTopBar("Seguimiento", onBack = onBack) }) { padding ->
        val actual = pedido
        if (actual == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val p = actual.pedido
            val estado = EstadoPedido.desde(p.estado)
            val onPrimario = MaterialTheme.colorScheme.onPrimary

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Zona desplazable: cabecera + stepper
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Tarjeta de cabecera naranja
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    "Pedido ${p.id.codigo()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = onPrimario
                                )
                                Text(
                                    if (p.horaRecojo == "Ahora") "Recojo inmediato" else "Recojo a las ${p.horaRecojo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onPrimario
                                )
                                Text(
                                    if (estado.activo) "Tiempo estimado: 10 min" else "Pedido ${estado.etiqueta.lowercase()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = onPrimario
                                )
                            }
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, onPrimario.copy(alpha = 0.7f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Schedule, null, tint = onPrimario, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    // Estados en vertical (sin tarjeta, directo sobre el fondo)
                    StepperEstado(
                        estado = estado,
                        modifier = Modifier.padding(start = 8.dp, top = 24.dp, bottom = 16.dp)
                    )
                }

                // Resumen fijo abajo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            actual.detalles.joinToString("  ·  ") { "${it.cantidad} x ${it.nombreProducto}" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Total  ${p.total.soles()}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
