package com.coronado.cafecampus.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Double.soles(): String = String.format(Locale.US, "S/ %.2f", this)

/** #0046 */
fun Int.codigo(): String = String.format(Locale.US, "#%04d", this)

fun Long.fechaCorta(): String = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(this))

/** "Hoy 10:30" o "12/10 10:30" */
fun Long.fechaRelativa(): String {
    val ms = this
    val hoy = Calendar.getInstance()
    val c = Calendar.getInstance().apply { timeInMillis = ms }
    val mismoDia = hoy.get(Calendar.YEAR) == c.get(Calendar.YEAR) &&
            hoy.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)
    val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
    return if (mismoDia) "Hoy $hora"
    else SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(ms)) + " " + hora
}

fun String.pagoEtiqueta(): String = if (this == "YAPE") "Yape / Plin" else "Pagar en caja"

/** "Ana Torres" -> "Ana T." */
fun String.abreviado(): String {
    val partes = trim().split(" ").filter { it.isNotEmpty() }
    return if (partes.size >= 2) "${partes[0]} ${partes[1].first()}." else this
}