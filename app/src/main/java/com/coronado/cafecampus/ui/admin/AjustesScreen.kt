@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.AvatarPerfil
import com.coronado.cafecampus.ui.util.ImagenesStore
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.ChipEtiqueta
import com.coronado.cafecampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(vm: SesionViewModel, onCambiarPerfil: () -> Unit, onCerrarSesion: () -> Unit) {
    AjustesContenido(
        titulo = "Ajustes",
        vm = vm,
        rolActivo = "Administrador",
        destinoCambio = "Estudiante",
        mostrarRecibirPedidos = true,
        onCambiarPerfil = onCambiarPerfil,
        onCerrarSesion = onCerrarSesion
    )
}

/** Contenido compartido por Ajustes (admin) y Perfil (estudiante). */
@Composable
fun AjustesContenido(
    titulo: String,
    vm: SesionViewModel,
    rolActivo: String,
    destinoCambio: String,
    mostrarRecibirPedidos: Boolean,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario by vm.usuarioActual.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()
    val notificaciones by vm.notificaciones.collectAsState()
    val recibir by vm.recibirPedidos.collectAsState()
    val foto by vm.fotoPerfil.collectAsState()
    val contexto = LocalContext.current
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) ImagenesStore.copiar(contexto, uri, "perfil")?.let { vm.guardarFoto(it) }
    }

    Scaffold(topBar = { AppTopBar(titulo) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Tarjeta de perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarPerfil(
                        ruta = foto,
                        tamano = 64.dp,
                        onClick = {
                            selectorFoto.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(usuario?.nombre ?: rolActivo, fontWeight = FontWeight.Bold)
                        Text(
                            usuario?.correo ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ChipEtiqueta(
                            "Perfil activo: $rolActivo",
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Cambiar de perfil (modo practica)
            Surface(
                onClick = onCambiarPerfil,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Filled.SwapHoriz, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Column(Modifier.weight(1f)) {
                        Text("Cambiar de perfil", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        Text(
                            "Modo práctica · ir a la vista $destinoCambio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Text("Preferencias", fontWeight = FontWeight.Bold)
            FilaPreferencia("Modo oscuro", "Cambia el tema de la app", oscuro, vm::setModoOscuro)
            FilaPreferencia("Notificaciones", "Avisar cuando llegue un pedido", notificaciones, vm::setNotificaciones)
            if (mostrarRecibirPedidos) {
                FilaPreferencia("Recibir pedidos", "Abrir o cerrar la cafetería", recibir, vm::setRecibirPedidos)
            }

            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    "Se guardan en el dispositivo con DataStore.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            CafeButton(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                outlined = true,
                peligro = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FilaPreferencia(titulo: String, subtitulo: String, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, fontWeight = FontWeight.Medium)
            Text(
                subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}
