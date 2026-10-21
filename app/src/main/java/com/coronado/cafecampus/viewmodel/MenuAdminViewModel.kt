package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.cafecampus.data.local.entity.Producto
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.model.Categorias
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuAdminViewModel(private val repo: CafeRepository) : ViewModel() {

    val productos: StateFlow<List<Producto>> = repo.observarProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorias: StateFlow<List<String>> = productos
        .map { l -> Categorias.ordenar(l.map { it.categoria }.distinct()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()
    fun elegirCategoria(c: String?) { _categoria.value = c }

    val productosVisibles: StateFlow<List<Producto>> = combine(productos, _categoria) { l, c ->
        if (c == null) l else l.filter { it.categoria == c }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Nuevo (id = 0) o editado (id existente). */
    fun guardar(producto: Producto) {
        viewModelScope.launch { repo.guardarProducto(producto) }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch { repo.eliminarProducto(producto) }
    }

    fun alternarDisponibilidad(producto: Producto) {
        viewModelScope.launch { repo.cambiarDisponibilidad(producto.id, !producto.disponible) }
    }

    suspend fun obtener(id: Int): Producto? = repo.observarProductos().first().firstOrNull { it.id == id }
}
