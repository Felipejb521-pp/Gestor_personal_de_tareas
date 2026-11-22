package com.example.gestor_personal_de_tareas.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestor_personal_de_tareas.TareaEntity
import com.example.gestor_personal_de_tareas.TareaViewModel

// -------------------------------------------------------------------------------------
// PANTALLA PRINCIPAL: Muestra la cabecera, botón de nueva tarea, filtro y la lista de tareas
// -------------------------------------------------------------------------------------
@Composable
fun PantallaPrincipal(
    viewModel: TareaViewModel,
    onNavegarAFormulario: (TareaEntity?) -> Unit,
    onCompartirTarea: (TareaEntity) -> Unit,
    onNavegarAApi: () -> Unit
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
            OutlinedButton(onClick = { onNavegarAApi() }) {
                Text(text = "Ejemplos API")
            }
        }

        // Bloque 2: Botón destacado para crear una Nueva Tarea
        Button(
            onClick = { onNavegarAFormulario(null) },
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
                            IconButton(onClick = { onNavegarAFormulario(tarea) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            // Botón Compartir
                            IconButton(onClick = { onCompartirTarea(tarea) }) {
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
