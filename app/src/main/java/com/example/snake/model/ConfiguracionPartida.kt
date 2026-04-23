package com.example.snake.model

// Tamaños de parrilla disponibles (filas x columnas)
enum class TamanoParrilla(val filas: Int, val columnas: Int, val etiqueta: String) {
    PEQUENA(10, 10, "Pequeña (10×10)"),
    MEDIANA(15, 15, "Mediana (15×15)"),
    GRANDE(20, 20,  "Grande (20×20)")
}

// Tiempo máximo por defecto si el usuario activa el control de tiempo
const val TIEMPO_MAXIMO_DEFECTO_SEG = 60

/**
 * Parámetros configurables por el usuario antes de iniciar cada partida.
 *
 * @param alias             Nombre identificativo del jugador.
 * @param tamanoParrilla    Tamaño de la parrilla seleccionado.
 * @param controlTiempo     true si el usuario quiere límite de tiempo.
 * @param tiempoMaximoSeg   Tiempo máximo en segundos (solo relevante si [controlTiempo] = true).
 */
data class ConfiguracionPartida(
    val alias: String = "Jugador",
    val tamanoParrilla: TamanoParrilla = TamanoParrilla.MEDIANA,
    val controlTiempo: Boolean = false,
    val tiempoMaximoSeg: Int = TIEMPO_MAXIMO_DEFECTO_SEG
) {
    val filas: Int    get() = tamanoParrilla.filas
    val columnas: Int get() = tamanoParrilla.columnas

    /**
     * Validaciones básicas de la configuración.
     * Devuelve null si es válida, o un mensaje de error si no lo es.
     */
    fun validar(): String? {
        if (alias.isBlank()) return "El alias no puede estar vacío."
        if (controlTiempo && tiempoMaximoSeg <= 0)
            return "El tiempo máximo debe ser mayor que 0."
        return null
    }
}