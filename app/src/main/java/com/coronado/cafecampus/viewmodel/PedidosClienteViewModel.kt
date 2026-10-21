package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.model.ItemCarrito
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PedidosClienteViewModel(private val repo: CafeRepository) : ViewModel() {

    private val clienteId = MutableStateFlow<Int?>(null)

    fun establecerCliente(id: Int) { clienteId.value = id }

    @OptIn(ExperimentalCoroutinesApi::class)
    val misPedidos: StateFlow<List<PedidoConDetalle>> = clienteId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repo.observarPedidosCliente(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _enviando = MutableStateFlow(false)
    val enviando: StateFlow<Boolean> = _enviando.asStateFlow()

    /** Crea el pedido con estado RECIBIDO y avisa con el id creado. */
    fun confirmar(
        items: List<ItemCarrito>, horaRecojo: String, pago: String, nota: String,
        onCreado: (Int) -> Unit
    ) {
        val id = clienteId.value ?: return
        if (items.isEmpty() || _enviando.value) return
        _enviando.value = true
        viewModelScope.launch {
            val pedidoId = repo.crearPedido(id, items, horaRecojo, pago, nota)
            _enviando.value = false
            onCreado(pedidoId.toInt())
        }
    }

    /** Seguimiento: se actualiza solo cuando el administrador cambia el estado. */
    fun pedido(id: Int): StateFlow<PedidoConDetalle?> = repo.observarPedido(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
