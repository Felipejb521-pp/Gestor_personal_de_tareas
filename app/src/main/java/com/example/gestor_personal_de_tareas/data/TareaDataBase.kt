package com.example.gestor_personal_de_tareas.data

import androidx.room.RoomDatabase
import androidx.room3.Database
import com.example.gestor_personal_de_tareas.DAO

@Database(version = 1)
abstract class TareaDataBase:RoomDatabase(){
    abstract fun tareaDAO(): DAO
}