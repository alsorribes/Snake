package com.example.snake.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    @Insert
    suspend fun insertar(partida: PartidaEntity)

    @Query("""
        SELECT * FROM partidas 
        ORDER BY manzanasComidas DESC, tiempoTotalSeg ASC, alias ASC
    """)
    fun obtenerTodas(): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): PartidaEntity?
}