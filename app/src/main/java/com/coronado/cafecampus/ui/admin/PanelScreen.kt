@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coronado.cafecampus.data.local.relation.PedidoPorHora
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.ui.theme.coloresDe
import com.coronado.cafecampus.ui.theme.esOscuro
import com.coronado.cafecampus.ui.util.abreviado
import com.coronado.cafecampus.ui.util.codigo
import com.coronado.cafecampus.ui.util.soles
import com.coronado.cafecampus.viewmodel.PanelAdminViewModel

@Composable
fun PanelScreen(vm: PanelAdminViewModel, onVerPedidos: () -> Unit) {
    val nuevos by vm.nuevos.collectAsState()
    val preparando by vm.preparando.collectAsState()
    val listos by vm.listos.collectAsState()
    val ventas by vm.ventasHoy.collectAsState()
    val pedidosHoy by vm.pedidosHoy.collectAsState()
    val porHora by vm.pedidosPorHora.collectAsState()
    val nuevosPedidos by vm.nuevosPedidos.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Panel de control", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Cafetería · Hoy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(Icons.Filled.Notifications, "Notificaciones", Modifier.padding(10.dp))
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Contador("Nuevos", nuevos, EstadoPedido.RECIBIDO, Modifier.weight(1f))
                Contador("Preparando", preparando, EstadoPedido.PREPARANDO, Modifier.weight(1f))
                Contador("Listos", listos, EstadoPedido.LISTO, Modifier.weight(1f))
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Bottom) {
                    Column(Modifier.weight(1f)) {
                        Text("Ventas del día", color = MaterialTheme.colorScheme.onPrimary)
                        Text(
                            ventas.soles(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        "$pedidosHoy pedidos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        item {
            Text("Pedidos por hora", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GraficoPorHora(porHora)
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Nuevos pedidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onVerPedidos) { Text("Ver todos") }
            }
        }
        if (nuevosPedidos.isEmpty()) {
            item { Text("No hay pedidos nuevos.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(nuevosPedidos, key = { it.pedido.id }) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "${p.pedido.id.codigo()} ${p.cliente.nombre.abreviado()}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            p.detalles.joinToString(" · ") { "${it.cantidad} × ${it.nombreProducto}" } +
                                    " · ${p.pedido.horaRecojo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = { vm.preparar(p) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) { Text("Preparar", fontSize = 13.sp) }
                }
            }
        }
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, estado: EstadoPedido, modifier: Modifier = Modifier) {
    val c = coloresDe(estado, esOscuro())
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = c.fondo)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$valor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = c.texto)
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = c.texto)
        }
    }
}

/** Grafico de barras calculado con una consulta Room (de 7 a 20 h). La hora pico resalta. */
@Composable
private fun GraficoPorHora(datos: List<PedidoPorHora>) {
    val horas = (7..20).toList()
    val mapa = datos.associate { it.hora to it.cantidad }
    val maximo = (mapa.values.maxOrNull() ?: 0).coerceAtLeast(1)

    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        horas.forEach { h ->
            val c = mapa[h] ?: 0
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (c > 0) Text("$c", style = MaterialTheme.typography.labelSmall)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((70.dp * c / maximo).coerceAtLeast(2.dp))
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            when {
                                c == 0 -> MaterialTheme.colorScheme.outlineVariant
                                c == maximo -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            }
                        )
                )
            }
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        horas.forEach { h ->
            Text(
                if (h % 2 == 0) "${h}h" else "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}