package com.example.snake.model

data class Partida(
    val config: ConfiguracionPartida,
    val serpiente: Serpiente,
    val manzana: Casilla,
    val manzanasComidas: Int = 0,
    val tiempoRestanteSeg: Int = 0,
    val tiempoTranscurridoSeg: Int = 0,
    val estado: EstadoJuego = EstadoJuego.EN_CURSO
) {
    val filas: Int    get() = config.filas
    val columnas: Int get() = config.columnas

    val haTerminado: Boolean get() = estado != EstadoJuego.EN_CURSO

    val tiempoParaMostrar: Int
        get() = if (config.controlTiempo) tiempoRestanteSeg else tiempoTranscurridoSeg

    fun tick(direccion: Direccion): Partida {
        if (haTerminado) return this

        val cabezaActual = serpiente.cabeza ?: return copy(estado = EstadoJuego.PERDIDA_COLISION)

        val direccionEfectiva =
            if (direccion.esOpuesta(serpiente.direccion)) serpiente.direccion else direccion

        val comeManzana = cabezaActual.mover(direccionEfectiva) == manzana

        val serpienteMovida = serpiente.mover(direccion, crecer = comeManzana)

        if (serpienteMovida.segmentos.isEmpty()) {
            return copy(estado = EstadoJuego.PERDIDA_COLISION)
        }

        if (serpienteMovida.estaFueraDeRango(filas, columnas)
            || serpienteMovida.colisionaConsigaMisma()
        ) {
            return copy(serpiente = serpienteMovida, estado = EstadoJuego.PERDIDA_COLISION)
        }

        val nuevasManzanasComidas = manzanasComidas + if (comeManzana) 1 else 0

        if (serpienteMovida.longitud >= filas * columnas) {
            return copy(
                serpiente = serpienteMovida,
                manzanasComidas = nuevasManzanasComidas,
                estado = EstadoJuego.GANADA
            )
        }

        val nuevaManzana = if (comeManzana) {
            generarManzana(serpienteMovida) ?: return copy(
                serpiente = serpienteMovida,
                manzanasComidas = nuevasManzanasComidas,
                estado = EstadoJuego.GANADA
            )
        } else manzana

        return copy(
            serpiente = serpienteMovida,
            manzana = nuevaManzana,
            manzanasComidas = nuevasManzanasComidas,
            estado = EstadoJuego.EN_CURSO
        )
    }

    fun tickTiempo(): Partida {
        if (haTerminado) return this

        val nuevoTiempoTranscurrido = tiempoTranscurridoSeg + 1

        return if (config.controlTiempo) {
            val nuevoTiempoRestante = (tiempoRestanteSeg - 1).coerceAtLeast(0)
            val nuevoEstado = if (nuevoTiempoRestante == 0) EstadoJuego.PERDIDA_TIEMPO
            else EstadoJuego.EN_CURSO
            copy(
                tiempoRestanteSeg     = nuevoTiempoRestante,
                tiempoTranscurridoSeg = nuevoTiempoTranscurrido,
                estado                = nuevoEstado
            )
        } else {
            copy(tiempoTranscurridoSeg = nuevoTiempoTranscurrido)
        }
    }

    private fun generarManzana(serpienteActual: Serpiente): Casilla? {
        val libres = (0 until filas).flatMap { f ->
            (0 until columnas).map { c -> Casilla(f, c) }
        }.filter { !serpienteActual.ocupaCasilla(it) }
        return if (libres.isNotEmpty()) libres.random() else null
    }

    companion object {
        fun nueva(config: ConfiguracionPartida): Partida {
            val serpiente = Serpiente.inicial(config.filas, config.columnas)
            val libres = (0 until config.filas).flatMap { f ->
                (0 until config.columnas).map { c -> Casilla(f, c) }
            }.filter { !serpiente.ocupaCasilla(it) }
            val manzana = if (libres.isNotEmpty()) libres.random() else Casilla(0, 0)
            return Partida(
                config                = config,
                serpiente             = serpiente,
                manzana               = manzana,
                tiempoRestanteSeg     = config.tiempoMaximoSeg,
                tiempoTranscurridoSeg = 0,
                estado                = EstadoJuego.EN_CURSO
            )
        }
    }
}

enum class EstadoJuego { EN_CURSO, GANADA, PERDIDA_COLISION, PERDIDA_TIEMPO }