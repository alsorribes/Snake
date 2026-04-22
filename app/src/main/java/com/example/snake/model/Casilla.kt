package com.example.snakegame.model

/**
 * Representa una casilla (celda) de la parrilla del juego.
 *
 * @param fila Índice de fila (0-based)
 * @param columna Índice de columna (0-based)
 */
data class Casilla(
    val fila: Int,
    val columna: Int
) {
    /**
     * Devuelve la casilla adyacente en la dirección indicada.
     */
    fun mover(direccion: Direccion): Casilla {
        return when (direccion) {
            Direccion.ARRIBA    -> copy(fila = fila - 1)
            Direccion.ABAJO     -> copy(fila = fila + 1)
            Direccion.IZQUIERDA -> copy(columna = columna - 1)
            Direccion.DERECHA   -> copy(columna = columna + 1)
        }
    }

    /**
     * Comprueba si la casilla está dentro de los límites de la parrilla.
     */
    fun estaEnRango(filas: Int, columnas: Int): Boolean {
        return fila in 0 until filas && columna in 0 until columnas
    }
}

/**
 * Direcciones posibles de movimiento de la serpiente.
 */
enum class Direccion {
    ARRIBA, ABAJO, IZQUIERDA, DERECHA;

    /**
     * Evita que la serpiente se mueva en dirección opuesta a la actual (giro de 180°).
     */
    fun esOpuesta(otra: Direccion): Boolean {
        return (this == ARRIBA && otra == ABAJO)
                || (this == ABAJO && otra == ARRIBA)
                || (this == IZQUIERDA && otra == DERECHA)
                || (this == DERECHA && otra == IZQUIERDA)
    }
}