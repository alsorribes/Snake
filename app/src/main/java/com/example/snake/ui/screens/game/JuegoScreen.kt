package com.example.snake.ui.screens.game

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.snake.model.Direccion
import com.example.snake.viewmodel.GameUiState
import kotlin.math.abs

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
            .pointerInput(Unit) {
                var startX = 0f
                var startY = 0f

                detectDragGestures(
                    onDragStart = { offset ->
                        startX = offset.x
                        startY = offset.y
                    },
                    onDragEnd = {
                    },
                    onDragCancel = {
                    },
                    onDrag = { change, _ ->
                        val endX = change.position.x
                        val endY = change.position.y

                        val dx = endX - startX
                        val dy = endY - startY

                        val umbralMinimo = 50f

                        if (abs(dx) > abs(dy)) {
                            if (abs(dx) > umbralMinimo) {
                                if (dx > 0) {
                                    onCambiarDireccion(Direccion.DERECHA)
                                } else {
                                    onCambiarDireccion(Direccion.IZQUIERDA)
                                }
                            }
                        } else {
                            if (abs(dy) > umbralMinimo) {
                                if (dy > 0) {
                                    onCambiarDireccion(Direccion.ABAJO)
                                } else {
                                    onCambiarDireccion(Direccion.ARRIBA)
                                }
                            }
                        }
                    }
                )
            }
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
        Text("Desliza en la pantalla para cambiar la dirección")

        Button(
            onClick = onTogglePausa,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.enPausa) "Reanudar" else "Pausar")
        }

        Button(
            onClick = { onCambiarDireccion(Direccion.ARRIBA) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Arriba")
        }

        Button(
            onClick = { onCambiarDireccion(Direccion.ABAJO) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abajo")
        }

        Button(
            onClick = { onCambiarDireccion(Direccion.IZQUIERDA) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Izquierda")
        }

        Button(
            onClick = { onCambiarDireccion(Direccion.DERECHA) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Derecha")
        }
    }
}