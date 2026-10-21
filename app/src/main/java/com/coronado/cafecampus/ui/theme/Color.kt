package com.coronado.cafecampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.coronado.cafecampus.model.EstadoPedido

// ---------- Cliente: naranja ----------
val ClienteLight = lightColorScheme(
    primary = Color(0xFFEA580C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF7C2D12),
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF7C2D12),
    background = Color(0xFFFFFBF7),
    onBackground = Color(0xFF1F2937),
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF5EDE6),
    onSurfaceVariant = Color(0xFF6B7280),
    surfaceContainer = Color.White,
    outline = Color(0xFFD1C7BE),
    outlineVariant = Color(0xFFE7DDD3),
    error = Color(0xFFDC2626)
)

val ClienteDark = darkColorScheme(
    primary = Color(0xFFFB923C),
    onPrimary = Color(0xFF431407),
    primaryContainer = Color(0xFF7C2D12),
    onPrimaryContainer = Color(0xFFFFEDD5),
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    background = Color(0xFF1C1917),
    onBackground = Color(0xFFF5F5F4),
    surface = Color(0xFF292524),
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF3A3532),
    onSurfaceVariant = Color(0xFFA8A29E),
    surfaceContainer = Color(0xFF292524),
    outline = Color(0xFF57534E),
    outlineVariant = Color(0xFF44403C),
    error = Color(0xFFF87171)
)

// ---------- Administrador: indigo ----------
val AdminLight = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF312E81),
    background = Color(0xFFF7F7FD),
    onBackground = Color(0xFF1F2937),
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFEDEDF7),
    onSurfaceVariant = Color(0xFF6B7280),
    surfaceContainer = Color.White,
    outline = Color(0xFFC7C7DA),
    outlineVariant = Color(0xFFE0E0EE),
    error = Color(0xFFDC2626)
)

val AdminDark = darkColorScheme(
    primary = Color(0xFFA5B4FC),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondaryContainer = Color(0xFF3730A3),
    onSecondaryContainer = Color(0xFFE0E7FF),
    background = Color(0xFF0F0F1A),
    onBackground = Color(0xFFF1F1F9),
    surface = Color(0xFF1B1B2E),
    onSurface = Color(0xFFF1F1F9),
    surfaceVariant = Color(0xFF2A2A42),
    onSurfaceVariant = Color(0xFF9CA3AF),
    surfaceContainer = Color(0xFF1B1B2E),
    outline = Color(0xFF4B4B6B),
    outlineVariant = Color(0xFF34344F),
    error = Color(0xFFF87171)
)

// ---------- Estados (comunes a ambos perfiles) ----------
data class ColoresEstado(val fondo: Color, val texto: Color)

fun coloresDe(estado: EstadoPedido, oscuro: Boolean): ColoresEstado {
    val (claroFondo, claroTexto, oscuroTexto) = when (estado) {
        EstadoPedido.RECIBIDO -> Triple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), Color(0xFF93C5FD))
        EstadoPedido.PREPARANDO -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Color(0xFFFCD34D))
        EstadoPedido.LISTO -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), Color(0xFF86EFAC))
        EstadoPedido.ENTREGADO -> Triple(Color(0xFFE5E7EB), Color(0xFF4B5563), Color(0xFFD1D5DB))
        EstadoPedido.CANCELADO -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), Color(0xFFFCA5A5))
    }
    return if (oscuro) ColoresEstado(oscuroTexto.copy(alpha = 0.18f), oscuroTexto)
    else ColoresEstado(claroFondo, claroTexto)
}

/** Verdadero si el tema actual (elegido por el usuario) es oscuro. */
@Composable
fun esOscuro(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
