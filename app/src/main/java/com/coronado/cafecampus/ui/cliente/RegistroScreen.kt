@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.model.Rol
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.CafeTextField
import com.coronado.cafecampus.ui.theme.CafeTheme
import com.coronado.cafecampus.viewmodel.SesionViewModel

@Composable
fun RegistroScreen(
    vm: SesionViewModel,
    onBack: () -> Unit,
    onExito: (Rol) -> Unit
) {
    val oscuro by vm.modoOscuro.collectAsState()
    val errorRegistro by vm.errorRegistro.collectAsState()

    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    var verClave by remember { mutableStateOf(false) }

    CafeTheme(rol = Rol.CLIENTE, darkTheme = oscuro) {
        Scaffold(topBar = { AppTopBar("Crear cuenta", onBack = onBack) }) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        "Regístrate como estudiante",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Crea tu cuenta para realizar pedidos en CaféCampus",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(20.dp))

                    CafeTextField(
                        valor = nombre,
                        onCambio = { nombre = it; vm.limpiarErrorRegistro() },
                        etiqueta = "Nombre completo",
                        leading = { Icon(Icons.Filled.Person, null) }
                    )
                    Spacer(Modifier.height(12.dp))

                    CafeTextField(
                        valor = correo,
                        onCambio = { correo = it; vm.limpiarErrorRegistro() },
                        etiqueta = "Correo electrónico",
                        teclado = KeyboardType.Email,
                        leading = { Icon(Icons.Filled.Email, null) }
                    )
                    Spacer(Modifier.height(12.dp))

                    CafeTextField(
                        valor = clave,
                        onCambio = { clave = it; vm.limpiarErrorRegistro() },
                        etiqueta = "Contraseña",
                        teclado = KeyboardType.Password,
                        ocultar = !verClave,
                        leading = { Icon(Icons.Filled.Lock, null) },
                        trailing = {
                            IconButton(onClick = { verClave = !verClave }) {
                                Icon(
                                    if (verClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Mostrar u ocultar contraseña"
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))

                    CafeTextField(
                        valor = confirmacion,
                        onCambio = { confirmacion = it; vm.limpiarErrorRegistro() },
                        etiqueta = "Confirmar contraseña",
                        teclado = KeyboardType.Password,
                        ocultar = !verClave,
                        leading = { Icon(Icons.Filled.Lock, null) }
                    )
                    Spacer(Modifier.height(16.dp))

                    if (errorRegistro != null) {
                        Text(
                            errorRegistro ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    CafeButton(
                        texto = "Crear cuenta",
                        onClick = {
                            vm.registrar(nombre, correo, clave, confirmacion, onExito)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
