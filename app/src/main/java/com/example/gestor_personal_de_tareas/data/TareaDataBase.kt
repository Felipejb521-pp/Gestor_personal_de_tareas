package com.example.gestor_personal_de_tareas.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import com.example.gestor_personal_de_tareas.TareaDAO
import com.example.gestor_personal_de_tareas.TareaEntity

@Database(version = 1 , entities = [TareaEntity::class])
abstract class TareaDataBase:RoomDatabase(){
    abstract fun tareaDAO(): TareaDAO
    companion object {
        private var instancia: TareaDataBase? = null

        fun obtenerBaseDatos(context: Context): TareaDataBase {
            if (instancia == null) {
                instancia = Room.databaseBuilder(
                    context.applicationContext,
                    TareaDataBase::class.java,
                    "tareas_database"
                ).build()
            }
            return instancia!!
        }
    }
}