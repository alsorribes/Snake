package com.example.snake.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.snake.viewmodel.GameViewModel
import com.example.snake.viewmodel.Pantalla

@Composable
fun AppNavigation(
    viewModel: GameViewModel,
    onSalirApp: () -> Unit,
    menuPrincipalContent: @Composable () -> Unit,
    ayudaContent: @Composable () -> Unit,
    configuracionContent: @Composable () -> Unit,
    juegoContent: @Composable () -> Unit,
    resultadosContent: @Composable () -> Unit,
    historialContent: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        when (uiState.pantallaActual) {
            Pantalla.MENU_PRINCIPAL -> onSalirApp()
            Pantalla.AYUDA          -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)
            Pantalla.CONFIGURACION  -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)
            Pantalla.JUEGO          -> viewModel.togglePausa()
            Pantalla.RESULTADOS     -> viewModel.nuevaPartida()
            Pantalla.HISTORIAL      -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)
        }
    }

    when (uiState.pantallaActual) {
        Pantalla.MENU_PRINCIPAL -> menuPrincipalContent()
        Pantalla.AYUDA          -> ayudaContent()
        Pantalla.CONFIGURACION  -> configuracionContent()
        Pantalla.JUEGO          -> juegoContent()
        Pantalla.RESULTADOS     -> resultadosContent()
        Pantalla.HISTORIAL      -> historialContent()
    }
}