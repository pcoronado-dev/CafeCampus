package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronado.cafecampus.data.local.entity.Usuario
import com.coronado.cafecampus.data.prefs.PreferenciasStore
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val recibirPedidos: StateFlow<Boolean> = prefs.recibirPedidos
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setNotificaciones(activo: Boolean) { viewModelScope.launch { prefs.guardarNotificaciones(activo) } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val fotoPerfil: StateFlow<String> = _usuarioActual
        .flatMapLatest { u ->
            val rol = Rol.desde(u?.rol)
            if (rol == null) flowOf("") else prefs.fotoPerfil(rol)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun guardarFoto(ruta: String) {
        val rol = Rol.desde(_usuarioActual.value?.rol) ?: return
        viewModelScope.launch { prefs.guardarFotoPerfil(rol, ruta) }
    }

    fun limpiarError() { _error.value = null }

    /** Splash: si ya hay perfil guardado, carga su usuario y devuelve el rol (o null). */
    suspend fun restaurarSesion(): Rol? {
        val rol = Rol.desde(prefs.perfilActivo.first()) ?: return null
        val usuario = repo.usuarioPorRol(rol) ?: return null
        _usuarioActual.value = usuario
        return rol
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol, onExito: (Rol) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa tu correo y contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo.trim(), clave)
            when {
                usuario == null -> _error.value = "Correo o contraseña incorrectos"
                usuario.rol != rolSeleccionado.name ->
                    _error.value = "Esta cuenta no es de ${rolSeleccionado.etiqueta}. Cambia de pestaña."
                else -> {
                    prefs.guardarPerfil(rolSeleccionado)
                    _usuarioActual.value = usuario
                    _error.value = null
                    onExito(rolSeleccionado)
                }
            }
        }
    }

    /** Modo practica: guarda el otro perfil y entrega su rol para navegar a su grafo. */
    fun cambiarPerfil(onListo: (Rol) -> Unit) {
        viewModelScope.launch {
            val actual = Rol.desde(_usuarioActual.value?.rol) ?: Rol.CLIENTE
            val otro = if (actual == Rol.CLIENTE) Rol.ADMIN else Rol.CLIENTE
            val usuario = repo.usuarioPorRol(otro) ?: return@launch
            prefs.guardarPerfil(otro)
            _usuarioActual.value = usuario
            onListo(otro)
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
            _error.value = null
            onListo()
        }
    }

    fun setModoOscuro(activo: Boolean) { viewModelScope.launch { prefs.guardarModoOscuro(activo) } }

    fun setRecibirPedidos(activo: Boolean) { viewModelScope.launch { prefs.guardarRecibirPedidos(activo) } }
}
