package com.coronado.cafecampus.ui.util

import android.content.Context
import android.net.Uri
import java.io.File

/** Copia una foto elegida de la galería al almacenamiento interno de la app. */
object ImagenesStore {
    fun copiar(context: Context, uri: Uri, prefijo: String): String? = try {
        val carpeta = File(context.filesDir, "imagenes").apply { mkdirs() }
        val destino = File(carpeta, "${prefijo}_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        if (destino.exists() && destino.length() > 0) destino.absolutePath else null
    } catch (e: Exception) {
        null
    }
}