package com.example.snake.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snake.model.ConfiguracionPartida
import com.example.snake.model.Direccion
import com.example.snake.model.EstadoJuego
import com.example.snake.model.LogPartida
import com.example.snake.model.Partida
import com.example.snake.model.ResultadoPartida
import com.example.snake.model.TamanoParrilla
import com.example.snake.model.TIEMPO_MAXIMO_DEFECTO_SEG
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

const val EMAIL_DEFECTO = "profesor@example.com"

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var direccionPendiente: Direccion = Direccion.DERECHA

    private var jobJuego:  Job? = null
    private var jobTiempo: Job? = null

    @Volatile
    private var partidaFinalizada = false

    // ── Configuració ────────────────────────────────────────────────────────

    fun actualizarAlias(value: String) {
        _uiState.update {
            it.copy(configuracion = it.configuracion.copy(alias = value, errorAlias = null))
        }
    }

    fun actualizarTamano(tamano: TamanoParrilla) {
        _uiState.update {
            it.copy(configuracion = it.configuracion.copy(tamanoParrilla = tamano))
        }
    }

    fun actualizarControlTiempo(activo: Boolean) {
        _uiState.update {
            it.copy(configuracion = it.configuracion.copy(controlTiempo = activo, errorTiempo = null))
        }
    }

    fun actualizarTiempoTexto(texto: String) {
        _uiState.update {
            it.copy(configuracion = it.configuracion.copy(tiempoMaximoTexto = texto, errorTiempo = null))
        }
    }

    /** Valida la configuració i, si és correcta, inicia la partida. */
    fun iniciarPartidaDesdeConfiguracion() {
        val cfg = _uiState.value.configuracion

        if (cfg.alias.isBlank()) {
            _uiState.update {
                it.copy(configuracion = it.configuracion.copy(errorAlias = ERROR_ALIAS_BUIT))
            }
            return
        }

        if (cfg.controlTiempo) {
            val t = cfg.tiempoMaximoTexto.toIntOrNull()
            if (t == null || t <= 0) {
                _uiState.update {
                    it.copy(configuracion = it.configuracion.copy(errorTiempo = ERROR_TIEMPO_INVALIDO))
                }
                return
            }
        }

        val config = ConfiguracionPartida(
            alias           = cfg.alias,
            tamanoParrilla  = cfg.tamanoParrilla,
            controlTiempo   = cfg.controlTiempo,
            tiempoMaximoSeg = cfg.tiempoMaximoTexto.toIntOrNull() ?: TIEMPO_MAXIMO_DEFECTO_SEG
        )
        iniciarPartida(config)
    }

    // ── Joc ─────────────────────────────────────────────────────────────────

    private fun iniciarPartida(config: ConfiguracionPartida) {
        cancelarJobs()
        partidaFinalizada = false

        val partida = Partida.nueva(config)
        direccionPendiente = partida.serpiente.direccion

        _uiState.update {
            it.copy(
                partida        = partida,
                log            = LogPartida.desdeConfiguracion(config),
                finPartida     = null,
                pantallaActual = Pantalla.JUEGO
            )
        }

        iniciarBucleJuego()
        iniciarBucleTiempo()
    }

    fun cambiarDireccion(nuevaDireccion: Direccion) {
        val partida = _uiState.value.partida ?: return
        if (partida.haTerminado) return
        if (!nuevaDireccion.esOpuesta(partida.serpiente.direccion)) {
            direccionPendiente = nuevaDireccion
        }
    }

    fun togglePausa() {
        val partida = _uiState.value.partida
        if (partida?.haTerminado == true) return
        if (_uiState.value.enPausa) reanudar() else pausar()
    }

    fun nuevaPartida() {
        cancelarJobs()
        _uiState.update { it.copy(pantallaActual = Pantalla.CONFIGURACION) }
    }

    fun salir() {
        cancelarJobs()
    }

    fun actualizarEmailDestinatario(email: String) {
        _uiState.update { it.copy(emailDestinatario = email) }
    }

    fun irAResultados() {
        _uiState.update {
            it.copy(finPartida = null, pantallaActual = Pantalla.RESULTADOS)
        }
    }

    fun navegarA(pantalla: Pantalla) {
        if (pantalla != Pantalla.JUEGO) cancelarJobs()
        _uiState.update { it.copy(pantallaActual = pantalla) }
    }

    // ── Bucles interns ───────────────────────────────────────────────────────

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
        val estado  = _uiState.value
        val partida = estado.partida ?: return
        if (partida.haTerminado || estado.enPausa) return

        val nuevaPartida = partida.tick(direccionPendiente)
        _uiState.update { it.copy(partida = nuevaPartida) }

        if (nuevaPartida.haTerminado) finalizarPartida(nuevaPartida)
    }

    private fun aplicarTickTiempo() {
        val estado  = _uiState.value
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

        val tiempoSobrante =
            if (partida.config.controlTiempo) partida.tiempoRestanteSeg else 0

        val logFinal = _uiState.value.log?.copy(
            resultado         = resultado,
            tiempoTotalSeg    = partida.tiempoTranscurridoSeg,
            tiempoSobranteSeg = tiempoSobrante,
            manzanasComidas   = partida.manzanasComidas,
            longitudFinal     = partida.serpiente.longitud,
            fechaHoraFin      = Date()
        )

        _uiState.update {
            it.copy(
                partida    = partida,
                log        = logFinal,
                finPartida = resultado
            )
        }
    }

    private fun pausar() {
        _uiState.update { it.copy(enPausa = true) }
        cancelarJobs()
    }

    private fun reanudar() {
        val partida = _uiState.value.partida ?: return
        if (partida.haTerminado) return
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

    companion object {
        // Missatges d'error interns (no depenen de Context)
        const val ERROR_ALIAS_BUIT      = "alias_buit"
        const val ERROR_TIEMPO_INVALIDO = "tiempo_invalido"
    }
}

// ── UI State ─────────────────────────────────────────────────────────────────

data class GameUiState(
    val partida:          Partida?              = null,
    val log:              LogPartida?           = null,
    val configuracion:    ConfiguracionUiState  = ConfiguracionUiState(),
    val pantallaActual:   Pantalla              = Pantalla.MENU_PRINCIPAL,
    val enPausa:          Boolean               = false,
    val emailDestinatario: String               = EMAIL_DEFECTO,
    val finPartida:       ResultadoPartida?     = null
)

data class ConfiguracionUiState(
    val alias:            String        = "",
    val tamanoParrilla:   TamanoParrilla = TamanoParrilla.MEDIANA,
    val controlTiempo:    Boolean       = false,
    val tiempoMaximoTexto: String       = TIEMPO_MAXIMO_DEFECTO_SEG.toString(),
    val errorAlias:       String?       = null,
    val errorTiempo:      String?       = null
)

enum class Pantalla {
    MENU_PRINCIPAL, AYUDA, CONFIGURACION, JUEGO, RESULTADOS
}