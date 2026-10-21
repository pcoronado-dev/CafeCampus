package com.coronado.cafecampus.model

enum class EstadoPedido(val etiqueta: String) {
    RECIBIDO("Recibido"),
    PREPARANDO("Preparando"),
    LISTO("Listo"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    /** Siguiente estado del flujo normal (null si ya no avanza). */
    fun siguiente(): EstadoPedido? = when (this) {
        RECIBIDO -> PREPARANDO
        PREPARANDO -> LISTO
        LISTO -> ENTREGADO
        else -> null
    }

    /** Activo = RECIBIDO, PREPARANDO o LISTO. */
    val activo: Boolean
        get() = this == RECIBIDO || this == PREPARANDO || this == LISTO

    companion object {
        fun desde(valor: String): EstadoPedido =
            values().firstOrNull { it.name == valor } ?: RECIBIDO
    }
}