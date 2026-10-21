@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.CafeChip
import com.coronado.cafecampus.ui.components.EstadoVacio
import com.coronado.cafecampus.ui.components.PedidoCard
import com.coronado.cafecampus.viewmodel.PedidosAdminViewModel

private fun etiquetaFiltro(e: EstadoPedido) = when (e) {
    EstadoPedido.RECIBIDO -> "Nuevos"
    EstadoPedido.PREPARANDO -> "Preparando"
    EstadoPedido.LISTO -> "Listos"
    EstadoPedido.ENTREGADO -> "Entregados"
    EstadoPedido.CANCELADO -> "Cancelados"
}

@Composable
fun PedidosAdminScreen(vm: PedidosAdminViewModel, onPedido: (Int) -> Unit) {
    val seleccionado by vm.estadoSeleccionado.collectAsState()
    val pedidos by vm.pedidos.collectAsState()

    Scaffold(topBar = { AppTopBar("Pedidos") }) { padding ->
        Column(Modifier.padding(padding)) {
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoPedido.values().forEach { e ->
                    CafeChip(etiquetaFiltro(e), e == seleccionado, { vm.seleccionar(e) })
                }
            }
            if (pedidos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EstadoVacio(
                        icono = Icons.Filled.Inbox,
                        titulo = "Sin pedidos",
                        mensaje = "No hay pedidos con el estado seleccionado."
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pedidos, key = { it.pedido.id }) { p ->
                        PedidoCard(p, mostrarCliente = true, onClick = { onPedido(p.pedido.id) }) {
                            AccionesPedidoAdmin(p, onAvanzar = { vm.avanzar(p) }, onCancelar = { vm.cancelar(p) })
                        }
                    }
                }
            }
        }
    }
}

/** Botones 'Pasar a…' y 'Cancelar'. La logica del siguiente estado vive en EstadoPedido. */
@Composable
fun AccionesPedidoAdmin(
    pedido: PedidoConDetalle,
    onAvanzar: () -> Unit,
    onCancelar: () -> Unit,
    apilado: Boolean = false,
    textoCancelar: String = "Cancelar"
) {
    val estado = EstadoPedido.desde(pedido.pedido.estado)
    val siguiente = estado.siguiente()
    if (siguiente == null && !estado.activo) return

    if (apilado) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (siguiente != null) {
                CafeButton("Pasar a ${siguiente.etiqueta}", onAvanzar, Modifier.fillMaxWidth())
            }
            if (estado.activo) {
                CafeButton(textoCancelar, onCancelar, Modifier.fillMaxWidth(), outlined = true, peligro = true)
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (siguiente != null) {
                CafeButton("Pasar a ${siguiente.etiqueta}", onAvanzar, Modifier.weight(1f))
            }
            if (estado.activo) {
                CafeButton(textoCancelar, onCancelar, Modifier.weight(1f), outlined = true, peligro = true)
            }
        }
    }
}
