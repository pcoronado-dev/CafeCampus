
ARBOL DE PAQUETES

com.tecsup.cafecampus
├── MainActivity.kt
├── CafeApp.kt                  (crea base, repository y siembra datos)
├── data
│   ├── local
│   │   ├── entity   Producto · Usuario · Pedido · DetallePedido
│   │   ├── dao      ProductoDao · UsuarioDao · PedidoDao
│   │   ├── relation PedidoConDetalle · PedidoPorHora
│   │   ├── CafeDatabase.kt · DatosIniciales.kt
│   ├── repository   CafeRepository (interface) · CafeRepositoryRoom
│   └── prefs        PreferenciasStore (DataStore)
├── model            Rol · EstadoPedido · ItemCarrito
├── viewmodel        Sesion · Menu · PedidosCliente · PanelAdmin · PedidosAdmin · MenuAdmin · Factory
└── ui
    ├── theme · components · navigation · util
    ├── cliente      Splash · Login · Menu · DetalleProducto · ConfirmarPedido · Seguimiento · MisPedidos · Perfil
    └── admin        Panel · PedidosAdmin · DetallePedidoAdmin · MenuAdmin · FormularioProducto · Ajustes

<img width="702" height="1600" alt="6" src="https://github.com/user-attachments/assets/f869661f-7b97-4633-9185-567db9e0bff7" />
<img width="702" height="1600" alt="5" src="https://github.com/user-attachments/assets/7fb207a4-0815-45b3-9d16-e7824fd90a39" />
<img width="702" height="1600" alt="4" src="https://github.com/user-attachments/assets/ab6e7d59-1d92-4411-bfa0-ea15e69ed8bb" />
<img width="702" height="1600" alt="3" src="https://github.com/user-attachments/assets/810cef0e-92b6-4b2b-b2b9-7522f3b6c06e" />
<img width="702" height="1600" alt="2" src="https://github.com/user-attachments/assets/2506986c-906c-48f9-9d9b-208f39a2dc41" />
<img width="702" height="1600" alt="WhatsApp Image 2026-10-21 at 4 08 19 AM" src="https://github.com/user-attachments/assets/47d0023d-06c7-4b2e-a46c-39869c843dde" />


# Prompts utilizados
## Prompt 1 — Buscar en el menú
**Commit:** `ia: agrega busqueda de productos en el menu`

Texto enviado a Gemini:
Tengo una app Android en Kotlin con Jetpack Compose (Material 3, sin XML), arquitectura
MVVM + Repository (interface CafeRepository) y Room. Ya existen MenuViewModel
(con `productos: StateFlow<List<Producto>>` y el carrito) y MenuScreen
(LazyVerticalGrid con ProductoCard).

Agrega una barra de búsqueda en MenuScreen que filtre los productos por nombre
mientras el usuario escribe.

Reglas obligatorias:
- La lógica del filtro va en MenuViewModel, NO en el Composable: crea un
  MutableStateFlow<String> para el texto y combínalo con `productos` usando
  `combine`, exponiendo `productosVisibles: StateFlow<List<Producto>>` con stateIn
  (SharingStarted.WhileSubscribed(5000)).
- El filtro ignora mayúsculas y espacios al inicio y al final.
- En MenuScreen usa un OutlinedTextField con ícono de lupa, botón para borrar el
  texto y placeholder "Buscar en el menú…", y el grid debe mostrar `productosVisibles`.
- Si no hay resultados, muestra el texto "No encontramos productos."
- La View no usa DAO ni Repository, y no toques CafeRepository ni los DAOs.
- Material 3 solamente, sin XML ni colores fijos (usa MaterialTheme.colorScheme).
Muéstrame solo los archivos que cambian, completos.


## Prompt 2 —Registro de estudiante
**Commit:** `ia: agrega registro de estudiante`

Texto enviado a Gemini:
Tengo una app Android en Kotlin con Jetpack Compose (Material 3, sin XML), arquitectura
MVVM + Repository y Room 2.6.1 con KSP. Datos relevantes del proyecto:
- Entidad Usuario(id: Int autogenerado, nombre, correo, clave, rol: String) con rol guardado
  como Rol.name ("CLIENTE" | "ADMIN"). Tabla "usuarios".
- UsuarioDao (interface) con: login(correo, clave): Usuario? (suspend), insertar(u): Long
  (suspend), contar(): Int (suspend) y primeroPorRol(rol): Usuario? (suspend).
- CafeRepository (interface) implementada por CafeRepositoryRoom, que recibe CafeDatabase.
- SesionViewModel(repo, prefs) expone usuarioActual: StateFlow<Usuario?>, error: StateFlow<String?>,
  y la función login(correo, clave, rolSeleccionado, onExito: (Rol) -> Unit). Al hacer login
  guarda el perfil en DataStore con prefs.guardarPerfil(rol) y actualiza usuarioActual.
- LoginScreen (Composable) con selector Estudiante/Administrador y, en la pestaña Estudiante,
  el texto "¿No tienes cuenta? Regístrate".
- Navegación con NavCafe.kt y Rutas.kt (Navigation Compose, rutas string). La función
  irAlPerfil(rol) navega al grafo del perfil limpiando la pila.

Agrega el REGISTRO DE ESTUDIANTE: una pantalla para crear una cuenta nueva con validaciones,
donde el correo debe ser único.

Hazlo por capas, en este orden:
1. UsuarioDao: agrega
   `@Query("SELECT COUNT(*) FROM usuarios WHERE LOWER(correo) = LOWER(:correo)")
    suspend fun contarPorCorreo(correo: String): Int`.
2. CafeRepository (interface): DECLARA PRIMERO
   `suspend fun registrarEstudiante(nombre: String, correo: String, clave: String): Usuario?`
   (devuelve null si el correo ya existe) y luego impleméntala en CafeRepositoryRoom:
   si contarPorCorreo(correo) > 0 devuelve null; si no, inserta
   Usuario(nombre = nombre.trim(), correo = correo.trim().lowercase(), clave = clave,
   rol = Rol.CLIENTE.name) y devuelve ese usuario con el id generado.
   El rol SIEMPRE es CLIENTE: nunca se puede registrar un administrador.
3. SesionViewModel: agrega un StateFlow<String?> `errorRegistro` y la función
   `registrar(nombre, correo, clave, confirmacion, onExito: (Rol) -> Unit)` que:
   - valida en este orden y publica el mensaje en errorRegistro:
     * nombre, correo o contraseña vacíos → "Completa todos los campos"
     * correo sin formato válido (usa android.util.Patterns.EMAIL_ADDRESS) → "Correo no válido"
     * contraseña de menos de 4 caracteres → "La contraseña debe tener al menos 4 caracteres"
     * contraseña distinta de la confirmación → "Las contraseñas no coinciden"
     * correo repetido (el repositorio devuelve null) → "Ese correo ya está registrado"
   - si todo es correcto, guarda el perfil CLIENTE en DataStore, asigna usuarioActual, limpia
     errorRegistro y llama onExito(Rol.CLIENTE) para entrar directo al menú.
   - usa viewModelScope.launch para las llamadas suspend.
   - agrega `limpiarErrorRegistro()`.
4. Rutas.kt: agrega la ruta REGISTRO = "registro".
5. RegistroScreen.kt (ui/cliente): Composable con TopBar "Crear cuenta" con botón de volver,
   campos Nombre, Correo, Contraseña y Confirmar contraseña (usa CafeTextField; las
   contraseñas ocultas con opción de mostrar/ocultar), el mensaje de errorRegistro en
   colorScheme.error y un botón "Crear cuenta" (CafeButton). Debe usar el tema naranja del
   estudiante: solo MaterialTheme.colorScheme, sin colores fijos.
6. NavCafe.kt: agrega composable(Rutas.REGISTRO) y conecta el texto "¿No tienes cuenta?
   Regístrate" de LoginScreen con un callback `onRegistro` para navegar a esa ruta. Al registrarse
   con éxito, navega con irAlPerfil(Rol.CLIENTE).

Reglas obligatorias:
- La View no usa DAO ni Repository: solo llama funciones de SesionViewModel.
- Toda la validación va en el ViewModel, NO en el Composable.
- El ViewModel solo conoce la interface CafeRepository.
- No cambies el esquema de Room ni la versión de la base de datos (solo agregas una consulta).
- Sin XML. Material 3 solamente.
Muéstrame solo los archivos que cambian, completos.


## Prompt 5 — Animaciones y estados vacíos
**Commit:** `ia: agrega animaciones y estados vacios`

Texto enviado a Gemini:
Tengo una app Android en Kotlin con Jetpack Compose (Material 3, sin XML), arquitectura
MVVM + Repository y Room. Navegación con Navigation Compose 2.8 en NavCafe.kt (un NavHost con
grafos anidados `navigation(...)`). Pantallas existentes: SplashScreen, MisPedidosScreen
(pestañas Activos/Historial con SelectorSegmentado y LazyColumn de PedidoCard),
PedidosAdminScreen, PanelScreen, MenuScreen, MenuAdminScreen, ConfirmarPedidoScreen y
SeguimientoScreen, que usa el componente StepperEstado (4 pasos verticales: Recibido,
Preparando, Listo, Entregado).

Mejora SOLO la capa View con animaciones y estados vacíos:

1. Transiciones entre pantallas en NavCafe: en el NavHost define enterTransition,
   exitTransition, popEnterTransition y popExitTransition con slideIntoContainer /
   slideOutOfContainer (dirección Start al avanzar, End al volver) combinados con fadeIn /
   fadeOut, duración 300 ms con tween.
2. Animación del StepperEstado: la línea entre pasos se llena con animateFloatAsState
   (600 ms) al avanzar de estado; el check aparece con AnimatedVisibility + scaleIn con
   spring de rebote; el paso en curso tiene un halo que late con rememberInfiniteTransition.
3. Estados vacíos con ilustración: crea el Composable reutilizable
   `EstadoVacio(icono: ImageVector, titulo: String, mensaje: String, modifier: Modifier,
   accion: (@Composable () -> Unit)? = null)` en ui/components. La ilustración se dibuja solo
   con Compose (círculos de colores del tema y un ícono Material en el centro) y flota
   suavemente con una animación infinita. Úsalo en: Mis pedidos (Activos e Historial, con
   mensaje "No hay pedidos"), Pedidos del administrador, el resumen "Nuevos pedidos" del
   Panel, el Menú cuando la búsqueda no encuentra nada, la gestión del menú sin productos y
   la bolsa vacía de ConfirmarPedido (con botón "Ver el menú").
4. En MisPedidosScreen envuelve el contenido de las pestañas en Crossfade para suavizar el
   cambio entre Activos e Historial.

Reglas obligatorias: solo cambia la capa View (ui/); NO toques ViewModels, CafeRepository,
DAOs ni la base de datos. Las pantallas no deben usar DAO ni Repository. Usa solo
MaterialTheme.colorScheme (sin colores fijos), sin XML ni librerías externas de animación
o de imágenes: solo androidx.compose.animation. Muéstrame solo los archivos que cambian,
completos.

¿Por qué el Repository es una interface? Para que los ViewModels dependan de un contrato y no de Room; así se puede cambiar la fuente de datos (servidor, nube) sin tocarlos.
¿Por qué crearPedido es suspend y observarPedidosCliente es Flow? Crear es una acción única que termina; observar debe avisar a la pantalla cada vez que los datos cambian.
¿Y si dos perfiles usaran bases distintas? Cada uno vería datos diferentes: el estudiante nunca vería el cambio de estado hecho por el administrador.
¿Qué cambiaría si los datos viven en un servidor? Solo una nueva implementación del Repository (p. ej. CafeRepositoryRemoto); ViewModels y pantallas quedan igual.
