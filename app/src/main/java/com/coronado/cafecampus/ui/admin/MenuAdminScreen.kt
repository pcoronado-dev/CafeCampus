@file:OptIn(ExperimentalMaterial3Api::class)

package com.coronado.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coronado.cafecampus.data.local.entity.Producto
import com.coronado.cafecampus.model.EstadoPedido
import com.coronado.cafecampus.ui.components.AppTopBar
import com.coronado.cafecampus.ui.components.CafeChip
import com.coronado.cafecampus.ui.components.ChipEtiqueta
import com.coronado.cafecampus.ui.components.ProductoMiniatura
import com.coronado.cafecampus.ui.theme.coloresDe
import com.coronado.cafecampus.ui.theme.esOscuro
import com.coronado.cafecampus.ui.util.soles
import com.coronado.cafecampus.viewmodel.MenuAdminViewModel

@Composable
fun MenuAdminScreen(vm: MenuAdminViewModel, onNuevo: () -> Unit, onEditar: (Int) -> Unit) {
    val productos by vm.productosVisibles.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val categoria by vm.categoria.collectAsState()
    var porEliminar by remember { mutableStateOf<Producto?>(null) }

    Scaffold(
        topBar = { AppTopBar("Gestión del menú") },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar producto", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { CafeChip("Todo", categoria == null, { vm.elegirCategoria(null) }) }
                items(categorias) { c -> CafeChip(c, categoria == c, { vm.elegirCategoria(c) }) }
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos, key = { it.id }) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProductoMiniatura(p, 52.dp)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(p.nombre, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${p.categoria} · ${p.precio.soles()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                // Tocar el chip alterna Disponible / Agotado
                                val c = coloresDe(
                                    if (p.disponible) EstadoPedido.LISTO else EstadoPedido.CANCELADO,
                                    esOscuro()
                                )
                                ChipEtiqueta(
                                    if (p.disponible) "Disponible" else "Agotado",
                                    c.fondo, c.texto,
                                    Modifier.clickable { vm.alternarDisponibilidad(p) }
                                )
                            }
                            IconButton(onClick = { onEditar(p.id) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { porEliminar = p }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    porEliminar?.let { p ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar \"${p.nombre}\"? Los pedidos antiguos no se verán afectados.") },
            confirmButton = {
                TextButton(onClick = { vm.eliminar(p); porEliminar = null }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
