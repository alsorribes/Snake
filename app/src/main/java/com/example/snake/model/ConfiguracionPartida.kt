package com.example.snake.model

enum class TamanoParrilla(val filas: Int, val columnas: Int, val etiqueta: String) {
    PEQUENA(10, 10, "Petita (10×10)"),
    MEDIANA(15, 15, "Mitjana (15×15)"),
    GRANDE(20, 20,  "Gran (20×20)")
}

const val TIEMPO_MAXIMO_DEFECTO_SEG = 60

/**
 * Paràmetres configurables per l'usuari abans d'iniciar cada partida.
 *
 * @param alias           Nom identificatiu del jugador.
 * @param tamanoParrilla  Mida de la parrilla seleccionada.
 * @param controlTiempo   true si l'usuari vol límit de temps.
 * @param tiempoMaximoSeg Temps màxim en segons (només rellevant si [controlTiempo] = true).
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
     * Validacions bàsiques de la configuració.
     * Retorna null si és vàlida, o un missatge d'error si no ho és.
     * Nota: els missatges estan en el model perquè no té accés a Context/Resources.
     */
    fun validar(): String? {
        if (alias.isBlank()) return "L'àlies no pot estar buit."
        if (controlTiempo && tiempoMaximoSeg <= 0)
            return "El temps màxim ha de ser major que 0."
        return null
    }
}