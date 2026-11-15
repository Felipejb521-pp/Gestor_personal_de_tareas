package com.example.gestor_personal_de_tareas


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gestor_personal_de_tareas.data.TareaDataBase
import com.example.gestor_personal_de_tareas.ui.theme.Gestor_personal_de_tareasTheme
import android.content.Intent
// Activity principal de la aplicación
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Paso 1: Inicializar la base de datos Room
        val baseDatos = TareaDataBase.obtenerBaseDatos(this)

        // Paso 2: Inicializar el gestor de preferencias DataStore
        val preferencias = PreferenciasManager(this)

        // Paso 3: Inicializar el ViewModel que conecta la base de datos y las preferencias
        val viewModel = TareaViewModel(baseDatos.tareaDAO(), preferencias)

        // Configurar la pantalla para usar todo el espacio disponible
        enableEdgeToEdge()

        // Paso 4: Dibujar la interfaz de usuario con Jetpack Compose
        setContent {
            Gestor_personal_de_tareasTheme {

                // Variable de estado que guarda cuál pantalla se está mostrando ("PRINCIPAL", "FORMULARIO" o "API")
                var pantallaActual by rememberSaveable { mutableStateOf("PRINCIPAL") }

                // Variable de estado para saber si estamos editando una tarea existente o creando una nueva
                var tareaAEditar by remember { mutableStateOf<TareaEntity?>(null) }

                // Contenedor principal de la pantalla
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // Usamos una estructura IF - ELSE IF tradicional para elegir la pantalla visible:
                        if (pantallaActual == "PRINCIPAL") {

                            // Muestra la lista principal de tareas
                            PantallaPrincipal(
                                viewModel = viewModel,
                                irAFormulario = { tareaSeleccionada ->
                                    tareaAEditar = tareaSeleccionada
                                    pantallaActual = "FORMULARIO"
                                },
                                compartirTarea = { tarea ->
                                    // Paso 1: Preparamos el texto que se compartirá
                                    val textoCompartir = "Tarea: ${tarea.titulo}\nPrioridad: ${tarea.prioridad}\nEstado: ${tarea.estado}"

                                    // Paso 2: Creamos el Intent (la intención de envío) en Android
                                    val sendIntent = Intent(Intent.ACTION_SEND)
                                    sendIntent.type = "text/plain"
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, textoCompartir)

                                    // Paso 3: Abrimos la ventana emergente para elegir la app (WhatsApp, Email, Notas, etc.)
                                    val selectorApps = Intent.createChooser(sendIntent, "Compartir tarea")
                                    startActivity(selectorApps)
                                },
                                irAApi = {
                                    pantallaActual = "API"
                                }
                            )

                        } else if (pantallaActual == "FORMULARIO") {

                            // Muestra el formulario para crear o editar una tarea
                            PantallaFormulario(
                                tareaEditar = tareaAEditar,
                                onGuardar = { titulo, descripcion, prioridad, estado ->
                                    // Comprobamos si es una tarea nueva o una edición usando un IF tradicional
                                    if (tareaAEditar == null) {
                                        viewModel.crearTarea(titulo, descripcion, prioridad)
                                    } else {
                                        val tareaExistente = tareaAEditar
                                        if (tareaExistente != null) {
                                            val tareaActualizada = TareaEntity(
                                                id = tareaExistente.id,
                                                titulo = titulo,
                                                descripcion = descripcion,
                                                prioridad = prioridad,
                                                estado = estado
                                            )
                                            viewModel.editarTarea(tareaActualizada)
                                        }
                                    }
                                    // Volvemos a la pantalla principal tras guardar
                                    pantallaActual = "PRINCIPAL"
                                },
                                onCancelar = {
                                    // Volvemos a la pantalla principal sin guardar
                                    pantallaActual = "PRINCIPAL"
                                }
                            )

                        } else if (pantallaActual == "API") {

                            // Muestra la pantalla informativa de Ejemplos API
                            PantallaApi(
                                onVolver = {
                                    pantallaActual = "PRINCIPAL"
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// PANTALLA PRINCIPAL: Muestra la cabecera, botón de nueva tarea, filtro y la lista de tareas
// -------------------------------------------------------------------------------------
@Composable
fun PantallaPrincipal(
    viewModel: TareaViewModel,
    irAFormulario: (TareaEntity?) -> Unit,
    compartirTarea: (TareaEntity) -> Unit,
    irAApi: () -> Unit
) {
    // Obtenemos la lista de tareas visibles desde el ViewModel
    val tareasVisibles = viewModel.obtenerTareasVisibles()

    // Variable para controlar si el diálogo de borrar tarea está abierto o cerrado
    var tareaABorrar by remember { mutableStateOf<TareaEntity?>(null) }

    // Disposición vertical de todos los elementos
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bloque 1: Cabecera con el título de la app y el botón Ejemplos API
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Tareas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = { irAApi() }) {
                Text(text = "Ejemplos API")
            }
        }

        // Bloque 2: Botón destacado para crear una Nueva Tarea
        Button(
            onClick = { irAFormulario(null) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nueva Tarea")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Nueva Tarea")
        }

        // Bloque 3: Interruptor (Switch) para filtrar solo las tareas pendientes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mostrar solo pendientes",
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = viewModel.mostrarSoloPendientes,
                onCheckedChange = { nuevoEstado ->
                    viewModel.cambiarFiltroSoloPendientes(nuevoEstado)
                }
            )
        }

        // Bloque 4: Lista de tareas o mensaje de lista vacía usando un IF tradicional
        if (tareasVisibles.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay tareas registradas.")
            }
        } else {
            // Lista desplegable para mostrar las tareas
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasVisibles) { tarea ->
                    // Tarjeta individual para cada tarea
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Casilla de verificación para marcar como completada
                            val estaCompletada = (tarea.estado == "Completada")
                            Checkbox(
                                checked = estaCompletada,
                                onCheckedChange = { estaMarcada ->
                                    viewModel.cambiarCompletada(tarea, estaMarcada)
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Información de la tarea (Título y Prioridad)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = tarea.titulo, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Prioridad: ${tarea.prioridad} | ${tarea.estado}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Botón Editar
                            IconButton(onClick = { irAFormulario(tarea) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            // Botón Compartir
                            IconButton(onClick = { compartirTarea(tarea) }) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir")
                            }

                            // Botón Eliminar
                            IconButton(onClick = { tareaABorrar = tarea }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar una tarea usando un IF clásico
    if (tareaABorrar != null) {
        val tareaSeleccionada = tareaABorrar
        val tituloTarea = if (tareaSeleccionada != null) tareaSeleccionada.titulo else ""

        AlertDialog(
            onDismissRequest = { tareaABorrar = null },
            title = { Text(text = "Confirmar eliminación") },
            text = { Text(text = "¿Eliminar '$tituloTarea'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tareaSeleccionada != null) {
                            viewModel.eliminarTarea(tareaSeleccionada)
                        }
                        tareaABorrar = null
                    }
                ) {
                    Text(text = "Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaABorrar = null }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

// -------------------------------------------------------------------------------------
// PANTALLA FORMULARIO: Formulario simple para crear o editar una tarea
// -------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(
    tareaEditar: TareaEntity?,
    onGuardar: (String, String, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    // Definimos el valor inicial de cada campo mediante IF imperativos tradicionales
    val tituloInicial = if (tareaEditar != null) tareaEditar.titulo else ""
    val descripcionInicial = if (tareaEditar != null) tareaEditar.descripcion else ""
    val prioridadInicial = if (tareaEditar != null) tareaEditar.prioridad else "Media"
    val estadoInicial = if (tareaEditar != null) tareaEditar.estado else "Pendiente"

    // Variables de estado del formulario
    var titulo by rememberSaveable { mutableStateOf(tituloInicial) }
    var descripcion by rememberSaveable { mutableStateOf(descripcionInicial) }
    var prioridad by rememberSaveable { mutableStateOf(prioridadInicial) }
    var estado by rememberSaveable { mutableStateOf(estadoInicial) }
    var mensajeError by rememberSaveable { mutableStateOf("") }

    var menuPrioridadAbierto by remember { mutableStateOf(false) }
    val opcionesPrioridad = listOf("Alta", "Media", "Baja")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título de la pantalla (Nueva Tarea o Editar Tarea)
        val textoTituloPantalla = if (tareaEditar == null) "Nueva Tarea" else "Editar Tarea"
        Text(
            text = textoTituloPantalla,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Campo de entrada para el Título
        val tieneError = mensajeError.isNotEmpty()
        OutlinedTextField(
            value = titulo,
            onValueChange = { nuevoTexto ->
                titulo = nuevoTexto
                mensajeError = ""
            },
            label = { Text("Título *") },
            isError = tieneError,
            modifier = Modifier.fillMaxWidth()
        )

        // Si hay un mensaje de error, lo mostramos con un IF tradicional
        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Campo de entrada para la Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { nuevoTexto -> descripcion = nuevoTexto },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        // Menú desplegable para seleccionar la Prioridad
        ExposedDropdownMenuBox(
            expanded = menuPrioridadAbierto,
            onExpandedChange = { menuPrioridadAbierto = !menuPrioridadAbierto }
        ) {
            OutlinedTextField(
                value = prioridad,
                onValueChange = {},
                readOnly = true,
                label = { Text("Prioridad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuPrioridadAbierto) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = menuPrioridadAbierto,
                onDismissRequest = { menuPrioridadAbierto = false }
            ) {
                // Recorremos la lista de opciones con un bucle FOR tradicional
                for (opcion in opcionesPrioridad) {
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            prioridad = opcion
                            menuPrioridadAbierto = false
                        }
                    )
                }
            }
        }

        // Casilla para seleccionar si está completada o pendiente
        val estaCompletadaFormulario = (estado == "Completada")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estaCompletadaFormulario,
                onCheckedChange = { estaMarcada ->
                    if (estaMarcada) {
                        estado = "Completada"
                    } else {
                        estado = "Pendiente"
                    }
                }
            )
            Text(text = "Marcar como completada")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botones Cancelar y Guardar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onCancelar() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    val tituloLimpio = titulo.trim()
                    // Validación del título: debe tener 3 o más caracteres
                    if (tituloLimpio.length < 3) {
                        mensajeError = "El título debe tener al menos 3 caracteres."
                    } else {
                        val descripcionLimpia = descripcion.trim()
                        onGuardar(tituloLimpio, descripcionLimpia, prioridad, estado)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// PANTALLA API: Carga y muestra tareas de ejemplo desde Internet usando Retrofit
// -------------------------------------------------------------------------------------
@Composable
fun PantallaApi(onVolver: () -> Unit) {
    // Lista de tareas cargadas desde la API
    var listaApi by remember { mutableStateOf<List<TareaApiDto>>(emptyList()) }
    var estaCargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    // Objeto para lanzar corrutinas asíncronas en Compose
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ejemplos API (Retrofit)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Fila de botones principales (Volver y Cargar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onVolver() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Volver")
            }

            Button(
                onClick = {
                    estaCargando = true
                    mensajeError = ""
                    coroutineScope.launch {
                        try {
                            // Paso 1: Crear la interfaz del servicio Retrofit
                            val apiService = TareaApiService.crear()
                            // Paso 2: Petición HTTP GET a la API en Internet
                            val resultado = apiService.obtenerTareasEjemplo()
                            listaApi = resultado
                        } catch (e: Exception) {
                            mensajeError = "Error al conectar: ${e.message}"
                        }
                        estaCargando = false
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cargar API")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estructura IF - ELSE IF tradicional para gestionar el estado visual
        if (estaCargando) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando tareas desde Internet...")
            }
        } else if (mensajeError.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (listaApi.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pulsa 'Cargar API' para traer 10 tareas de prueba desde Internet.")
            }
        } else {
            // Muestra la lista de tareas recibidas desde la API pública
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaApi) { tareaDto ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ID #${tareaDto.id}: ${tareaDto.title}",
                                fontWeight = FontWeight.Bold
                            )
                            val textoEstado = if (tareaDto.completed) "Completada" else "Pendiente"
                            Text(
                                text = "Estado en servidor: $textoEstado",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// PREVIEW: Vista previa para Jetpack Compose en Android Studio
// -------------------------------------------------------------------------------------
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Gestor_personal_de_tareasTheme {
        Greeting("Android")
    }
}
