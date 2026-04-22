package com.example.snake.ui.screens.results

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.snake.viewmodel.GameUiState

@Composable
fun ResultadosScreen(
    uiState: GameUiState,
    onEnviarEmail: (String) -> Unit,
    onNuevaPartida: () -> Unit,
    onSalir: () -> Unit,
    onEmailCambiado: (String) -> Unit
) {
    Text("Pantalla de resultados")
}