package com.example.snake.model

/**
 * Representa la serpiente del juego.
 *
 * El primer elemento de [segmentos] es siempre la cabeza.
 * El último elemento es la cola.
 */
data class Serpiente(
    val segmentos: List<Casilla>,
    val direccion: Direccion = Direccion.DERECHA
) {
    /**
     * FIX [J]: firstOrNull() en lugar de first() para evitar crash si segmentos vacío.
     * En la práctica nunca debería ser null en una partida en curso, pero
     * la propiedad es segura por construcción.
     */
    val cabeza: Casilla? get() = segmentos.firstOrNull()

    val longitud: Int get() = segmentos.size

    /**
     * Mueve la serpiente un paso en [nuevaDireccion].
     * Si [crecer] es true, no elimina la cola (la serpiente crece).
     * Retorna null si la cabeza actual es null (estado inválido).
     */
    fun mover(nuevaDireccion: Direccion, crecer: Boolean = false): Serpiente {
        val cabezaActual = cabeza ?: return this  // estado inválido, no mover

        val direccionFinal =
            if (nuevaDireccion.esOpuesta(direccion)) direccion else nuevaDireccion

        val nuevaCabeza = cabezaActual.mover(direccionFinal)

        val nuevosSegmentos = if (crecer) {
            listOf(nuevaCabeza) + segmentos
        } else {
            listOf(nuevaCabeza) + segmentos.dropLast(1)
        }

        return copy(segmentos = nuevosSegmentos, direccion = direccionFinal)
    }

    /**
     * Comprueba si la cabeza choca con algún segmento del cuerpo.
     * FIX [J]: seguro con cabeza nullable.
     */
    fun colisionaConsigaMisma(): Boolean {
        val cab = cabeza ?: return false
        return segmentos.drop(1).contains(cab)
    }

    /**
     * Comprueba si la cabeza está fuera de los límites de la parrilla.
     * FIX [J]: seguro con cabeza nullable → si no hay cabeza, es fuera de rango.
     */
    fun estaFueraDeRango(filas: Int, columnas: Int): Boolean {
        return cabeza?.estaEnRango(filas, columnas) != true
    }

    fun ocupaCasilla(casilla: Casilla): Boolean = segmentos.contains(casilla)

    companion object {
        fun inicial(filas: Int, columnas: Int): Serpiente {
            val filaCentro = filas / 2
            val colCentro  = columnas / 2
            return Serpiente(
                segmentos = listOf(
                    Casilla(filaCentro, colCentro),
                    Casilla(filaCentro, colCentro - 1),
                    Casilla(filaCentro, colCentro - 2)
                ),
                direccion = Direccion.DERECHA
            )
        }
    }
}