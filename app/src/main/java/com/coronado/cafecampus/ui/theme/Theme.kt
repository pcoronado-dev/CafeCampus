package com.coronado.cafecampus.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.coronado.cafecampus.model.Rol

/** Entrega el ColorScheme segun el perfil y el modo oscuro. */
@Composable
fun CafeTheme(rol: Rol, darkTheme: Boolean, content: @Composable () -> Unit) {
    val scheme = when {
        rol == Rol.ADMIN && darkTheme -> AdminDark
        rol == Rol.ADMIN -> AdminLight
        darkTheme -> ClienteDark
        else -> ClienteLight
    }
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = scheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }
    MaterialTheme(colorScheme = scheme, typography = AppTypography, content = content)
}