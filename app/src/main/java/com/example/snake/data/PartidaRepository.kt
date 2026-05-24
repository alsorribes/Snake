package com.example.snake.data

import com.example.snake.model.LogPartida
import com.example.snake.model.ResultadoPartida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class PartidaRepository(private val dao: PartidaDao) {

    val historial: Flow<List<LogPartida>> = dao.obtenerTodas().map { lista ->
        lista.map { it.aLogPartida() }
    }

    suspend fun guardar(log: LogPartida) {
        val resultado = log.resultado ?: return
        dao.insertar(log.aEntity(resultado))
    }
}


private fun PartidaEntity.aLogPartida() = LogPartida(
    id                = id,
    alias             = alias,
    tamanoParrilla    = tamanoParrilla,
    controlTiempo     = controlTiempo,
    tiempoMaximoSeg   = tiempoMaximoSeg,
    resultado         = ResultadoPartida.valueOf(resultado),
    tiempoTotalSeg    = tiempoTotalSeg,
    tiempoSobranteSeg = tiempoSobranteSeg,
    manzanasComidas   = manzanasComidas,
    longitudFinal     = longitudFinal,
    fechaHoraFin      = Date(fechaHoraFin)
)

private fun LogPartida.aEntity(resultado: ResultadoPartida) = PartidaEntity(
    alias             = alias,
    tamanoParrilla    = tamanoParrilla,
    controlTiempo     = controlTiempo,
    tiempoMaximoSeg   = tiempoMaximoSeg,
    resultado         = resultado.name,
    tiempoTotalSeg    = tiempoTotalSeg,
    tiempoSobranteSeg = tiempoSobranteSeg,
    manzanasComidas   = manzanasComidas,
    longitudFinal     = longitudFinal,
    fechaHoraFin      = fechaHoraFin?.time ?: System.currentTimeMillis()
)