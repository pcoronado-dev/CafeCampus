package com.coronado.cafecampus.data.local

import com.coronado.cafecampus.data.local.entity.Producto
import com.coronado.cafecampus.data.local.entity.Usuario
import com.coronado.cafecampus.model.Rol

/** Datos ficticios. Se cargan solo la primera vez. */
object DatosIniciales {
    suspend fun sembrar(db: CafeDatabase) {
        if (db.usuarioDao().contar() != 0) return

        val productos = listOf(
            Producto(nombre = "Café americano", descripcion = "Café negro recién filtrado, 8 oz.", categoria = "Bebidas", precio = 4.00, icono = "cafe", imagen = "prod_cafe_americano"),
            Producto(nombre = "Sándwich de pollo", descripcion = "Pan ciabatta, pollo deshilachado, lechuga y mayonesa.", categoria = "Desayunos", precio = 9.00, icono = "pan", imagen = "prod_sandwich_pollo"),
            Producto(nombre = "Menú del día", descripcion = "Entrada, segundo y refresco. Cambia todos los días.", categoria = "Almuerzos", precio = 12.00, icono = "plato", imagen = "prod_menu_del_dia"),
            Producto(nombre = "Jugo de naranja", descripcion = "Jugo natural exprimido al momento, 12 oz.", categoria = "Bebidas", precio = 5.00, icono = "jugo", imagen = "prod_jugo_naranja"),
            Producto(nombre = "Empanada", descripcion = "Empanada de carne horneada.", categoria = "Desayunos", precio = 4.50, icono = "dulce", imagen = "prod_empanada"),
            Producto(nombre = "Chicha morada", descripcion = "Refresco tradicional de maíz morado, 500 ml.", categoria = "Bebidas", precio = 3.50, icono = "botella", imagen = "prod_chicha_morada")
        )
        productos.forEach { db.productoDao().insertar(it) }

        db.usuarioDao().insertar(
            Usuario(nombre = "Piero", correo = "piero@campus.test", clave = "1234", rol = Rol.CLIENTE.name)
        )
        db.usuarioDao().insertar(
            Usuario(nombre = "Admin Cafetería", correo = "admin@cafe.test", clave = "admin", rol = Rol.ADMIN.name)
        )
    }
}