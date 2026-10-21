package com.coronado.cafecampus.data.repository

import com.coronado.cafecampus.data.local.entity.Producto
import com.coronado.cafecampus.data.local.entity.Usuario
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.data.local.relation.PedidoPorHora
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.model.ItemCarrito
import com.coronado.cafecampus.model.Rol
import kotlinx.coroutines.flow.Flow

/** El contrato. Mañana puede implementarlo un servidor web sin tocar ViewModels ni pantallas. */
interface CafeRepository {
    // Usuarios
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun usuarioPorRol(rol: Rol): Usuario?
    suspend fun registrarEstudiante(nombre: String, correo: String, clave: String): Usuario?

    // Productos (ejemplo resuelto)
    fun observarProductos(): Flow<List<Producto>>
    suspend fun guardarProducto(p: Producto)
    suspend fun eliminarProducto(p: Producto)
    suspend fun cambiarDisponibilidad(id: Int, disponible: Boolean)

    // Pedidos
    suspend fun crearPedido(
        clienteId: Int, items: List<ItemCarrito>,
        horaRecojo: String, pago: String, nota: String
    ): Long
    fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>>
    fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>>
    fun observarPedido(id: Int): Flow<PedidoConDetalle?>
    suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido)

    // Panel
    fun observarVentasHoy(inicioDia: Long): Flow<Double>
    fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>
    fun observarPedidosHoy(inicioDia: Long): Flow<Int>
    fun observarPedidosPorHora(inicioDia: Long): Flow<List<PedidoPorHora>>
}
