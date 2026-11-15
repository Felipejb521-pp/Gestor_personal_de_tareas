package com.example.gestor_personal_de_tareas

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Modelo de datos que coincide con el JSON de la API pública de JSONPlaceholder
data class TareaApiDto(
    val id: Int,
    val title: String,
    val completed: Boolean
)

// Interfaz con la petición GET para traer 10 tareas de ejemplo desde internet
interface TareaApiService {

    @GET("todos?_limit=10")
    suspend fun obtenerTareasEjemplo(): List<TareaApiDto>

    companion object {
        // Función para construir e inicializar la instancia de Retrofit
        fun crear(): TareaApiService {
            return Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TareaApiService::class.java)
        }
    }
}
