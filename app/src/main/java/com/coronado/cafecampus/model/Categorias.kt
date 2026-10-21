package com.coronado.cafecampus.model

object Categorias {
    private val ORDEN = listOf("Desayunos", "Almuerzos", "Bebidas")

    fun ordenar(lista: List<String>): List<String> =
        lista.sortedWith(compareBy<String>({ val i = ORDEN.indexOf(it); if (i < 0) ORDEN.size else i }, { it }))
}
