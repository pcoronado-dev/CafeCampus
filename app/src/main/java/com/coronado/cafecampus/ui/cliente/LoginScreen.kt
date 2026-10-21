package com.coronado.cafecampus.ui.cliente

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.model.Rol
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.CafeTextField
import com.coronado.cafecampus.ui.components.SelectorSegmentado
import com.coronado.cafecampus.ui.theme.CafeTheme
import com.coronado.cafecampus.viewmodel.SesionViewModel

/** Un solo Composable para ambos roles: el selector cambia los colores del tema. */
@Composable
fun LoginScreen(
    vm: SesionViewModel,
    onRegistro: () -> Unit,
    onExito: (Rol) -> Unit
) {
    val oscuro by vm.modoOscuro.collectAsState()
    val error by vm.error.collectAsState()
    val contexto = LocalContext.current

    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var verClave by remember { mutableStateOf(false) }
    val rol = Rol.values()[pestana]

    fun proximamente() = Toast.makeText(contexto, "Disponible próximamente", Toast.LENGTH_SHORT).show()

    CafeTheme(rol = rol, darkTheme = oscuro) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Text("Bienvenido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Ingresa para continuar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))

                SelectorSegmentado(
                    opciones = Rol.values().map { it.etiqueta },
                    seleccion = pestana,
                    onSeleccion = { pestana = it; vm.limpiarError() }
                )
                Spacer(Modifier.height(20.dp))

                CafeTextField(
                    valor = correo,
                    onCambio = { correo = it; vm.limpiarError() },
                    etiqueta = "Correo",
                    teclado = KeyboardType.Email,
                    leading = { Icon(Icons.Filled.Email, null) }
                )
                Spacer(Modifier.height(12.dp))
                CafeTextField(
                    valor = clave,
                    onCambio = { clave = it; vm.limpiarError() },
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
                TextButton(onClick = { proximamente() }, modifier = Modifier.align(Alignment.End)) {
                    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.bodySmall)
                }
                if (error != null) {
                    Text(
                        error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
                CafeButton(
                    texto = "Ingresar",
                    onClick = { vm.login(correo, clave, rol, onExito) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                if (rol == Rol.CLIENTE) {
                    TextButton(onClick = onRegistro, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("¿No tienes cuenta? Regístrate")
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Modo práctica", fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Demo → juan@campus.test / 1234  ·  admin@cafe.test / admin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
