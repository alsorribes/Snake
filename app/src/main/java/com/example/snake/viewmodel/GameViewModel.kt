package com.example.snake.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snake.model.ConfiguracionPartida
import com.example.snake.model.Direccion
import com.example.snake.model.EstadoJuego
import com.example.snake.model.LogPartida
import com.example.snake.model.Partida
import com.example.snake.model.ResultadoPartida
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

// Intervalo de cada tick del juego en milisegundos (velocidad de la serpiente)
private const val TICK_JUEGO_MS = 300L
// Intervalo del tick de tiempo (1 segundo)
private const val TICK_TIEMPO_MS = 1000L

/**
 * ViewModel principal del juego.
 *
 * Sobrevive a los cambios de configuración (rotaciones de pantalla),
 * por lo que todo el estado del juego se mantiene aquí.
 *
 * Expone [uiState] como StateFlow para que los Composables lo observen.
 */
class GameViewModel : ViewModel() {

    // -------------------------------------------------------------------------
    // Estado observable
    // -------------------------------------------------------------------------

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Dirección pendiente solicitada por el usuario (se aplica en el próximo tick)
    private var direccionPendiente: Direccion = Direccion.DERECHA

    // Jobs de las corrutinas del bucle del juego y del contador de tiempo
    private var jobJuego: Job? = null
    private var jobTiempo: Job? = null

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Inicia una nueva partida con la configuración dada.
     * Cancela cualquier partida en curso.
     */
    fun iniciarPartida(config: ConfiguracionPartida) {
        cancelarJobs()

        val partida = Partida.nueva(config)
        direccionPendiente = partida.serpiente.direccion

        _uiState.update {
            GameUiState(
                partida = partida,
                log = LogPartida.desdeConfiguracion(config),
                pantallaActual = Pantalla.JUEGO
            )
        }

        iniciarBucleJuego()
        if (config.controlTiempo) iniciarBucleTiempo()
    }

    /**
     * Llamado por el Composable cuando el usuario cambia de dirección
     * (botón de control o gesto swipe).
     */
    fun cambiarDireccion(nuevaDireccion: Direccion) {
        // Ignorar si es opuesta a la dirección actual (evita giro de 180°)
        val partida = _uiState.value.partida ?: return
        if (!nuevaDireccion.esOpuesta(partida.serpiente.direccion)) {
            direccionPendiente = nuevaDireccion
        }
    }

    /**
     * Pausa o reanuda el bucle del juego.
     */
    fun togglePausa() {
        val enPausa = _uiState.value.enPausa
        if (enPausa) {
            reanudar()
        } else {
            pausar()
        }
    }

    /**
     * Navega a la pantalla de configuración para una nueva partida.
     */
    fun nuevaPartida() {
        cancelarJobs()
        _uiState.update { GameUiState(pantallaActual = Pantalla.CONFIGURACION) }
    }

    /**
     * Salida limpia de la app (cancela jobs).
     */
    fun salir() {
        cancelarJobs()
    }

    /**
     * Actualiza el email destinatario en el log (editable desde la pantalla de Resultados).
     */
    fun actualizarEmailDestinatario(email: String) {
        _uiState.update { it.copy(emailDestinatario = email) }
    }

    /**
     * Navega a una pantalla concreta del mapa de navegación.
     * Cancela los jobs si se sale del juego.
     */
    fun navegarA(pantalla: Pantalla) {
        if (pantalla != Pantalla.JUEGO) cancelarJobs()
        _uiState.update { it.copy(pantallaActual = pantalla, mensajeError = null) }
    }

    // -------------------------------------------------------------------------
    // Lógica interna
    // -------------------------------------------------------------------------

    /** Bucle principal: avanza la serpiente cada [TICK_JUEGO_MS] ms. */
    private fun iniciarBucleJuego() {
        jobJuego = viewModelScope.launch {
            while (true) {
                delay(TICK_JUEGO_MS)
                avanzarTick()
            }
        }
    }

    /** Bucle de tiempo: decrementa el contador cada segundo. */
    private fun iniciarBucleTiempo() {
        jobTiempo = viewModelScope.launch {
            while (true) {
                delay(TICK_TIEMPO_MS)
                aplicarTickTiempo()
            }
        }
    }

    /** Aplica un tick de movimiento al estado actual de la partida. */
    private fun avanzarTick() {
        val estado = _uiState.value
        val partida = estado.partida ?: return
        if (partida.haTerminado || estado.enPausa) return

        val nuevaPartida = partida.tick(direccionPendiente)
        _uiState.update { it.copy(partida = nuevaPartida) }

        if (nuevaPartida.haTerminado) {
            finalizarPartida(nuevaPartida)
        }
    }

    /** Aplica un tick de tiempo al estado actual. */
    private fun aplicarTickTiempo() {
        val estado = _uiState.value
        val partida = estado.partida ?: return
        if (partida.haTerminado || estado.enPausa) return

        val nuevaPartida = partida.tickTiempo()
        _uiState.update { it.copy(partida = nuevaPartida) }

        if (nuevaPartida.haTerminado) {
            finalizarPartida(nuevaPartida)
        }
    }

    /** Cuando la partida termina, cancela los jobs y construye el log final. */
    private fun finalizarPartida(partida: Partida) {
        cancelarJobs()

        val resultado = when (partida.estado) {
            EstadoJuego.GANADA           -> ResultadoPartida.GANADA
            EstadoJuego.PERDIDA_COLISION -> ResultadoPartida.PERDIDA_COLISION
            EstadoJuego.PERDIDA_TIEMPO   -> ResultadoPartida.PERDIDA_TIEMPO
            EstadoJuego.EN_CURSO         -> return // No debería llegar aquí
        }

        val tiempoTranscurrido = partida.tiempoTranscurridoSeg
        val tiempoSobrante = if (partida.config.controlTiempo)
            partida.tiempoRestanteSeg else 0

        val logFinal = _uiState.value.log?.copy(
            resultado = resultado,
            tiempoTotalSeg = tiempoTranscurrido,
            tiempoSobranteSeg = tiempoSobrante,
            manzanasComidas = partida.manzanasComidas,
            longitudFinal = partida.serpiente.longitud,
            fechaHoraFin = Date()
        )

        _uiState.update {
            it.copy(
                partida = partida,
                log = logFinal,
                pantallaActual = Pantalla.RESULTADOS
            )
        }
    }

    private fun pausar() {
        _uiState.update { it.copy(enPausa = true) }
        cancelarJobs()
    }

    private fun reanudar() {
        val config = _uiState.value.partida?.config ?: return
        _uiState.update { it.copy(enPausa = false) }
        iniciarBucleJuego()
        if (config.controlTiempo) iniciarBucleTiempo()
    }

    private fun cancelarJobs() {
        jobJuego?.cancel()
        jobTiempo?.cancel()
        jobJuego = null
        jobTiempo = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelarJobs()
    }
}

// -------------------------------------------------------------------------
// Estado de la UI
// -------------------------------------------------------------------------

/**
 * Clase de datos que representa el estado completo de la UI.
 * Es la única fuente de verdad que observan los Composables.
 */
data class GameUiState(
    val partida: Partida? = null,
    val log: LogPartida? = null,
    val pantallaActual: Pantalla = Pantalla.MENU_PRINCIPAL,
    val enPausa: Boolean = false,
    val emailDestinatario: String = "profesor@example.com",
    val mensajeError: String? = null
)

/**
 * Pantallas de la app gestionadas desde el ViewModel.
 * Refleja el mapa de navegación del enunciado.
 */
enum class Pantalla {
    MENU_PRINCIPAL,
    AYUDA,
    CONFIGURACION,
    JUEGO,
    RESULTADOS
}