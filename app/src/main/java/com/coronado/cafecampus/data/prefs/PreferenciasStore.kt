package com.coronado.cafecampus.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coronado.cafecampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Declarada una sola vez, a nivel de archivo
val Context.dataStore by preferencesDataStore(name = "ajustes")

class PreferenciasStore(private val context: Context) {

    private object Claves {
        val PERFIL = stringPreferencesKey("perfilActivo")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val RECIBIR_PEDIDOS = booleanPreferencesKey("recibirPedidos")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
    }

    // ---------- Lectura ----------
    val perfilActivo: Flow<String> = context.dataStore.data.map { it[Claves.PERFIL] ?: "" }
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { it[Claves.MODO_OSCURO] ?: false }
    val recibirPedidos: Flow<Boolean> = context.dataStore.data.map { it[Claves.RECIBIR_PEDIDOS] ?: true }

    val notificaciones: Flow<Boolean> = context.dataStore.data.map { it[Claves.NOTIFICACIONES] ?: true }

    // ---------- Escritura ----------
    suspend fun guardarPerfil(rol: Rol) {
        context.dataStore.edit { it[Claves.PERFIL] = rol.name }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { it[Claves.MODO_OSCURO] = activo }
    }

    suspend fun guardarRecibirPedidos(activo: Boolean) {
        context.dataStore.edit { it[Claves.RECIBIR_PEDIDOS] = activo }
    }

    suspend fun guardarNotificaciones(activo: Boolean) {
        context.dataStore.edit { it[Claves.NOTIFICACIONES] = activo }
    }

    /** Foto de perfil (ruta del archivo) por rol. */
    fun fotoPerfil(rol: Rol): Flow<String> =
        context.dataStore.data.map { it[stringPreferencesKey("foto_${rol.name}")] ?: "" }

    suspend fun guardarFotoPerfil(rol: Rol, ruta: String) {
        context.dataStore.edit { it[stringPreferencesKey("foto_${rol.name}")] = ruta }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { it[Claves.PERFIL] = "" }
    }
}
