package com.coronado.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.coronado.cafecampus.data.local.entity.Producto
import java.io.File

/**
 * Foto del producto. Orden de búsqueda:
 *  1. archivo de la galería (imagen = "/ruta/...")
 *  2. foto incluida en res/drawable-nodpi (imagen = "prod_xxx")
 *  3. si no hay foto: ícono sobre fondo suave
 */
@Composable
fun ProductoImagen(
    imagen: String,
    icono: String,
    modifier: Modifier = Modifier,
    iconoTamano: Dp = 40.dp
) {
    val contexto = LocalContext.current
    val modelo: Any? = remember(imagen) {
        when {
            imagen.isBlank() -> null
            imagen.startsWith("/") -> File(imagen).takeIf { it.exists() }
            else -> contexto.resources
                .getIdentifier(imagen, "drawable", contexto.packageName)
                .takeIf { it != 0 }
        }
    }
    Box(
        modifier = modifier.background(fondoProducto(icono)),
        contentAlignment = Alignment.Center
    ) {
        if (modelo != null) {
            AsyncImage(
                model = modelo,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                vectorDeIcono(icono), contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(iconoTamano)
            )
        }
    }
}

@Composable
fun ProductoMiniatura(p: Producto, tamano: Dp, modifier: Modifier = Modifier) {
    ProductoImagen(
        imagen = p.imagen,
        icono = p.icono,
        modifier = modifier
            .size(tamano)
            .clip(RoundedCornerShape(12.dp)),
        iconoTamano = tamano * 0.5f
    )
}
