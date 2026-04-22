package com.example.snakegame.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Posibles resultados finales de una partida.
 */
enum class ResultadoPartida {
    GANADA,           // El jugador ha comido todas las manzanas posibles / completado el objetivo
    PERDIDA_COLISION, // La serpiente ha chocado con la pared o consigo misma
    PERDIDA_TIEMPO    // Se ha agotado el tiempo máximo
}

/**
 * Contiene todos los datos que se mostrarán en la pantalla de Resultados
 * y se enviarán como cuerpo del email.
 *
 * Los datos se van acumulando durante la partida:
 *  - Datos de configuración: se añaden al inicio (pantalla Configuración)
 *  - Datos de resultado:     se añaden al final   (pantalla Resultados)
 */
data class LogPartida(
    // --- Datos de configuración (disponibles desde el inicio) ---
    val alias: String = "",
    val tamanoParrilla: String = "",
    val controlTiempo: Boolean = false,
    val tiempoMaximoSeg: Int = 0,

    // --- Datos de resultado (disponibles al terminar) ---
    val resultado: ResultadoPartida? = null,
    val tiempoTotalSeg: Int = 0,
    val tiempoSobranteSeg: Int = 0,
    val manzanasComidas: Int = 0,
    val longitudFinal: Int = 0,
    val fechaHoraFin: Date? = null
) {
    /**
     * Genera el texto del log formateado para mostrar en la pantalla de Resultados
     * y para usar como cuerpo del email.
     */
    fun generarTexto(): String {
        val sb = StringBuilder()

        sb.appendLine("Alias: $alias")
        sb.appendLine("Mida graella: $tamanoParrilla")
        sb.appendLine("Temps Total: $tiempoTotalSeg secs.")
        sb.appendLine("Manzanas comidas: $manzanasComidas")
        sb.appendLine("Longitud serpiente: $longitudFinal")

        when (resultado) {
            ResultadoPartida.GANADA -> {
                sb.appendLine("Has guanyat !!")
                if (controlTiempo && tiempoSobranteSeg > 0) {
                    sb.appendLine("Han sobrat $tiempoSobranteSeg segons !")
                }
            }
            ResultadoPartida.PERDIDA_COLISION -> {
                sb.appendLine("Has perdut !! La serp ha xocat.")
            }
            ResultadoPartida.PERDIDA_TIEMPO -> {
                sb.appendLine("Has esgotat el temps !!")
            }
            null -> { /* Partida aún en curso, no se añade resultado */ }
        }

        return sb.toString().trimEnd()
    }

    /**
     * Genera el asunto del email según el formato: "Log – fecha y hora"
     */
    fun generarAsuntoEmail(): String {
        val fecha = fechaHoraFin ?: Date()
        val formato = SimpleDateFormat("d/M/yy, HH:mm", Locale.getDefault())
        return "Log - ${formato.format(fecha)}"
    }

    /**
     * Devuelve la fecha y hora de fin formateada para mostrar en pantalla.
     */
    fun fechaHoraFormateada(): String {
        val fecha = fechaHoraFin ?: Date()
        val formato = SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.getDefault())
        return formato.format(fecha)
    }

    companion object {
        /**
         * Crea un LogPartida inicial a partir de la configuración de partida.
         * Se llama al pulsar "Empezar" en la pantalla de Configuración.
         */
        fun desdeConfiguracion(config: ConfiguracionPartida): LogPartida {
            return LogPartida(
                alias = config.alias,
                tamanoParrilla = config.tamanoParrilla.etiqueta,
                controlTiempo = config.controlTiempo,
                tiempoMaximoSeg = config.tiempoMaximoSeg
            )
        }
    }
}