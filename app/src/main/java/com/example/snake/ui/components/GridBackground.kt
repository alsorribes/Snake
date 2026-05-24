package com.example.snake.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
// FIX [E3]: usar SnakeGreen de SnakeColors en lugar de Color(0xFF4CAF50) hardcoded
import com.example.snake.ui.theme.SnakeGreen

@Composable
fun GridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellSize  = 40.dp.toPx()
        val lineColor = SnakeGreen.copy(alpha = 0.05f)

        var x = 0f
        while (x < size.width) {
            drawLine(lineColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
            x += cellSize
        }
        var y = 0f
        while (y < size.height) {
            drawLine(lineColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
            y += cellSize
        }
    }
}