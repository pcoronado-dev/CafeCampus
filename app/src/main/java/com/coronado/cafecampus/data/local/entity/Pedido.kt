package com.coronado.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"]
        )
    ],
    indices = [Index("clienteId")]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteId: Int,
    val horaRecojo: String,
    val pago: String, // CAJA | YAPE
    val nota: String = "",
    val total: Double,
    val fecha: Long = System.currentTimeMillis()
)