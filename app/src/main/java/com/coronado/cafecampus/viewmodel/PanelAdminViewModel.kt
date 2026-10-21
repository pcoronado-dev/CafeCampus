package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.data.local.relation.PedidoPorHora
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class PanelAdminViewModel(private val repo: CafeRepository) : ViewModel() {

    private fun inicioDelDia(): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private val inicioDia = inicioDelDia()

    private fun contador(estado: EstadoPedido): StateFlow<Int> =
        repo.observarCantidadPorEstado(estado)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val nuevos: StateFlow<Int> = contador(EstadoPedido.RECIBIDO)
    val preparando: StateFlow<Int> = contador(EstadoPedido.PREPARANDO)
    val listos: StateFlow<Int> = contador(EstadoPedido.LISTO)

    val ventasHoy: StateFlow<Double> = repo.observarVentasHoy(inicioDia)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pedidosHoy: StateFlow<Int> = repo.observarPedidosHoy(inicioDia)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val nuevosPedidos: StateFlow<List<PedidoConDetalle>> =
        repo.observarPedidosPorEstado(EstadoPedido.RECIBIDO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Fase 2: grafico real calculado con una consulta Room. */
    val pedidosPorHora: StateFlow<List<PedidoPorHora>> = repo.observarPedidosPorHora(inicioDia)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun preparar(pedido: PedidoConDetalle) {
        viewModelScope.launch { repo.cambiarEstado(pedido.pedido.id, EstadoPedido.PREPARANDO) }
    }
}
