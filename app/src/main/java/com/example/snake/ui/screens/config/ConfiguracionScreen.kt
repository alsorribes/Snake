package com.example.snake.ui.screens.config

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.snake.model.ConfiguracionPartida

@Composable
fun ConfiguracionScreen(
    onEmpezar: (ConfiguracionPartida) -> Unit,
    onVolver: () -> Unit
) {
    Text("Pantalla de configuración")
}