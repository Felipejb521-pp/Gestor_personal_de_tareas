package com.example.gestor_personal_de_tareas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDAO{

    @Query("SELECT * FROM tareas ORDER BY id DESC")
    suspend fun obtener(): List<TareaEntity>
    @Insert
    //funciones suspend pausan la corutina sin retener el hilo a diferencia del Thread.sleep
    suspend fun insertar(tarea: TareaEntity)
    @Update
    suspend fun actualizar(tarea: TareaEntity)
    @Delete
    suspend fun eliminar(tarea: TareaEntity)
}