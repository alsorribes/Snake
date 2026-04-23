package com.example.snake.ui.screens.game

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.snake.model.Direccion
import com.example.snake.viewmodel.GameUiState

@Composable
fun JuegoScreen(
    uiState: GameUiState,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit
) {
    Text("Pantalla de juego")
}