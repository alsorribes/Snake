package com.example.snake.model

/**
 * Estado completo de una partida en un instante dado.
 *
 * Es inmutable: cada cambio produce una nueva instancia (patrón funcional),
 * lo que facilita la integración con StateFlow y Compose.
 *
 * @param config            Configuración con la que se inició la partida.
 * @param serpiente         Estado actual de la serpiente.
 * @param manzana           Posición actual de la manzana en la parrilla.
 * @param manzanasComidas   Número de manzanas que ha comido la serpiente.
 * @param tiempoRestanteSeg Tiempo restante en segundos (solo relevante si config.controlTiempo).
 * @param estado            Estado actual del juego (en curso, ganada, perdida…).
 */
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

    /** true si la partida ha terminado por cualquier motivo. */
    val haTerminado: Boolean
        get() = estado != EstadoJuego.EN_CURSO

    /**
     * Avanza la partida un tick:
     * 1. Mueve la serpiente en la dirección solicitada.
     * 2. Comprueba colisiones (pared / cuerpo).
     * 3. Comprueba si ha comido la manzana.
     * 4. Genera nueva manzana si es necesario.
     * 5. Comprueba condición de victoria (parrilla llena).
     *
     * @param direccion Dirección deseada para este tick.
     * @return Nueva [Partida] con el estado actualizado.
     */
    fun tick(direccion: Direccion): Partida {
        if (haTerminado) return this

        val comeManzana = serpiente.cabeza.mover(
            if (direccion.esOpuesta(serpiente.direccion)) serpiente.direccion else direccion
        ) == manzana

        val serpienteMovida = serpiente.mover(direccion, crecer = comeManzana)

        // --- Comprobar colisiones ---
        if (serpienteMovida.estaFueraDeRango(filas, columnas)
            || serpienteMovida.colisionaConsigaMisma()
        ) {
            return copy(
                serpiente = serpienteMovida,
                estado = EstadoJuego.PERDIDA_COLISION
            )
        }

        val nuevasManzanasComidas = manzanasComidas + if (comeManzana) 1 else 0

        // --- Comprobar victoria (serpiente ocupa toda la parrilla) ---
        if (serpienteMovida.longitud == filas * columnas) {
            return copy(
                serpiente = serpienteMovida,
                manzanasComidas = nuevasManzanasComidas,
                estado = EstadoJuego.GANADA
            )
        }

        // --- Generar nueva manzana si fue comida ---
        val nuevaManzana = if (comeManzana) {
            generarManzana(serpienteMovida)
        } else {
            manzana
        }

        return copy(
            serpiente = serpienteMovida,
            manzana = nuevaManzana,
            manzanasComidas = nuevasManzanasComidas,
            estado = EstadoJuego.EN_CURSO
        )
    }

    /**
     * Aplica el tick de tiempo: decrementa el tiempo restante.
     * Si llega a 0 marca la partida como perdida por tiempo.
     */
    fun tickTiempo(): Partida {
        if (!config.controlTiempo || haTerminado) return this

        val nuevoTiempoRestante = (tiempoRestanteSeg - 1).coerceAtLeast(0)
        val nuevoTiempoTranscurrido = tiempoTranscurridoSeg + 1
        val nuevoEstado = if (nuevoTiempoRestante == 0) EstadoJuego.PERDIDA_TIEMPO
        else EstadoJuego.EN_CURSO

        return copy(
            tiempoRestanteSeg = nuevoTiempoRestante,
            tiempoTranscurridoSeg = nuevoTiempoTranscurrido,
            estado = nuevoEstado
        )
    }

    /**
     * Genera una casilla aleatoria libre (no ocupada por la serpiente).
     */
    private fun generarManzana(serpienteActual: Serpiente): Casilla {
        val casillasLibres = (0 until filas).flatMap { f ->
            (0 until columnas).map { c -> Casilla(f, c) }
        }.filter { !serpienteActual.ocupaCasilla(it) }

        return if (casillasLibres.isNotEmpty()) casillasLibres.random()
        else manzana // No debería ocurrir si la partida no ha terminado
    }

    companion object {
        /**
         * Crea una nueva partida a partir de la configuración dada.
         * Posiciona la serpiente inicial y genera la primera manzana aleatoriamente.
         */
        fun nueva(config: ConfiguracionPartida): Partida {
            val serpiente = Serpiente.inicial(config.filas, config.columnas)

            // Generar manzana en posición aleatoria no ocupada por la serpiente inicial
            val casillasLibres = (0 until config.filas).flatMap { f ->
                (0 until config.columnas).map { c -> Casilla(f, c) }
            }.filter { !serpiente.ocupaCasilla(it) }

            val manzana = casillasLibres.random()

            return Partida(
                config = config,
                serpiente = serpiente,
                manzana = manzana,
                tiempoRestanteSeg = config.tiempoMaximoSeg,
                tiempoTranscurridoSeg = 0,
                estado = EstadoJuego.EN_CURSO
            )
        }
    }
}

/**
 * Posibles estados del juego en un instante dado.
 */
enum class EstadoJuego {
    EN_CURSO,
    GANADA,
    PERDIDA_COLISION,
    PERDIDA_TIEMPO
}