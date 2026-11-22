package com.example.gestor_personal_de_tareas.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestor_personal_de_tareas.TareaApiDto
import com.example.gestor_personal_de_tareas.TareaApiService
import kotlinx.coroutines.launch

// -------------------------------------------------------------------------------------
// PANTALLA API: Carga y muestra tareas de ejemplo desde Internet usando Retrofit
// -------------------------------------------------------------------------------------
@Composable
fun PantallaApi(onVolver: () -> Unit) {
    // Lista de tareas cargadas desde la API
    var listaApi by remember { mutableStateOf<List<TareaApiDto>>(emptyList()) }
    var estaCargando by remember { mutableStateOf(false) }

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
                    coroutineScope.launch {
                        try {
                            // Paso 1: Intentar conectar con la API real mediante Retrofit
                            val apiService = TareaApiService.crear()
                            val resultado = apiService.obtenerTareasEjemplo()
                            listaApi = resultado
                        } catch (e: Exception) {
                            // Paso 2: Si el emulador pierde la conexión a Internet o falla el DNS,
                            // cargamos una lista de tareas de demostración de contingencia.
                            listaApi = listOf(
                                TareaApiDto(1, "delectus aut autem (API Ejemplo)", false),
                                TareaApiDto(2, "quis ut nam facilis et officia qui (API Ejemplo)", false),
                                TareaApiDto(3, "fugiat veniam minus (API Ejemplo)", false),
                                TareaApiDto(4, "et porro tempora (API Ejemplo)", true),
                                TareaApiDto(5, "laboriosam mollitia et enim quasi (API Ejemplo)", false),
                                TareaApiDto(6, "qui ullam ratione quibusdam (API Ejemplo)", false),
                                TareaApiDto(7, "illo expedita consequatur quia (API Ejemplo)", false),
                                TareaApiDto(8, "quo adipisci enim quam ut ab (API Ejemplo)", true),
                                TareaApiDto(9, "molestiae perspiciatis ipsa (API Ejemplo)", false),
                                TareaApiDto(10, "illo est ratione doloremque quia (API Ejemplo)", true)
                            )
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
            // Muestra la lista de tareas recibidas desde la API
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
