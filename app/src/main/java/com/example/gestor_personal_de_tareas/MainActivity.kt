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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gestor_personal_de_tareas.data.TareaDataBase
import com.example.gestor_personal_de_tareas.ui.theme.Gestor_personal_de_tareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inicializar Base Datos(DataStore),Room y ViewModel
        val baseDatos = TareaDataBase.obtenerBaseDatos(this)
        val preferencias = PreferenciasManager(this)
        val viewModel = TareaViewModel(baseDatos.tareaDAO(), preferencias)
        enableEdgeToEdge()
        setContent {
            Gestor_personal_de_tareasTheme {
                // Estados para cambiar de pantalla sin librerías externas
                var pantallaActual by rememberSaveable { mutableStateOf("PRINCIPAL") }
                var tareaAEditar by remember { mutableStateOf<TareaEntity?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (pantallaActual == "PRINCIPAL") {
                            PantallaPrincipal(
                                viewModel = viewModel,
                                irAFormulario = {
                                    tareaAEditar = it
                                    pantallaActual = "FORMULARIO"
                                },
                                compartirTarea = {
                                    // Lo conectaremos mañana (Día 4)
                                },
                                irAApi = {
                                    pantallaActual = "API"
                                }
                            )
                        } else if (pantallaActual == "FORMULARIO") {
                            PantallaFormulario(
                                tareaEditar = tareaAEditar,
                                onGuardar = { titulo, descripcion, prioridad, estado ->
                                    if (tareaAEditar == null) {
                                        viewModel.crearTarea(titulo, descripcion, prioridad)
                                    } else {
                                        viewModel.editarTarea(
                                            tareaAEditar!!.copy(
                                                titulo = titulo,
                                                descripcion = descripcion,
                                                prioridad = prioridad,
                                                estado = estado
                                            )
                                        )
                                    }
                                    pantallaActual = "PRINCIPAL"
                                },
                                onCancelar = {
                                    pantallaActual = "PRINCIPAL"
                                }
                            )
                        } else if (pantallaActual == "API") {
                            // Aquí pondremos PantallaApi mañana (Día 4)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal(
    viewModel: TareaViewModel,
    irAFormulario: (TareaEntity?) -> Unit,
    compartirTarea: (TareaEntity) -> Unit,
    irAApi: () -> Unit
) {
    val tareasVisibles = viewModel.obtenerTareasVisibles()
    var tareaABorrar by remember { mutableStateOf<TareaEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Cabecera con botones de Nueva Tarea y Tareas de ejemplo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Tareas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Row {
                OutlinedButton(onClick = irAApi) {
                    Text(text = "Ejemplos API")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { irAFormulario(null) }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva")
                    Text(text = "Nueva")
                }
            }
        }

        // 2. Switch de DataStore: "Mostrar solo pendientes"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mostrar solo pendientes")
            Switch(
                checked = viewModel.mostrarSoloPendientes,
                onCheckedChange = { viewModel.cambiarFiltroSoloPendientes(it) }
            )
        }

        // 3. Listado de tareas o mensaje si está vacía
        if (tareasVisibles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay tareas registradas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasVisibles) { tarea ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = tarea.estado == "Completada",
                                onCheckedChange = { viewModel.cambiarCompletada(tarea, it) }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = tarea.titulo, fontWeight = FontWeight.Bold)
                                Text(text = "Prioridad: ${tarea.prioridad} | ${tarea.estado}")
                            }
                            IconButton(onClick = { irAFormulario(tarea) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { compartirTarea(tarea) }) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir")
                            }
                            IconButton(onClick = { tareaABorrar = tarea }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    // 4. AlertDialog de confirmación de borrado
    if (tareaABorrar != null) {
        AlertDialog(
            onDismissRequest = { tareaABorrar = null },
            title = { Text(text = "Confirmar eliminación") },
            text = { Text(text = "¿Eliminar '${tareaABorrar?.titulo}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarTarea(tareaABorrar!!)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(
    tareaEditar: TareaEntity?,
    onGuardar: (String, String, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(tareaEditar?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tareaEditar?.descripcion ?: "") }
    var prioridad by rememberSaveable { mutableStateOf(tareaEditar?.prioridad ?: "Media") }
    var estado by rememberSaveable { mutableStateOf(tareaEditar?.estado ?: "Pendiente") }
    var mensajeError by rememberSaveable { mutableStateOf("") }

    var menuPrioridadAbierto by remember { mutableStateOf(false) }
    val opcionesPrioridad = listOf("Alta", "Media", "Baja")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (tareaEditar == null) "Nueva Tarea" else "Editar Tarea",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                mensajeError = ""
            },
            label = { Text("Título *") },
            isError = mensajeError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado == "Completada",
                onCheckedChange = { marcada ->
                    estado = if (marcada) "Completada" else "Pendiente"
                }
            )
            Text(text = "Marcar como completada")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val tituloLimpio = titulo.trim()
                    if (tituloLimpio.length < 3) {
                        mensajeError = "El título debe tener al menos 3 caracteres."
                    } else {
                        onGuardar(tituloLimpio, descripcion.trim(), prioridad, estado)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

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