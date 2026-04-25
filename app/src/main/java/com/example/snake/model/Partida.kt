package com.example.snake.model

/**
 * Estado completo de una partida en un instante dado.
 *
 * Es inmutable: cada cambio produce una nueva instancia (patrón funcional),
 * lo que facilita la integración con StateFlow y Compose.
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
     * 5. Comprueba condición de victoria (parrilla llena o sin casillas libres).
     */
    fun tick(direccion: Direccion): Partida {
        if (haTerminado) return this

        val direccionEfectiva =
            if (direccion.esOpuesta(serpiente.direccion)) serpiente.direccion else direccion

        val comeManzana = serpiente.cabeza.mover(direccionEfectiva) == manzana

        val serpienteMovida = serpiente.mover(direccion, crecer = comeManzana)

        // FIX [9]: comprobar nulos/rango antes de acceder a cabeza
        if (serpienteMovida.segmentos.isEmpty()) {
            return copy(estado = EstadoJuego.PERDIDA_COLISION)
        }

        // Comprobar colisiones
        if (serpienteMovida.estaFueraDeRango(filas, columnas)
            || serpienteMovida.colisionaConsigaMisma()
        ) {
            return copy(
                serpiente = serpienteMovida,
                estado = EstadoJuego.PERDIDA_COLISION
            )
        }

        val nuevasManzanasComidas = manzanasComidas + if (comeManzana) 1 else 0

        // FIX [16]: victoria correcta cuando serpiente ocupa toda la parrilla
        if (serpienteMovida.longitud >= filas * columnas) {
            return copy(
                serpiente = serpienteMovida,
                manzanasComidas = nuevasManzanasComidas,
                estado = EstadoJuego.GANADA
            )
        }

        // Generar nueva manzana si fue comida
        val nuevaManzana = if (comeManzana) {
            generarManzana(serpienteMovida) ?: return copy(
                serpiente = serpienteMovida,
                manzanasComidas = nuevasManzanasComidas,
                estado = EstadoJuego.GANADA   // Sin casillas libres = victoria
            )
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
     * FIX [8]: tiempoTranscurridoSeg se incrementa SIEMPRE (con o sin control de tiempo)
     * para que el log muestre el tiempo real empleado.
     */
    fun tickTiempo(): Partida {
        if (haTerminado) return this

        // Siempre incrementamos el tiempo transcurrido
        val nuevoTiempoTranscurrido = tiempoTranscurridoSeg + 1

        return if (config.controlTiempo) {
            val nuevoTiempoRestante = (tiempoRestanteSeg - 1).coerceAtLeast(0)
            val nuevoEstado = if (nuevoTiempoRestante == 0) EstadoJuego.PERDIDA_TIEMPO
            else EstadoJuego.EN_CURSO
            copy(
                tiempoRestanteSeg = nuevoTiempoRestante,
                tiempoTranscurridoSeg = nuevoTiempoTranscurrido,
                estado = nuevoEstado
            )
        } else {
            copy(tiempoTranscurridoSeg = nuevoTiempoTranscurrido)
        }
    }

    /**
     * FIX [1][9]: devuelve null si no hay casillas libres (en vez de crashear con .random()).
     */
    private fun generarManzana(serpienteActual: Serpiente): Casilla? {
        val casillasLibres = (0 until filas).flatMap { f ->
            (0 until columnas).map { c -> Casilla(f, c) }
        }.filter { !serpienteActual.ocupaCasilla(it) }

        return if (casillasLibres.isNotEmpty()) casillasLibres.random() else null
    }

    companion object {
        /**
         * FIX [1][9]: comprueba que hay casillas libres antes de llamar .random().
         */
        fun nueva(config: ConfiguracionPartida): Partida {
            val serpiente = Serpiente.inicial(config.filas, config.columnas)

            val casillasLibres = (0 until config.filas).flatMap { f ->
                (0 until config.columnas).map { c -> Casilla(f, c) }
            }.filter { !serpiente.ocupaCasilla(it) }

            // En una parrilla mínima 10x10 con serpiente de 3, esto nunca será vacío,
            // pero lo protegemos igualmente
            val manzana = if (casillasLibres.isNotEmpty()) casillasLibres.random()
            else Casilla(0, 0)

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

enum class EstadoJuego {
    EN_CURSO,
    GANADA,
    PERDIDA_COLISION,
    PERDIDA_TIEMPO
}