package com.coronado.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.coronado.cafecampus.data.local.entity.DetallePedido
import com.coronado.cafecampus.data.local.entity.Pedido
import com.coronado.cafecampus.data.local.relation.PedidoConDetalle
import com.coronado.cafecampus.data.local.relation.PedidoPorHora
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PedidoDao {

    // ---------- Escritura ----------
    @Insert
    abstract suspend fun insertarPedido(p: Pedido): Long

    @Insert
    abstract suspend fun insertarDetalles(lista: List<DetallePedido>)

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    abstract suspend fun cambiarEstado(id: Int, estado: String)

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
    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY fecha DESC")
    abstract fun observarPorEstado(estado: String): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id")
    abstract fun observarUno(id: Int): Flow<PedidoConDetalle?>

    // ---------- Panel ----------
    @Query("SELECT COALESCE(SUM(total), 0.0) FROM pedidos WHERE fecha >= :inicio AND estado != 'CANCELADO'")
    abstract fun ventasDesde(inicio: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM pedidos WHERE fecha >= :inicio AND estado != 'CANCELADO'")
    abstract fun contarDesde(inicio: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = :estado")
    abstract fun cantidadPorEstado(estado: String): Flow<Int>

    // ---------- Fase 2: grafico real por hora ----------
    @Query(
        "SELECT CAST(strftime('%H', fecha / 1000, 'unixepoch', 'localtime') AS INTEGER) AS hora, " +
                "COUNT(*) AS cantidad FROM pedidos " +
                "WHERE fecha >= :inicio AND estado != 'CANCELADO' GROUP BY hora ORDER BY hora"
    )
    abstract fun pedidosPorHora(inicio: Long): Flow<List<PedidoPorHora>>
}

