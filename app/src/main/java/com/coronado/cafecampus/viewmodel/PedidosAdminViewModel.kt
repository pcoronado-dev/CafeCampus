package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PedidosAdminViewModel(private val repo: CafeRepository) : ViewModel() {

    private val _estadoSeleccionado = MutableStateFlow(EstadoPedido.RECIBIDO)
    val estadoSeleccionado: StateFlow<EstadoPedido> = _estadoSeleccionado.asStateFlow()

    fun seleccionar(estado: EstadoPedido) { _estadoSeleccionado.value = estado }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pedidos: StateFlow<List<PedidoConDetalle>> = _estadoSeleccionado
        .flatMapLatest { repo.observarPedidosPorEstado(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** RECIBIDO -> PREPARANDO -> LISTO -> ENTREGADO (la logica vive en EstadoPedido). */
    fun avanzar(pedido: PedidoConDetalle) {
        val siguiente = EstadoPedido.desde(pedido.pedido.estado).siguiente() ?: return
        viewModelScope.launch { repo.cambiarEstado(pedido.pedido.id, siguiente) }
    }

    fun cancelar(pedido: PedidoConDetalle) {
        viewModelScope.launch { repo.cambiarEstado(pedido.pedido.id, EstadoPedido.CANCELADO) }
    }

    fun cambiarEstado(pedido: PedidoConDetalle, estado: EstadoPedido) {
        viewModelScope.launch { repo.cambiarEstado(pedido.pedido.id, estado) }
    }

    fun pedido(id: Int): StateFlow<PedidoConDetalle?> = repo.observarPedido(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
