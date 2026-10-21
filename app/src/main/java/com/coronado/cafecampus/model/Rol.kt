package com.coronado.cafecampus.model

enum class Rol(val etiqueta: String) {
    CLIENTE("Estudiante"),
    ADMIN("Administrador");

    companion object {
        fun desde(valor: String?): Rol? = values().firstOrNull { it.name == valor }
    }
}