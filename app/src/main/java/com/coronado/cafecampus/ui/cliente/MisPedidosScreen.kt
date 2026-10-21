@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.PedidoCard
import com.coronado.cafecampus.ui.components.SelectorSegmentado
import com.coronado.cafecampus.viewmodel.PedidosClienteViewModel

@Composable
fun MisPedidosScreen(
    vm: PedidosClienteViewModel,
    onPedido: (Int) -> Unit,
    onRepetir: (PedidoConDetalle) -> Unit
) {
    val pedidos by vm.misPedidos.collectAsState()
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    // Activos = RECIBIDO, PREPARANDO, LISTO
    val activos = pedidos.filter { EstadoPedido.desde(it.pedido.estado).activo }
    val historial = pedidos.filter { !EstadoPedido.desde(it.pedido.estado).activo }
    val lista = if (pestana == 0) activos else historial

    Scaffold(topBar = { AppTopBar("Mis pedidos") }) { padding ->
        Column(Modifier.padding(padding)) {
            SelectorSegmentado(
                opciones = listOf("Activos", "Historial"),
                seleccion = pestana,
                onSeleccion = { pestana = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (lista.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (pestana == 0) "No tienes pedidos activos." else "Aún no hay pedidos en tu historial.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.pedido.id }) { p ->
                        PedidoCard(p, onClick = { onPedido(p.pedido.id) }) {
                            if (pestana == 1) {
                                CafeButton(
                                    texto = "Repetir pedido",
                                    onClick = { onRepetir(p) },
                                    outlined = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
