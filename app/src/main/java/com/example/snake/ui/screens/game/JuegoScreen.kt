package com.example.snake.ui.screens.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.snake.model.Direccion
import com.example.snake.viewmodel.GameUiState

@Composable
fun JuegoScreen(
    uiState: GameUiState,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit
) {
    val partida = uiState.partida

    if (partida == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("No hay partida en curso")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Juego",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Alias: ${partida.config.alias}")
        Text("Parrilla: ${partida.filas} x ${partida.columnas}")
        Text("Manzanas comidas: ${partida.manzanasComidas}")
        Text("Longitud serpiente: ${partida.serpiente.longitud}")
        Text("Tiempo restante: ${partida.tiempoRestanteSeg}")
        Text("Tiempo transcurrido: ${partida.tiempoTranscurridoSeg}")
        Text("Estado: ${partida.estado}")
        Text("Pausa: ${if (uiState.enPausa) "Sí" else "No"}")

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val filas = partida.filas
            val columnas = partida.columnas

            val cellWidth = size.width / columnas
            val cellHeight = size.height / filas

            // Fondo del tablero
            drawRect(
                color = Color(0xFF101820),
                size = size
            )

            // Cuadrícula
            for (fila in 0..filas) {
                val y = fila * cellHeight
                drawLine(
                    color = Color(0xFF2A3B47),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            for (col in 0..columnas) {
                val x = col * cellWidth
                drawLine(
                    color = Color(0xFF2A3B47),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
            }

            // Manzana
            drawRect(
                color = Color.Red,
                topLeft = Offset(
                    partida.manzana.columna * cellWidth,
                    partida.manzana.fila * cellHeight
                ),
                size = Size(cellWidth, cellHeight)
            )

            // Serpiente
            partida.serpiente.segmentos.forEachIndexed { index, casilla ->
                val color = if (index == 0) {
                    Color(0xFF4CAF50) // cabeza
                } else {
                    Color(0xFF81C784) // cuerpo
                }

                drawRect(
                    color = color,
                    topLeft = Offset(
                        casilla.columna * cellWidth,
                        casilla.fila * cellHeight
                    ),
                    size = Size(cellWidth, cellHeight)
                )
            }
        }

        Button(
            onClick = onTogglePausa,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.enPausa) "Reanudar" else "Pausar")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onCambiarDireccion(Direccion.ARRIBA) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Arriba")
            }

            Button(
                onClick = { onCambiarDireccion(Direccion.ABAJO) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Abajo")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onCambiarDireccion(Direccion.IZQUIERDA) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Izquierda")
            }

            Button(
                onClick = { onCambiarDireccion(Direccion.DERECHA) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Derecha")
            }
        }
    }
}