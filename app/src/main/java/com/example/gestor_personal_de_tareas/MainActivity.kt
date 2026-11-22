package com.example.gestor_personal_de_tareas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gestor_personal_de_tareas.data.TareaDataBase
import com.example.gestor_personal_de_tareas.pantallas.PantallaApi
import com.example.gestor_personal_de_tareas.pantallas.PantallaFormulario
import com.example.gestor_personal_de_tareas.pantallas.PantallaPrincipal
import com.example.gestor_personal_de_tareas.ui.theme.Gestor_personal_de_tareasTheme

// -------------------------------------------------------------------------------------
// ACTIVIDAD PRINCIPAL (MainActivity)
// Responsabilidad única: Inicializar la app y gestionar el selector principal de pantallas.
// -------------------------------------------------------------------------------------
class MainActivity : ComponentActivity() {

    // Variables de estado para navegación y edición
    private var pantallaActualState = mutableStateOf("PRINCIPAL")
    private var tareaAEditarState = mutableStateOf<TareaEntity?>(null)
    private lateinit var viewModel: TareaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Paso 1: Inicialización de capa de datos y preferencias
        val baseDatos = TareaDataBase.obtenerBaseDatos(this)
        val preferencias = PreferenciasManager(this)
        viewModel = TareaViewModel(baseDatos.tareaDAO(), preferencias)

        // Configuración de pantalla completa
        enableEdgeToEdge()

        // Paso 2: Dibujar la interfaz gráfica desacoplada
        setContent {
            Gestor_personal_de_tareasTheme {

                var pantallaActual by rememberSaveable { pantallaActualState }
                var tareaAEditar by remember { tareaAEditarState }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // Selección de pantalla mediante sentencias IF - ELSE IF explícitas
                        if (pantallaActual == "PRINCIPAL") {

                            PantallaPrincipal(
                                viewModel = viewModel,
                                onNavegarAFormulario = ::abrirFormularioParaCrearOEditar,
                                onCompartirTarea = ::ejecutarCompartirTarea,
                                onNavegarAApi = ::abrirPantallaApi
                            )

                        } else if (pantallaActual == "FORMULARIO") {

                            PantallaFormulario(
                                tareaEditar = tareaAEditar,
                                onGuardar = ::procesarGuardarTarea,
                                onCancelar = ::volverAPrincipal
                            )

                        } else if (pantallaActual == "API") {

                            PantallaApi(
                                onVolver = ::volverAPrincipal
                            )

                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // MÉTODOS NOMBRADOS PARA EVENTOS Y NAVEGACIÓN (Sustituyen a las lambdas anónimas)
    // ---------------------------------------------------------------------------------

    // Abrir formulario para crear o editar
    fun abrirFormularioParaCrearOEditar(tarea: TareaEntity?) {
        tareaAEditarState.value = tarea
        pantallaActualState.value = "FORMULARIO"
    }

    // Volver a la pantalla principal
    fun volverAPrincipal() {
        pantallaActualState.value = "PRINCIPAL"
    }

    // Navegar a la pantalla de API
    fun abrirPantallaApi() {
        pantallaActualState.value = "API"
    }

    // Procesar guardado de tarea (Creación o Edición)
    fun procesarGuardarTarea(titulo: String, descripcion: String, prioridad: String, estado: String) {
        val tareaExistente = tareaAEditarState.value

        if (tareaExistente == null) {
            // Tarea Nueva
            viewModel.crearTarea(titulo, descripcion, prioridad)
        } else {
            // Tarea Existente (Edición)
            val tareaActualizada = TareaEntity(
                id = tareaExistente.id,
                titulo = titulo,
                descripcion = descripcion,
                prioridad = prioridad,
                estado = estado
            )
            viewModel.editarTarea(tareaActualizada)
        }
        volverAPrincipal()
    }

    // Compartir tarea mediante Intent nativo
    fun ejecutarCompartirTarea(tarea: TareaEntity) {
        val textoCompartir = "Tarea: ${tarea.titulo}\nPrioridad: ${tarea.prioridad}\nEstado: ${tarea.estado}"

        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, textoCompartir)

        val selectorApps = Intent.createChooser(sendIntent, "Compartir tarea")
        startActivity(selectorApps)
    }
}

// -------------------------------------------------------------------------------------
// PREVIEW
// -------------------------------------------------------------------------------------
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
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
