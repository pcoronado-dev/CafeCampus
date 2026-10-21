package com.coronado.cafecampus.data.local.relation

/** Resultado de la consulta del grafico de pedidos por hora (Fase 2). */
data class PedidoPorHora(
    val hora: Int,
    val cantidad: Int
)