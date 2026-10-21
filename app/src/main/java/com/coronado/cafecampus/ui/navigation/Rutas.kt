package com.coronado.cafecampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    // Grafos
    const val GRAFO_CLIENTE = "grafo_cliente"
    const val GRAFO_ADMIN = "grafo_admin"

    // Cliente
    const val MENU = "menu"
    const val PEDIDOS_CLIENTE = "pedidos_cliente"
    const val PERFIL = "perfil"
    const val DETALLE_PRODUCTO = "producto/{id}"
    const val CONFIRMAR = "confirmar"
    const val SEGUIMIENTO = "seguimiento/{id}"

    // Admin
    const val PANEL = "panel"
    const val PEDIDOS_ADMIN = "pedidos_admin"
    const val DETALLE_PEDIDO_ADMIN = "pedido_admin/{id}"
    const val MENU_ADMIN = "menu_admin"
    const val FORMULARIO = "formulario?id={id}"
    const val AJUSTES = "ajustes"

    fun detalleProducto(id: Int) = "producto/$id"
    fun seguimiento(id: Int) = "seguimiento/$id"
    fun detallePedidoAdmin(id: Int) = "pedido_admin/$id"
    fun formulario(id: Int? = null) = if (id == null) "formulario" else "formulario?id=$id"
}
