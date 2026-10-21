package com.coronado.cafecampus.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.ui.theme.coloresDe
import com.coronado.cafecampus.ui.theme.esOscuro

@Composable
fun ChipEtiqueta(texto: String, fondo: Color, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(50), color = fondo) {
        Text(
            texto,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EstadoChip(estado: EstadoPedido, modifier: Modifier = Modifier) {
    val c = coloresDe(estado, esOscuro())
    ChipEtiqueta(estado.etiqueta, c.fondo, c.texto, modifier)
}

/** Chip Disponible (verde) / Agotado (rojo). */
@Composable
fun DisponibleChip(disponible: Boolean, modifier: Modifier = Modifier) {
    val c = coloresDe(if (disponible) EstadoPedido.LISTO else EstadoPedido.CANCELADO, esOscuro())
    ChipEtiqueta(if (disponible) "Disponible" else "Agotado", c.fondo, c.texto, modifier)
}
