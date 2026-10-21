package com.coronado.cafecampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Boton de 10 dp de radio. outlined = secundario; peligro = rojo (cancelar/eliminar). */
@Composable
fun CafeButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    outlined: Boolean = false,
    peligro: Boolean = false
) {
    val forma = RoundedCornerShape(10.dp)
    if (outlined) {
        val color = if (peligro) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.heightIn(min = 48.dp),
            enabled = enabled,
            shape = forma,
            border = BorderStroke(1.dp, if (enabled) color else MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
        ) {
            Text(texto, fontWeight = FontWeight.SemiBold)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(min = 48.dp),
            enabled = enabled,
            shape = forma
        ) {
            Text(texto, fontWeight = FontWeight.SemiBold)
        }
    }
}