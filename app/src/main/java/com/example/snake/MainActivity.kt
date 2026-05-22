package com.example.snake

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.snake.navigation.AppNavigation
import com.example.snake.ui.screens.help.AyudaScreen
import com.example.snake.ui.screens.config.ConfiguracionScreen
import com.example.snake.ui.screens.game.JuegoScreen
import com.example.snake.ui.screens.menu.MenuPrincipalScreen
import com.example.snake.ui.screens.results.ResultadosScreen
import com.example.snake.ui.theme.SnakeTheme
import com.example.snake.viewmodel.GameViewModel
import com.example.snake.viewmodel.Pantalla

private const val EMAIL_MIME_TYPE    = "message/rfc822"
private const val EMAIL_CHOOSER_TEXT = "Enviar log per email"

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SnakeTheme {
                val uiState by viewModel.uiState.collectAsState()

                AppNavigation(
                    viewModel  = viewModel,
                    onSalirApp = { finalizarApp() },

                    menuPrincipalContent = {
                        MenuPrincipalScreen(
                            onEmpezarPartida = { viewModel.navegarA(Pantalla.CONFIGURACION) },
                            onAyuda          = { viewModel.navegarA(Pantalla.AYUDA) },
                            onSalir          = { finalizarApp() }
                        )
                    },

                    ayudaContent = {
                        AyudaScreen(
                            onIrAlJuego = { viewModel.navegarA(Pantalla.CONFIGURACION) },
                            onVolver    = { viewModel.navegarA(Pantalla.MENU_PRINCIPAL) }
                        )
                    },

                    configuracionContent = {
                        ConfiguracionScreen(
                            configuracion         = uiState.configuracion,
                            onAliasChange         = { viewModel.actualizarAlias(it) },
                            onTamanoChange        = { viewModel.actualizarTamano(it) },
                            onControlTiempoChange = { viewModel.actualizarControlTiempo(it) },
                            onTiempoTextoChange   = { viewModel.actualizarTiempoTexto(it) },
                            onEmpezar             = { viewModel.iniciarPartidaDesdeConfiguracion() },
                            onVolver              = { viewModel.navegarA(Pantalla.MENU_PRINCIPAL) }
                        )
                    },

                    juegoContent = {
                        JuegoScreen(
                            uiState            = uiState,
                            onCambiarDireccion = { dir -> viewModel.cambiarDireccion(dir) },
                            onTogglePausa      = { viewModel.togglePausa() },
                            onIrAResultados    = { viewModel.irAResultados() }
                        )
                    },

                    resultadosContent = {
                        ResultadosScreen(
                            log               = uiState.log,
                            emailDestinatario = uiState.emailDestinatario,
                            onEnviarEmail     = { email ->
                                enviarEmail(
                                    destinatario = email,
                                    asunto       = uiState.log?.generarAsuntoEmail() ?: "",
                                    cuerpo       = uiState.log?.generarTexto() ?: ""
                                )
                            },
                            onNuevaPartida  = { viewModel.nuevaPartida() },
                            onSalir         = { finalizarApp() },
                            onEmailCambiado = { viewModel.actualizarEmailDestinatario(it) }
                        )
                    }
                )
            }
        }
    }

    private fun enviarEmail(destinatario: String, asunto: String, cuerpo: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = EMAIL_MIME_TYPE
            putExtra(Intent.EXTRA_EMAIL,   arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT,    cuerpo)
        }
        startActivity(Intent.createChooser(intent, EMAIL_CHOOSER_TEXT))
    }

    private fun finalizarApp() {
        viewModel.salir()
        finishAffinity()
    }
}