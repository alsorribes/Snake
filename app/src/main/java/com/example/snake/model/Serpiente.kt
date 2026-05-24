package com.example.snake.model


data class Serpiente(
    val segmentos: List<Casilla>,
    val direccion: Direccion = Direccion.DERECHA
) {
    val cabeza: Casilla? get() = segmentos.firstOrNull()

    val longitud: Int get() = segmentos.size


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


    fun colisionaConsigaMisma(): Boolean {
        val cab = cabeza ?: return false
        return segmentos.drop(1).contains(cab)
    }


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