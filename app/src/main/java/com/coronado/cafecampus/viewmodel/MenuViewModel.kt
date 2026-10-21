package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.cafecampus.data.local.entity.Producto
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.model.Categorias
import com.coronado.cafecampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MenuViewModel(private val repo: CafeRepository) : ViewModel() {

    // ---------- Productos (Flow -> StateFlow con stateIn) ----------
    val productos: StateFlow<List<Producto>> = repo.observarProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorias: StateFlow<List<String>> = productos
        .map { lista -> Categorias.ordenar(lista.map { it.categoria }.distinct()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Fase 2: busqueda + filtro por categoria (logica en el ViewModel) ----------
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()

    fun buscar(texto: String) { _busqueda.value = texto }
    fun elegirCategoria(c: String?) { _categoria.value = c }

    val productosVisibles: StateFlow<List<Producto>> =
        combine(productos, _busqueda, _categoria) { lista, texto, cat ->
            lista.filter { p ->
                (cat == null || p.categoria == cat) &&
                        p.nombre.contains(texto.trim(), ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Carrito ----------
    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito.asStateFlow()

    /** Si el producto ya esta en el carrito, suma la cantidad en vez de duplicar la linea. */
    fun agregar(producto: Producto, cantidad: Int, nota: String = "") {
        if (!producto.disponible || cantidad < 1) return
        _carrito.update { lista ->
            val existente = lista.firstOrNull { it.producto.id == producto.id }
            if (existente == null) {
                lista + ItemCarrito(producto, cantidad, nota.trim())
            } else {
                lista.map {
                    if (it.producto.id == producto.id) {
                        it.copy(
                            cantidad = it.cantidad + cantidad,
                            nota = if (nota.isBlank()) it.nota else nota.trim()
                        )
                    } else it
                }
            }
        }
    }

    fun repetir(pedido: PedidoConDetalle, onListo: () -> Unit) {
        val listaProductos = productos.value
        _carrito.value = pedido.detalles.map { d ->
            val p = listaProductos.firstOrNull { it.id == d.productoId }
                ?: Producto(
                    id = d.productoId,
                    nombre = d.nombreProducto,
                    descripcion = "",
                    categoria = "",
                    precio = d.precioUnitario,
                    disponible = true,
                    icono = "cafeteria"
                )
            ItemCarrito(p, d.cantidad, "")
        }
        onListo()
    }

    fun quitar(productoId: Int) {
        _carrito.update { lista -> lista.filterNot { it.producto.id == productoId } }
    }

    fun vaciar() { _carrito.value = emptyList() }

    fun total(): Double = _carrito.value.sumOf { it.subtotal }
}
