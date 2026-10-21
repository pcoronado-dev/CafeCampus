@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.DisponibleChip
import com.coronado.cafecampus.ui.components.ProductoImagen
import com.coronado.cafecampus.ui.components.vectorDeIcono
import com.coronado.cafecampus.ui.util.soles
import com.coronado.cafecampus.viewmodel.MenuViewModel

@Composable
fun DetalleProductoScreen(productoId: Int, vm: MenuViewModel, onBack: () -> Unit) {
    val productos by vm.productos.collectAsState()
    val producto = productos.firstOrNull { it.id == productoId }

    var cantidad by rememberSaveable { mutableIntStateOf(1) }
    var nota by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar("Detalle del producto", onBack = onBack) },
        bottomBar = {
            if (producto != null) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(
                        Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Caja "Total"
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text(
                                    "Total",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    (producto.precio * cantidad).soles(),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        CafeButton(
                            texto = "Agregar al pedido",
                            onClick = {
                                vm.agregar(producto, cantidad, nota)
                                onBack()
                            },
                            enabled = producto.disponible,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (producto == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Imagen
                ProductoImagen(
                    imagen = producto.imagen,
                    icono = producto.icono,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.75f)
                        .clip(RoundedCornerShape(16.dp)),
                    iconoTamano = 80.dp
                )

                // Nombre y precio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        producto.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        producto.precio.soles(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Disponible + tiempo
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DisponibleChip(producto.disponible)
                    Icon(
                        Icons.Filled.Schedule, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        "10 – 15 min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Cantidad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BotonCirculo(Icons.Filled.Remove, "Menos", relleno = false, activo = cantidad > 1) {
                                if (cantidad > 1) cantidad--
                            }
                            Text(
                                "$cantidad",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.widthIn(min = 20.dp),
                                textAlign = TextAlign.Center
                            )
                            BotonCirculo(Icons.Filled.Add, "Más", relleno = true, activo = true) { cantidad++ }
                        }
                    }
                }

                // Nota para la cocina
                Text("Nota para la cocina", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("Ej: sin ají, por favor") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

/** Botón circular del selector de cantidad: relleno = naranja (+), suave = claro (−). */
@Composable
private fun BotonCirculo(
    icono: ImageVector,
    descripcion: String,
    relleno: Boolean,
    activo: Boolean,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .size(32.dp)
            .alpha(if (activo) 1f else 0.4f)
            .clip(CircleShape)
            .background(
                if (relleno) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primaryContainer
            )
            .clickable(enabled = activo) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono, contentDescription = descripcion,
            tint = if (relleno) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
    }
}
