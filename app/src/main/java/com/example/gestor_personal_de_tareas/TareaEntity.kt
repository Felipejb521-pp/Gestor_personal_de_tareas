package com.example.gestor_personal_de_tareas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)//le dice a SQLite que esa columna es el identificador único
    // y que debe ir sumando 1, 2, 3... automáticamente cada vez que guardes una fila nueva.
    val id: Int = 0,//Como en Kotlin tienes que darle algún valor inicial al crear el objeto
    // TareaEntity antes de enviarlo a la base de datos, le pones 0 por defecto.
    val titulo: String,
    val descripcion: String,
    val prioridad: String,//Baja,Media,Alta
    val estado: String //Finalizada,Pendiente
)