package com.example.snakegame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.snakegame.navigation.AppNavigation
import com.example.snakegame.ui.screens.AyudaScreen
import com.example.snakegame.ui.screens.ConfiguracionScreen
import com.example.snakegame.ui.screens.JuegoScreen
import com.example.snakegame.ui.screens.MenuPrincipalScreen
import com.example.snakegame.ui.screens.ResultadosScreen
import com.example.snakegame.ui.theme.SnakeGameTheme
import com.example.snakegame.viewmodel.GameViewModel
import com.example.snakegame.viewmodel.Pantalla

/**
 * Única Activity de la app (arquitectura single-Activity con Compose).
 *
 * Responsabilidades:
 *  - Obtener el ViewModel (sobrevive a rotaciones automáticamente).
 *  - Conectar AppNavigation con los Composables de cada pantalla.
 *  - Gestionar el Intent de email (requiere contexto de Activity).
 *  - Cerrar la app de forma limpia.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnakeGameTheme {
                val uiState by viewModel.uiState.collectAsState()

                AppNavigation(
                    viewModel = viewModel,
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
                            onEmpezar = { config -> viewModel.iniciarPartida(config) },
                            onVolver  = { viewModel.navegarA(Pantalla.MENU_PRINCIPAL) }
                        )
                    },

                    juegoContent = {
                        JuegoScreen(
                            uiState = uiState,
                            onCambiarDireccion = { dir -> viewModel.cambiarDireccion(dir) },
                            onTogglePausa      = { viewModel.togglePausa() }
                        )
                    },

                    resultadosContent = {
                        ResultadosScreen(
                            uiState = uiState,
                            onEnviarEmail = { email ->
                                enviarEmail(
                                    destinatario = email,
                                    asunto = uiState.log?.generarAsuntoEmail() ?: "",
                                    cuerpo = uiState.log?.generarTexto() ?: ""
                                )
                            },
                            onNuevaPartida = { viewModel.nuevaPartida() },
                            onSalir        = { finalizarApp() },
                            onEmailCambiado = { viewModel.actualizarEmailDestinatario(it) }
                        )
                    }
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Intent de email
    // -------------------------------------------------------------------------

    /**
     * Abre el cliente de correo del sistema con los datos del log pre-rellenos.
     * Cumple el requisito: asunto = "Log – fecha y hora", cuerpo = log de partida.
     */
    private fun enviarEmail(destinatario: String, asunto: String, cuerpo: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"               // Filtra solo apps de email
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
        }
        // Usamos chooser para que el usuario elija la app de email
        startActivity(Intent.createChooser(intent, "Enviar log por email"))
    }

    // -------------------------------------------------------------------------
    // Salida limpia
    // -------------------------------------------------------------------------

    /**
     * Finaliza la app de forma limpia:
     *  - Cancela jobs del ViewModel.
     *  - Cierra la Activity (finishAffinity elimina toda la tarea del backstack).
     */
    private fun finalizarApp() {
        viewModel.salir()
        finishAffinity()  // Garantiza que no queda ninguna Activity en el backstack
    }
}