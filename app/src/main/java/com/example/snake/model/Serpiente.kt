package com.example.snakegame.model

/**
 * Representa la serpiente del juego.
 *
 * El primer elemento de [segmentos] es siempre la cabeza.
 * El último elemento es la cola.
 *
 * @param segmentos Lista ordenada de casillas que ocupa la serpiente.
 * @param direccion Dirección de movimiento actual.
 */
data class Serpiente(
    val segmentos: List<Casilla>,
    val direccion: Direccion = Direccion.DERECHA
) {
    /** Casilla donde está la cabeza. */
    val cabeza: Casilla get() = segmentos.first()

    /** Longitud actual de la serpiente. */
    val longitud: Int get() = segmentos.size

    /**
     * Mueve la serpiente un paso en [nuevaDireccion].
     * Si [crecer] es true, no se elimina la cola (la serpiente crece).
     * Ignora la nueva dirección si es opuesta a la actual (evita giro de 180°).
     *
     * @return Nueva instancia de [Serpiente] tras el movimiento.
     */
    fun mover(nuevaDireccion: Direccion, crecer: Boolean = false): Serpiente {
        val direccionFinal =
            if (nuevaDireccion.esOpuesta(direccion)) direccion else nuevaDireccion

        val nuevaCabeza = cabeza.mover(direccionFinal)

        val nuevosSegmentos = if (crecer) {
            listOf(nuevaCabeza) + segmentos          // Cabeza nueva + cuerpo completo
        } else {
            listOf(nuevaCabeza) + segmentos.dropLast(1) // Cabeza nueva + cuerpo sin cola
        }

        return copy(segmentos = nuevosSegmentos, direccion = direccionFinal)
    }

    /**
     * Comprueba si la cabeza choca con algún segmento del cuerpo
     * (excluyendo la propia cabeza).
     */
    fun colisionaConsigaMisma(): Boolean {
        return segmentos.drop(1).contains(cabeza)
    }

    /**
     * Comprueba si la cabeza está fuera de los límites de la parrilla.
     */
    fun estaFueraDeRango(filas: Int, columnas: Int): Boolean {
        return !cabeza.estaEnRango(filas, columnas)
    }

    /**
     * Comprueba si la serpiente ocupa una casilla concreta.
     */
    fun ocupaCasilla(casilla: Casilla): Boolean {
        return segmentos.contains(casilla)
    }

    companion object {
        /**
         * Crea una serpiente inicial centrada en la parrilla,
         * con longitud 3 y dirección hacia la derecha.
         */
        fun inicial(filas: Int, columnas: Int): Serpiente {
            val filaCentro = filas / 2
            val colCentro  = columnas / 2
            val segmentosIniciales = listOf(
                Casilla(filaCentro, colCentro),
                Casilla(filaCentro, colCentro - 1),
                Casilla(filaCentro, colCentro - 2)
            )
            return Serpiente(segmentos = segmentosIniciales, direccion = Direccion.DERECHA)
        }
    }
}