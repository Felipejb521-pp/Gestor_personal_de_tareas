package com.example.gestor_personal_de_tareas

import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.Inet4Address
import java.net.InetAddress

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

            // Cliente OkHttp personalizado para forzar el uso de IPv4 en el emulador (evitando fallos de IPv6)
            val okHttpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .dns(object : Dns {
                    override fun lookup(hostname: String): List<InetAddress> {
                        val direcciones = InetAddress.getAllByName(hostname).toList()
                        val ipv4Direcciones = mutableListOf<InetAddress>()
                        for (dir in direcciones) {
                            if (dir is Inet4Address) {
                                ipv4Direcciones.add(dir)
                            }
                        }
                        return if (ipv4Direcciones.isNotEmpty()) ipv4Direcciones else direcciones
                    }
                })
                .build()

            return Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TareaApiService::class.java)
        }
    }
}
