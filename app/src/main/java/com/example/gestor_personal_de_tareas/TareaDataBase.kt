package com.example.gestor_personal_de_tareas

import androidx.room.RoomDatabase
import androidx.room3.Database

@Database
abstract class TareaDataBase:RoomDatabase(){
    abstract fun tareaDAO(): TareaDAO
}