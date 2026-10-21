package com.coronado.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.coronado.cafecampus.ui.theme.esOscuro
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val ICONOS_PRODUCTO = listOf("cafe", "plato", "jugo", "pan", "botella", "dulce")

fun vectorDeIcono(icono: String): ImageVector = when (icono) {
    "cafe" -> Icons.Filled.LocalCafe
    "plato" -> Icons.Filled.Restaurant
    "jugo" -> Icons.Filled.LocalDrink
    "pan" -> Icons.Filled.Fastfood
    "botella" -> Icons.Filled.LocalBar
    "dulce" -> Icons.Filled.Cake
    else -> Icons.Filled.Restaurant
}

/** Fondo suave según el producto (como las fotos del mockup). */
@Composable
fun fondoProducto(icono: String): Color {
    if (esOscuro()) return MaterialTheme.colorScheme.primaryContainer
    return when (icono) {
        "cafe" -> Color(0xFFEBDDCC)
        "plato" -> Color(0xFFFDEBD3)
        "pan" -> Color(0xFFFEF3C7)
        "jugo" -> Color(0xFFFFE5CF)
        "botella" -> Color(0xFFF8DDE3)
        else -> Color(0xFFFFEDD5)
    }
}

@Composable
fun ProductoIcono(icono: String, modifier: Modifier = Modifier, tamano: Dp = 56.dp) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(RoundedCornerShape(12.dp))
            .background(fondoProducto(icono)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = vectorDeIcono(icono),
            contentDescription = icono,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(tamano * 0.5f)
        )
    }
}