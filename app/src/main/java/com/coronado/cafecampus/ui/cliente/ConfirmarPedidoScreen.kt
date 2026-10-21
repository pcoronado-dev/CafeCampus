@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.coronado.cafecampus.ui.cliente

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.ProductoMiniatura
import com.coronado.cafecampus.ui.util.soles
import com.coronado.cafecampus.viewmodel.MenuViewModel
import com.coronado.cafecampus.viewmodel.PedidosClienteViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ConfirmarPedidoScreen(
    menuVm: MenuViewModel,
    pedidosVm: PedidosClienteViewModel,
    cafeteriaAbierta: Boolean,
    onBack: () -> Unit,
    onSeguirPidiendo: () -> Unit,
    onConfirmado: (Int) -> Unit
) {
    val contexto = LocalContext.current
    val carrito by menuVm.carrito.collectAsState()
    val enviando by pedidosVm.enviando.collectAsState()
    val total = carrito.sumOf { it.subtotal }

    // "Ahora" + las tres próximas medias horas (10:30 · 11:00 · 11:30)
    val horas = remember {
        val formato = DateTimeFormatter.ofPattern("HH:mm")
        val ahora = LocalTime.now().withSecond(0).withNano(0)
        val proxima = ahora.plusMinutes((30 - ahora.minute % 30).toLong())
        listOf("Ahora") + listOf(0L, 30L, 60L).map { proxima.plusMinutes(it).format(formato) }
    }
    var hora by rememberSaveable { mutableStateOf("") }
    var pago by rememberSaveable { mutableStateOf("CAJA") }
    val horaElegida = hora.ifEmpty { horas[1] }

    Scaffold(topBar = { AppTopBar("Confirmar pedido", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (carrito.isEmpty()) {
                Text(
                    "Tu bolsa está vacía.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CafeButton("Ver el menú", onSeguirPidiendo, Modifier.fillMaxWidth())
            } else {
                if (!cafeteriaAbierta) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "Cafetería cerrada: no se pueden confirmar pedidos por ahora.",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Líneas del pedido (mantén presionado una línea para quitarla)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        carrito.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            menuVm.quitar(item.producto.id)
                                            Toast.makeText(contexto, "Producto quitado", Toast.LENGTH_SHORT).show()
                                        }
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ProductoMiniatura(item.producto, 44.dp)
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        item.producto.nombre,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "x${item.cantidad}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    item.subtotal.soles(),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Hora de recojo
                Text("Hora de recojo", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    horas.forEach { h ->
                        ChipAncho(h, h == horaElegida, { hora = h }, Modifier.weight(1f))
                    }
                }

                // Forma de pago
                Text("Forma de pago", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OpcionPago("Pagar en caja", pago == "CAJA", { pago = "CAJA" }, Modifier.weight(1f))
                    OpcionPago("Yape / Plin", pago == "YAPE", { pago = "YAPE" }, Modifier.weight(1f))
                }

                // Resumen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilaResumen("Subtotal", total.soles())
                        FilaResumen("Servicio", 0.0.soles())
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(
                                total.soles(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                CafeButton(
                    texto = "Confirmar pedido",
                    enabled = cafeteriaAbierta && !enviando,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // Las notas de cada producto viajan en la nota del pedido
                        val notaFinal = carrito
                            .filter { it.nota.isNotBlank() }
                            .joinToString(" · ") { "${it.producto.nombre}: ${it.nota}" }
                        pedidosVm.confirmar(carrito, horaElegida, pago, notaFinal) { id ->
                            menuVm.vaciar()
                            onConfirmado(id)
                        }
                    }
                )
                CafeButton(
                    texto = "Seguir pidiendo",
                    onClick = onSeguirPidiendo,
                    outlined = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FilaResumen(etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(valor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
    }
}

/** Chip de hora: ocupa todo el ancho disponible; seleccionado = naranja con texto blanco. */
@Composable
private fun ChipAncho(texto: String, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            texto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Opción de pago: seleccionada = relleno naranja claro, borde y texto naranja. */
@Composable
private fun OpcionPago(texto: String, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            if (seleccionado) 2.dp else 1.dp,
            if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            texto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
