package com.example.gestor_personal_de_tareas

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO{

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