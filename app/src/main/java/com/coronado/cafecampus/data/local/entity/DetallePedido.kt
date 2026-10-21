package com.coronado.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cada linea guarda una "foto" del producto (nombre y precio) en el momento
 * de la compra, asi los pedidos antiguos no cambian si el producto cambia.
 */
@Entity(
    tableName = "detalle_pedido",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pedidoId")]
)
data class DetallePedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pedidoId: Int,
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
)