package com.example.gestor_personal_de_tareas.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestor_personal_de_tareas.TareaEntity

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
