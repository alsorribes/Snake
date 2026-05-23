package com.example.snake.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snake.model.ResultadoPartida

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val alias: String,
    val tamanoParrilla: String,
    val controlTiempo: Boolean,
    val tiempoMaximoSeg: Int,
    val resultado: String,
    val tiempoTotalSeg: Int,
    val tiempoSobranteSeg: Int,
    val manzanasComidas: Int,
    val longitudFinal: Int,
    val fechaHoraFin: Long
)