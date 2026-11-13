package com.example.gestor_personal_de_tareas

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "ajustes_usuario")

class PreferenciasManager(private val context: Context){

    //crear clave
    val  CLAVE_SOLO_PENDIENTES = booleanPreferencesKey("mostrar solo pendientes")
    //leer la preferencia
    suspend fun leerSoloPendientes(): Boolean{
        val preferencias = context.dataStore.data.first()
        val valorGuardado = preferencias[CLAVE_SOLO_PENDIENTES]
        var resultado = false

        if (valorGuardado != null) {
            resultado = valorGuardado
        }
        return resultado
    }
    //abre archivo configuracion interno y te da una copia editable para que modifiques
    suspend fun guardarSoloPendientes(activar: Boolean){
        context.dataStore.edit { it[CLAVE_SOLO_PENDIENTES]=activar
        }
    }
}