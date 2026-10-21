@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.model.Rol
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeChip
import com.coronado.cafecampus.ui.components.EstadoChip
import com.coronado.cafecampus.ui.theme.esOscuro
import com.coronado.cafecampus.ui.util.codigo
import com.coronado.cafecampus.ui.util.fechaRelativa
import com.coronado.cafecampus.ui.util.soles
import com.coronado.cafecampus.viewmodel.PedidosAdminViewModel

@Composable
fun DetallePedidoAdminScreen(pedidoId: Int, vm: PedidosAdminViewModel, onBack: () -> Unit) {
    val flujo = remember(pedidoId) { vm.pedido(pedidoId) }
    val pedido by flujo.collectAsState()

    Scaffold(topBar = { AppTopBar("Detalle del pedido", onBack = onBack) }) { padding ->
        val actual = pedido
        if (actual == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val p = actual.pedido
            val estado = EstadoPedido.desde(p.estado)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Pedido ${p.id.codigo()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${p.fecha.fechaRelativa()} · Recojo ${p.horaRecojo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    EstadoChip(estado)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Cliente", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${actual.cliente.nombre} · ${Rol.desde(actual.cliente.rol)?.etiqueta ?: ""}",
                            fontWeight = FontWeight.SemiBold
                        )
                        HorizontalDivider()
                        actual.detalles.forEach { d ->
                            Row {
                                Text(d.nombreProducto, modifier = Modifier.weight(1f))
                                Text("x${d.cantidad}", modifier = Modifier.padding(horizontal = 16.dp))
                                Text((d.cantidad * d.precioUnitario).soles())
                            }
                        }
                        HorizontalDivider()
                        Row {
                            Text("Total", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(p.total.soles(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Nota del cliente (tarjeta ambar)
                if (p.nota.isNotBlank()) {
                    val oscuro = esOscuro()
                    val texto = if (oscuro) Color(0xFFFDE68A) else Color(0xFF92400E)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (oscuro) Color(0xFF78350F) else Color(0xFFFEF3C7)
                        )
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Nota del cliente", fontWeight = FontWeight.SemiBold, color = texto)
                            Text(p.nota, color = texto)
                        }
                    }
                }

                Text("Cambiar estado", fontWeight = FontWeight.SemiBold)
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        EstadoPedido.RECIBIDO, EstadoPedido.PREPARANDO,
                        EstadoPedido.LISTO, EstadoPedido.ENTREGADO
                    ).forEach { e ->
                        CafeChip(e.etiqueta, e == estado, { vm.cambiarEstado(actual, e) })
                    }
                }

                AccionesPedidoAdmin(
                    actual,
                    onAvanzar = { vm.avanzar(actual) },
                    onCancelar = { vm.cancelar(actual) },
                    apilado = true,
                    textoCancelar = "Cancelar pedido"
                )
            }
        }
    }
}
