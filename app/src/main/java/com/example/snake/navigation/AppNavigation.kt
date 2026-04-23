package com.example.snake.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.snake.viewmodel.GameViewModel
import com.example.snake.viewmodel.Pantalla

// ─── Rutas de navegación ────────────────────────────────────────────────────
// Se definen como constantes para evitar strings hardcoded en toda la app.
object Rutas {
    const val MENU_PRINCIPAL = "menu_principal"
    const val AYUDA          = "ayuda"
    const val CONFIGURACION  = "configuracion"
    const val JUEGO          = "juego"
    const val RESULTADOS     = "resultados"
}

/**
 * Punto de entrada de la navegación de la app.
 *
 * En lugar de usar NavHost con rutas de strings (lo cual complicaría
 * la gestión del backstack y del ViewModel compartido), se usa un
 * enfoque basado en el estado del ViewModel ([Pantalla]).
 *
 * Esto garantiza que:
 *  - El estado del juego sobrevive a rotaciones (ViewModel).
 *  - No quedan activities en el backstack (requisito del enunciado).
 *  - La navegación es coherente con el mapa definido en la práctica.
 *
 * Los parámetros de las funciones Composable de cada pantalla se inyectan
 * desde aquí para desacoplar la UI de la lógica.
 *
 * @param viewModel       ViewModel compartido por todas las pantallas.
 * @param onSalirApp      Lambda que cierra la app completamente (llamada desde Activity).
 * @param menuPrincipalContent  Composable de la pantalla Menú Principal.
 * @param ayudaContent          Composable de la pantalla Ayuda.
 * @param configuracionContent  Composable de la pantalla Configuración.
 * @param juegoContent          Composable de la pantalla de Juego.
 * @param resultadosContent     Composable de la pantalla de Resultados.
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

    // ── Control del botón Back del sistema ──────────────────────────────────
    // Implementamos aquí toda la lógica del backstack para cumplir el requisito
    // "No debe quedar ninguna activity en la back stack"
    BackHandler {
        when (uiState.pantallaActual) {
            // Desde el menú principal → salir de la app
            Pantalla.MENU_PRINCIPAL -> onSalirApp()

            // Desde Ayuda → volver al menú principal
            Pantalla.AYUDA -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)

            // Desde Configuración → volver al menú principal
            Pantalla.CONFIGURACION -> viewModel.navegarA(Pantalla.MENU_PRINCIPAL)

            // Durante el juego → pausar (no se puede ir atrás para evitar perder la partida)
            Pantalla.JUEGO -> viewModel.togglePausa()

            // Desde Resultados → menú principal (no se puede volver al juego)
            Pantalla.RESULTADOS -> viewModel.nuevaPartida()
        }
    }

    // ── Renderizado de la pantalla activa ───────────────────────────────────
    when (uiState.pantallaActual) {
        Pantalla.MENU_PRINCIPAL -> menuPrincipalContent()
        Pantalla.AYUDA          -> ayudaContent()
        Pantalla.CONFIGURACION  -> configuracionContent()
        Pantalla.JUEGO          -> juegoContent()
        Pantalla.RESULTADOS     -> resultadosContent()
    }
}