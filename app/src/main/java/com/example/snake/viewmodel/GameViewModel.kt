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

private const val TICK_JUEGO_MS  = 300L
private const val TICK_TIEMPO_MS = 1000L
// Tiempo que se muestra el mensaje de Game Over antes de navegar a Resultados
private const val DELAY_GAME_OVER_MS = 800L

const val EMAIL_DEFECTO = "profesor@example.com"

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var direccionPendiente: Direccion = Direccion.DERECHA
    private var jobJuego: Job? = null
    private var jobTiempo: Job? = null

    // FIX [1]: @Volatile garantiza visibilidad entre corrutinas en distintos threads
    @Volatile
    private var partidaFinalizada = false

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    fun iniciarPartida(config: ConfiguracionPartida) {
        cancelarJobs()
        partidaFinalizada = false

        val partida = Partida.nueva(config)
        direccionPendiente = partida.serpiente.direccion

        _uiState.update {
            GameUiState(
                partida        = partida,
                log            = LogPartida.desdeConfiguracion(config),
                pantallaActual = Pantalla.JUEGO
            )
        }

        iniciarBucleJuego()
        iniciarBucleTiempo()
    }

    fun cambiarDireccion(nuevaDireccion: Direccion) {
        val partida = _uiState.value.partida ?: return
        // FIX [2]: ignorar input si la partida ya ha terminado
        if (partida.haTerminado) return
        if (!nuevaDireccion.esOpuesta(partida.serpiente.direccion)) {
            direccionPendiente = nuevaDireccion
        }
    }

    fun togglePausa() {
        if (_uiState.value.enPausa) reanudar() else pausar()
    }

    fun nuevaPartida() {
        cancelarJobs()
        _uiState.update { GameUiState(pantallaActual = Pantalla.CONFIGURACION) }
    }

    fun salir() {
        cancelarJobs()
    }

    fun actualizarEmailDestinatario(email: String) {
        _uiState.update { it.copy(emailDestinatario = email) }
    }

    fun navegarA(pantalla: Pantalla) {
        if (pantalla != Pantalla.JUEGO) cancelarJobs()
        _uiState.update { it.copy(pantallaActual = pantalla, mensajeError = null) }
    }

    // -------------------------------------------------------------------------
    // Lógica interna
    // -------------------------------------------------------------------------

    private fun iniciarBucleJuego() {
        jobJuego = viewModelScope.launch {
            while (true) {
                delay(TICK_JUEGO_MS)
                avanzarTick()
            }
        }
    }

    private fun iniciarBucleTiempo() {
        jobTiempo = viewModelScope.launch {
            while (true) {
                delay(TICK_TIEMPO_MS)
                aplicarTickTiempo()
            }
        }
    }

    private fun avanzarTick() {
        val estado = _uiState.value
        val partida = estado.partida ?: return
        if (partida.haTerminado || estado.enPausa) return

        val nuevaPartida = partida.tick(direccionPendiente)
        _uiState.update { it.copy(partida = nuevaPartida) }

        if (nuevaPartida.haTerminado) finalizarPartida(nuevaPartida)
    }

    private fun aplicarTickTiempo() {
        val estado = _uiState.value
        val partida = estado.partida ?: return
        if (partida.haTerminado || estado.enPausa) return

        val nuevaPartida = partida.tickTiempo()
        _uiState.update { it.copy(partida = nuevaPartida) }

        if (nuevaPartida.haTerminado) finalizarPartida(nuevaPartida)
    }

    private fun finalizarPartida(partida: Partida) {
        if (partidaFinalizada) return
        partidaFinalizada = true

        cancelarJobs()

        val resultado = when (partida.estado) {
            EstadoJuego.GANADA           -> ResultadoPartida.GANADA
            EstadoJuego.PERDIDA_COLISION -> ResultadoPartida.PERDIDA_COLISION
            EstadoJuego.PERDIDA_TIEMPO   -> ResultadoPartida.PERDIDA_TIEMPO
            EstadoJuego.EN_CURSO         -> return
        }

        // FIX [8]: mensaje de feedback visible en el estado de la UI
        val mensajeFin = when (resultado) {
            ResultadoPartida.GANADA           -> "🏆 ¡Has ganado!"
            ResultadoPartida.PERDIDA_COLISION -> "💀 Game Over — La serp ha xocat"
            ResultadoPartida.PERDIDA_TIEMPO   -> "⏱ Has esgotat el temps!"
        }

        val tiempoSobrante = if (partida.config.controlTiempo) partida.tiempoRestanteSeg else 0

        val logFinal = _uiState.value.log?.copy(
            resultado         = resultado,
            tiempoTotalSeg    = partida.tiempoTranscurridoSeg,
            tiempoSobranteSeg = tiempoSobrante,
            manzanasComidas   = partida.manzanasComidas,
            longitudFinal     = partida.serpiente.longitud,
            fechaHoraFin      = Date()
        )

        // Mostrar feedback brevemente antes de navegar a Resultados
        _uiState.update {
            it.copy(
                partida        = partida,
                log            = logFinal,
                mensajeGameOver = mensajeFin
            )
        }

        // FIX [8]: delay para que el usuario vea el mensaje antes de navegar
        viewModelScope.launch {
            delay(DELAY_GAME_OVER_MS)
            _uiState.update {
                it.copy(
                    mensajeGameOver = null,
                    pantallaActual  = Pantalla.RESULTADOS
                )
            }
        }
    }

    private fun pausar() {
        _uiState.update { it.copy(enPausa = true) }
        cancelarJobs()
    }

    private fun reanudar() {
        _uiState.update { it.copy(enPausa = false) }
        iniciarBucleJuego()
        iniciarBucleTiempo()
    }

    private fun cancelarJobs() {
        jobJuego?.cancel();  jobJuego  = null
        jobTiempo?.cancel(); jobTiempo = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelarJobs()
    }
}

// ─── UI State ────────────────────────────────────────────────────────────────

data class GameUiState(
    val partida: Partida? = null,
    val log: LogPartida? = null,
    val pantallaActual: Pantalla = Pantalla.MENU_PRINCIPAL,
    val enPausa: Boolean = false,
    val emailDestinatario: String = EMAIL_DEFECTO,
    val mensajeError: String? = null,
    // FIX [8]: campo para el mensaje de fin de partida (Game Over / Victoria)
    val mensajeGameOver: String? = null
)

enum class Pantalla {
    MENU_PRINCIPAL,
    AYUDA,
    CONFIGURACION,
    JUEGO,
    RESULTADOS
}