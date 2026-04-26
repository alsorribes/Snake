package com.example.snake.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.snake.viewmodel.GameViewModel
import com.example.snake.viewmodel.Pantalla

// FIX [5]: eliminado objeto Rutas con constantes de strings que nunca se usaban.
// La navegación se gestiona con el enum Pantalla del ViewModel, no con strings.

/**
 * Punto de entrada de la navegación de la app.
 *
 * Enfoque basado en el estado del ViewModel ([Pantalla]) en lugar de NavHost,
 * lo que garantiza:
 *  - Estado del juego sobrevive a rotaciones (ViewModel no se destruye).
 *  - No quedan activities en el backstack (requisito del enunciado).
 *  - Navegación coherente con el mapa de la práctica.
 */
@Composable
fun AppNavigation(
    viewModel: GameViewModel,
    onSalirApp: () -> Unit,
    menuPrincipalContent: @Composable () -> Unit,
    ayudaContent: @Composable () -> Unit,
    configuracionContent: @Composable () -> Unit,
    juegoContent: @Composable () -> Unit,
    resultadosContent: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Control del botón Back — gestión completa del backstack
    BackHandler {
        when (uiState.pantallaActual) {
            Pantalla.MENU_PRINCIPAL -> onSalirApp()
            Pantalla.AYUDA          -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)
            Pantalla.CONFIGURACION  -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)
            Pantalla.JUEGO          -> viewModel.togglePausa()
            Pantalla.RESULTADOS     -> viewModel.nuevaPartida()
        }
    }

    when (uiState.pantallaActual) {
        Pantalla.MENU_PRINCIPAL -> menuPrincipalContent()
        Pantalla.AYUDA          -> ayudaContent()
        Pantalla.CONFIGURACION  -> configuracionContent()
        Pantalla.JUEGO          -> juegoContent()
        Pantalla.RESULTADOS     -> resultadosContent()
    }
}