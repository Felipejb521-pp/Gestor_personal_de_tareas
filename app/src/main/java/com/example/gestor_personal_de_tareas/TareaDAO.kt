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
    fun obtener(): Flow<List<TareaEntity>>
    @Insert
    fun insertar(tarea: TareaEntity)
    @Update
    fun actualizar(tarea: TareaEntity)
    @Delete
    fun eliminar(tarea: TareaEntity)
}