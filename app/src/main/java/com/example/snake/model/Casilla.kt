package com.example.snake.model

/**
 * Representa una casilla (celda) de la parrilla del juego
 *
 * @param fila Índice de fila (0-based)
 * @param columna Índice de columna (0-based)
 */
data class Casilla(
    val fila: Int,
    val columna: Int
) {

    fun mover(direccion: Direccion): Casilla {
        return when (direccion) {
            Direccion.ARRIBA    -> copy(fila = fila - 1)
            Direccion.ABAJO     -> copy(fila = fila + 1)
            Direccion.IZQUIERDA -> copy(columna = columna - 1)
            Direccion.DERECHA   -> copy(columna = columna + 1)
        }
    }


    fun estaEnRango(filas: Int, columnas: Int): Boolean {
        return fila in 0 until filas && columna in 0 until columnas
    }
}


enum class Direccion {
    ARRIBA, ABAJO, IZQUIERDA, DERECHA;

    fun esOpuesta(otra: Direccion): Boolean {
        return (this == ARRIBA && otra == ABAJO)
                || (this == ABAJO && otra == ARRIBA)
                || (this == IZQUIERDA && otra == DERECHA)
                || (this == DERECHA && otra == IZQUIERDA)
    }
}