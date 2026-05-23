package com.example.snake.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ResultadoPartida {
    GANADA,
    PERDIDA_COLISION,
    PERDIDA_TIEMPO
}

/**
 * Contiene todos los datos del log de la partida.
 * Los datos se acumulan en dos fases:
 *  - Al iniciar: alias, tamaño, configuración de tiempo (desde Configuración)
 *  - Al terminar: resultado, tiempos, manzanas, longitud, fecha (desde el juego)
 */
data class LogPartida(
    val id: Int = 0,
    val alias: String = "",
    val tamanoParrilla: String = "",
    val controlTiempo: Boolean = false,
    val tiempoMaximoSeg: Int = 0,
    val resultado: ResultadoPartida? = null,
    val tiempoTotalSeg: Int = 0,
    val tiempoSobranteSeg: Int = 0,
    val manzanasComidas: Int = 0,
    val longitudFinal: Int = 0,
    val fechaHoraFin: Date? = null
) {
    /**
     * FIX [F7]: log completamente en catalán, sin mezcla de idiomas.
     * Genera el texto formateado para mostrar en Resultados y enviar por email.
     */
    fun generarTexto(): String {
        val sb = StringBuilder()

        sb.appendLine("Àlies: $alias")
        sb.appendLine("Mida graella: $tamanoParrilla")
        sb.appendLine("Temps total: $tiempoTotalSeg segs")
        sb.appendLine("Pomes menjades: $manzanasComidas")
        sb.appendLine("Longitud serp: $longitudFinal")

        when (resultado) {
            ResultadoPartida.GANADA -> {
                sb.appendLine("Has guanyat !!")
                if (controlTiempo && tiempoSobranteSeg > 0) {
                    sb.appendLine("Han sobrat $tiempoSobranteSeg segons !")
                }
            }
            ResultadoPartida.PERDIDA_COLISION -> {
                sb.appendLine("Has perdut !! La serp ha xocat")
            }
            ResultadoPartida.PERDIDA_TIEMPO -> {
                sb.appendLine("Has esgotat el temps !!")
            }
            null -> { /* Partida en curs */ }
        }

        return sb.toString().trimEnd()
    }

    /** Asunto del email: "Log – fecha y hora" */
    fun generarAsuntoEmail(): String {
        val fecha = fechaHoraFin ?: Date()
        val formato = SimpleDateFormat("d/M/yy, HH:mm", Locale.getDefault())
        return "Log - ${formato.format(fecha)}"
    }

    /** Fecha y hora formateada para mostrar en pantalla. */
    fun fechaHoraFormateada(): String {
        val fecha = fechaHoraFin ?: Date()
        val formato = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.getDefault())
        return formato.format(fecha)
    }

    companion object {
        fun desdeConfiguracion(config: ConfiguracionPartida): LogPartida {
            return LogPartida(
                alias          = config.alias,
                tamanoParrilla = config.tamanoParrilla.etiqueta,
                controlTiempo  = config.controlTiempo,
                tiempoMaximoSeg = config.tiempoMaximoSeg
            )
        }
    }
}