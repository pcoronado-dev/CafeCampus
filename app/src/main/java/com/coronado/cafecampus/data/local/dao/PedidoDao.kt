package com.coronado.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.coronado.cafecampus.data.local.entity.DetallePedido
import com.coronado.cafecampus.data.local.entity.Pedido
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PedidoDao {

    // ---------- Escritura ----------
    @Insert
    abstract suspend fun insertarPedido(p: Pedido): Long

    @Insert
    abstract suspend fun insertarDetalles(lista: List<DetallePedido>)

    /** Inserta el pedido, asigna su id a cada detalle e inserta los detalles (atomico). */
    @Transaction
    open suspend fun crearPedido(pedido: Pedido, detalles: List<DetallePedido>): Long {
        val id = insertarPedido(pedido)
        insertarDetalles(detalles.map { it.copy(pedidoId = id.toInt()) })
        return id
    }

    // ---------- Lectura ----------
    @Transaction
    @Query("SELECT * FROM pedidos WHERE clienteId = :clienteId ORDER BY fecha DESC")
    abstract fun observarPorCliente(clienteId: Int): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id")
    abstract fun observarUno(id: Int): Flow<PedidoConDetalle?>

}
