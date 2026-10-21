@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.data.local.entity.Producto
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeButton
import com.coronado.cafecampus.ui.components.CafeTextField
import com.coronado.cafecampus.ui.components.ICONOS_PRODUCTO
import com.coronado.cafecampus.ui.components.ProductoIcono
import com.coronado.cafecampus.ui.components.ProductoImagen
import com.coronado.cafecampus.ui.util.ImagenesStore
import com.coronado.cafecampus.viewmodel.MenuAdminViewModel

/** Sirve para crear (productoId = null) y para editar (id existente). */
@Composable
fun FormularioProductoScreen(productoId: Int?, vm: MenuAdminViewModel, onBack: () -> Unit) {
    val contexto = LocalContext.current
    var nombre by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var disponible by rememberSaveable { mutableStateOf(true) }
    var icono by rememberSaveable { mutableStateOf(ICONOS_PRODUCTO.first()) }
    var imagen by rememberSaveable { mutableStateOf("") }
    var cargado by rememberSaveable { mutableStateOf(false) }
    var intento by rememberSaveable { mutableStateOf(false) }

    // Selector de fotos del sistema (no necesita permisos)
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) ImagenesStore.copiar(contexto, uri, "producto")?.let { imagen = it }
    }

    LaunchedEffect(productoId) {
        if (productoId != null && !cargado) {
            vm.obtener(productoId)?.let {
                nombre = it.nombre
                categoria = it.categoria
                precio = it.precio.toString()
                descripcion = it.descripcion
                disponible = it.disponible
                icono = it.icono
                imagen = it.imagen
                cargado = true
            }
        }
    }

    val precioNum = precio.replace(',', '.').toDoubleOrNull()
    val errorNombre = if (intento && nombre.isBlank()) "El nombre es obligatorio" else null
    val errorPrecio = if (intento && (precioNum == null || precioNum <= 0.0)) "Ingresa un precio mayor que 0" else null

    Scaffold(
        topBar = { AppTopBar(if (productoId == null) "Nuevo producto" else "Editar producto", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta de imagen: al tocarla se elige una foto de la galería
            Card(
                onClick = {
                    selectorFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ProductoImagen(
                        imagen = imagen,
                        icono = icono,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        iconoTamano = 36.dp
                    )
                    Column {
                        Text("Imagen del producto", fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                        Text(
                            "Toca para elegir una foto",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }
            if (imagen.isNotBlank()) {
                TextButton(onClick = { imagen = "" }) { Text("Quitar foto") }
            }

            CafeTextField(nombre, { nombre = it }, "Nombre del producto", error = errorNombre)
            CafeTextField(categoria, { categoria = it }, "Categoría (Desayunos, Almuerzos, Bebidas…)")
            CafeTextField(
                precio, { precio = it }, "Precio (S/)",
                teclado = KeyboardType.Decimal, error = errorPrecio
            )
            CafeTextField(
                descripcion, { descripcion = it }, "Descripción",
                singleLine = false, minLines = 3
            )

            Text("Ícono (se usa si no hay foto)", fontWeight = FontWeight.SemiBold)
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ICONOS_PRODUCTO.forEach { i ->
                    ProductoIcono(
                        icono = i,
                        tamano = 48.dp,
                        modifier = Modifier
                            .clickable { icono = i }
                            .then(
                                if (i == icono) Modifier.border(
                                    2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)
                                ) else Modifier
                            )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Disponible en el menú", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Los estudiantes podrán pedirlo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = disponible, onCheckedChange = { disponible = it })
            }

            CafeButton(
                texto = "Guardar producto",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    intento = true
                    if (nombre.isNotBlank() && precioNum != null && precioNum > 0.0) {
                        vm.guardar(
                            Producto(
                                id = productoId ?: 0,
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                categoria = categoria.trim().ifEmpty { "General" },
                                precio = precioNum,
                                disponible = disponible,
                                icono = icono,
                                imagen = imagen
                            )
                        )
                        onBack()
                    }
                }
            )
            CafeButton("Cancelar", onBack, Modifier.fillMaxWidth(), outlined = true)
        }
    }
}