package com.coronado.cafecampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.coronado.cafecampus.model.Rol
import com.coronado.cafecampus.ui.admin.AjustesScreen
import com.coronado.cafecampus.ui.admin.DetallePedidoAdminScreen
import com.coronado.cafecampus.ui.admin.FormularioProductoScreen
import com.coronado.cafecampus.ui.admin.MenuAdminScreen
import com.coronado.cafecampus.ui.admin.PanelScreen
import com.coronado.cafecampus.ui.admin.PedidosAdminScreen
import com.coronado.cafecampus.ui.cliente.ConfirmarPedidoScreen
import com.coronado.cafecampus.ui.cliente.DetalleProductoScreen
import com.coronado.cafecampus.ui.cliente.LoginScreen
import com.coronado.cafecampus.ui.cliente.MenuScreen
import com.coronado.cafecampus.ui.cliente.MisPedidosScreen
import com.coronado.cafecampus.ui.cliente.PerfilScreen
import com.coronado.cafecampus.ui.cliente.RegistroScreen
import com.coronado.cafecampus.ui.cliente.SeguimientoScreen
import com.coronado.cafecampus.ui.cliente.SplashScreen
import com.coronado.cafecampus.viewmodel.MenuAdminViewModel
import com.coronado.cafecampus.viewmodel.MenuViewModel
import com.coronado.cafecampus.viewmodel.PanelAdminViewModel
import com.coronado.cafecampus.viewmodel.PedidosAdminViewModel
import com.coronado.cafecampus.viewmodel.PedidosClienteViewModel
import com.coronado.cafecampus.viewmodel.SesionViewModel

private data class TabItem(val ruta: String, val titulo: String, val icono: ImageVector)

private val TABS_CLIENTE = listOf(
    TabItem(Rutas.MENU, "Menú", Icons.Filled.Home),
    TabItem(Rutas.PEDIDOS_CLIENTE, "Pedidos", Icons.Filled.Receipt),
    TabItem(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

private val TABS_ADMIN = listOf(
    TabItem(Rutas.PANEL, "Panel", Icons.Filled.BarChart),
    TabItem(Rutas.PEDIDOS_ADMIN, "Pedidos", Icons.Filled.Receipt),
    TabItem(Rutas.MENU_ADMIN, "Menú", Icons.Filled.LocalCafe),
    TabItem(Rutas.AJUSTES, "Ajustes", Icons.Filled.Settings)
)

@Composable
fun NavCafe(
    sesionVm: SesionViewModel,
    menuVm: MenuViewModel,
    pedidosClienteVm: PedidosClienteViewModel,
    panelVm: PanelAdminViewModel,
    pedidosAdminVm: PedidosAdminViewModel,
    menuAdminVm: MenuAdminViewModel
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    val usuario by sesionVm.usuarioActual.collectAsState()
    val cafeteriaAbierta by sesionVm.recibirPedidos.collectAsState()

    // El ViewModel del cliente necesita saber quién es el estudiante activo
    LaunchedEffect(usuario) {
        val u = usuario
        if (u != null && u.rol == Rol.CLIENTE.name) pedidosClienteVm.establecerCliente(u.id)
    }

    val esTabCliente = TABS_CLIENTE.any { it.ruta == ruta } || ruta == Rutas.SEGUIMIENTO
    val esTabAdmin = TABS_ADMIN.any { it.ruta == ruta }

    /** Navega al grafo del perfil y limpia la pila de navegación. */
    val irAlPerfil: (Rol?) -> Unit = { rol ->
        val destino = when (rol) {
            Rol.CLIENTE -> Rutas.GRAFO_CLIENTE
            Rol.ADMIN -> Rutas.GRAFO_ADMIN
            null -> Rutas.LOGIN
        }
        nav.navigate(destino) {
            popUpTo(nav.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    val irATab: (String, String) -> Unit = { grafo, destino ->
        nav.navigate(destino) {
            popUpTo(grafo) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val cambiarPerfil = { sesionVm.cambiarPerfil { rol -> irAlPerfil(rol) } }
    val cerrarSesion = { sesionVm.cerrarSesion { irAlPerfil(null) } }

    Scaffold(
        bottomBar = {
            if (esTabCliente || esTabAdmin) {
                val tabs = if (esTabCliente) TABS_CLIENTE else TABS_ADMIN
                val grafo = if (esTabCliente) Rutas.GRAFO_CLIENTE else Rutas.GRAFO_ADMIN
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = ruta == tab.ruta ||
                                    (ruta == Rutas.SEGUIMIENTO && tab.ruta == Rutas.PEDIDOS_CLIENTE),
                            onClick = { irATab(grafo, tab.ruta) },
                            icon = { Icon(tab.icono, contentDescription = tab.titulo) },
                            label = { Text(tab.titulo) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.SPLASH) {
                SplashScreen(sesionVm) { rol -> irAlPerfil(rol) }
            }
            composable(Rutas.LOGIN) {
                LoginScreen(
                    vm = sesionVm,
                    onRegistro = { nav.navigate(Rutas.REGISTRO) },
                    onExito = { rol -> irAlPerfil(rol) }
                )
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    vm = sesionVm,
                    onBack = { nav.popBackStack() },
                    onExito = { rol -> irAlPerfil(rol) }
                )
            }

            // ================= PERFIL CLIENTE =================
            navigation(startDestination = Rutas.MENU, route = Rutas.GRAFO_CLIENTE) {
                composable(Rutas.MENU) {
                    MenuScreen(
                        vm = menuVm,
                        nombre = usuario?.nombre?.substringBefore(" ") ?: "",
                        cafeteriaAbierta = cafeteriaAbierta,
                        onProducto = { nav.navigate(Rutas.detalleProducto(it)) },
                        onBolsa = { nav.navigate(Rutas.CONFIRMAR) }
                    )
                }
                composable(Rutas.PEDIDOS_CLIENTE) {
                    MisPedidosScreen(
                        vm = pedidosClienteVm,
                        onPedido = { nav.navigate(Rutas.seguimiento(it)) },
                        onRepetir = { pedido ->
                            menuVm.repetir(pedido) { agregados ->
                                if (agregados > 0) {
                                    nav.navigate(Rutas.CONFIRMAR)
                                }
                            }
                        }
                    )
                }
                composable(Rutas.PERFIL) {
                    PerfilScreen(sesionVm, onCambiarPerfil = cambiarPerfil, onCerrarSesion = cerrarSesion)
                }
                composable(
                    Rutas.DETALLE_PRODUCTO,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { e ->
                    DetalleProductoScreen(
                        productoId = e.arguments?.getInt("id") ?: 0,
                        vm = menuVm,
                        onBack = { nav.popBackStack() }
                    )
                }
                composable(Rutas.CONFIRMAR) {
                    ConfirmarPedidoScreen(
                        menuVm = menuVm,
                        pedidosVm = pedidosClienteVm,
                        cafeteriaAbierta = cafeteriaAbierta,
                        onBack = { nav.popBackStack() },
                        onSeguirPidiendo = {
                            nav.navigate(Rutas.MENU) {
                                popUpTo(Rutas.GRAFO_CLIENTE)
                                launchSingleTop = true
                            }
                        },
                        onConfirmado = { id ->
                            nav.navigate(Rutas.seguimiento(id)) {
                                popUpTo(Rutas.CONFIRMAR) { inclusive = true }
                            }
                        }
                    )
                }
                composable(
                    Rutas.SEGUIMIENTO,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { e ->
                    SeguimientoScreen(
                        pedidoId = e.arguments?.getInt("id") ?: 0,
                        vm = pedidosClienteVm,
                        onBack = { nav.popBackStack() }
                    )
                }
            }

            // ================= PERFIL ADMIN =================
            navigation(startDestination = Rutas.PANEL, route = Rutas.GRAFO_ADMIN) {
                composable(Rutas.PANEL) {
                    PanelScreen(panelVm, onVerPedidos = { irATab(Rutas.GRAFO_ADMIN, Rutas.PEDIDOS_ADMIN) })
                }
                composable(Rutas.PEDIDOS_ADMIN) {
                    PedidosAdminScreen(pedidosAdminVm) { nav.navigate(Rutas.detallePedidoAdmin(it)) }
                }
                composable(
                    Rutas.DETALLE_PEDIDO_ADMIN,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { e ->
                    DetallePedidoAdminScreen(
                        pedidoId = e.arguments?.getInt("id") ?: 0,
                        vm = pedidosAdminVm,
                        onBack = { nav.popBackStack() }
                    )
                }
                composable(Rutas.MENU_ADMIN) {
                    MenuAdminScreen(
                        vm = menuAdminVm,
                        onNuevo = { nav.navigate(Rutas.formulario()) },
                        onEditar = { nav.navigate(Rutas.formulario(it)) }
                    )
                }
                composable(
                    Rutas.FORMULARIO,
                    arguments = listOf(navArgument("id") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { e ->
                    val id = e.arguments?.getInt("id") ?: -1
                    FormularioProductoScreen(
                        productoId = if (id > 0) id else null,
                        vm = menuAdminVm,
                        onBack = { nav.popBackStack() }
                    )
                }
                composable(Rutas.AJUSTES) {
                    AjustesScreen(sesionVm, onCambiarPerfil = cambiarPerfil, onCerrarSesion = cerrarSesion)
                }
            }
        }
    }
}
