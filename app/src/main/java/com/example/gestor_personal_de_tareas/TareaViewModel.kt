package com.example.gestor_personal_de_tareas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TareaViewModel (val dao: TareaDAO, val preferencias : PreferenciasManager): ViewModel(){
    var listaTareas by mutableStateOf<List<TareaEntity>>(emptyList())
    var mostrarSoloPendientes by mutableStateOf(false)

    //se ejecuta automaticamente al iniciar el viewmodel
    init {
        cargarTareas()
    }


    // 1. Cargar todas las tareas desde Room
    fun cargarTareas() {
        viewModelScope.launch {
            listaTareas = dao.obtener()
        }
    }

    // 2. Recuperar el estado del Switch desde DataStore
    private fun cargarPreferenciaInicial() {
        viewModelScope.launch {
            mostrarSoloPendientes = preferencias.leerSoloPendientes()
        }
    }

    // 3. Guardar el cambio cuando el usuario pulsa el Switch
    fun cambiarFiltroSoloPendientes(nuevoValor: Boolean) {
        mostrarSoloPendientes = nuevoValor
        viewModelScope.launch {
            preferencias.guardarSoloPendientes(nuevoValor)
        }
    }

    // 4. Obtener las tareas visibles (con bucle 'for' clásico y un solo return)
    fun obtenerTareasVisibles(): List<TareaEntity> {
        var resultado: List<TareaEntity> = listaTareas

        if (mostrarSoloPendientes) {
            val listaFiltrada = mutableListOf<TareaEntity>()
            for (tarea in listaTareas) {
                if (tarea.estado != "Completada") {
                    listaFiltrada.add(tarea)
                }
            }
            resultado = listaFiltrada
        }

        return resultado
    }

    // 5. Crear tarea nueva
    fun crearTarea(titulo: String, descripcion: String, prioridad: String) {
        viewModelScope.launch {
            val nuevaTarea = TareaEntity(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                prioridad = prioridad,
                estado = "Pendiente"
            )
            dao.insertar(nuevaTarea)
            cargarTareas() // Refresca la lista en pantalla
        }
    }

    // 6. Editar tarea existente
    fun editarTarea(tarea: TareaEntity) {
        viewModelScope.launch {
            dao.actualizar(tarea)
            cargarTareas()
        }
    }

    // 7. Marcar o desmarcar como completada
    fun cambiarCompletada(tarea: TareaEntity, estaMarcada: Boolean) {
        viewModelScope.launch {
            var nuevoEstado = "Pendiente"
            if (estaMarcada) {
                nuevoEstado = "Completada"
            }
            val tareaActualizada = tarea.copy(estado = nuevoEstado)
            dao.actualizar(tareaActualizada)
            cargarTareas()
        }
    }

    // 8. Eliminar tarea
    fun eliminarTarea(tarea: TareaEntity) {
        viewModelScope.launch {
            dao.eliminar(tarea)
            cargarTareas()
        }
    }
}