package com.example.snake.ui.screens.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.model.Casilla
import com.example.snake.model.Direccion
import com.example.snake.model.Partida
import com.example.snake.model.Serpiente
import com.example.snake.ui.theme.AppleRed
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.BoardBackground
import com.example.snake.ui.theme.BoardGrid
import com.example.snake.ui.theme.BtnNeutral
import com.example.snake.ui.theme.SnakeBody
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeHead
import com.example.snake.ui.theme.TimeNoControl
import com.example.snake.ui.theme.TimeWithControl
import com.example.snake.viewmodel.GameUiState

@Composable
fun JuegoScreen(
    uiState: GameUiState,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit,
    onIrAResultados: () -> Unit
) {
    val partida = uiState.partida
    if (partida == null) {
        Box(
            Modifier.fillMaxSize().safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No hi ha partida en curs",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
        return
    }

    val esLandscape =
        LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp

    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        if (esLandscape) {
            JuegoLandscape(partida, uiState.enPausa, onCambiarDireccion, onTogglePausa)
        } else {
            JuegoPortrait(partida, uiState.enPausa, onCambiarDireccion, onTogglePausa)
        }

        if (uiState.mensajeGameOver != null) {
            FinPartidaOverlay(
                mensaje = uiState.mensajeGameOver,
                onIrAResultados = onIrAResultados
            )
        } else if (uiState.enPausa) {
            PausaOverlay(onReanudar = onTogglePausa)
        }
    }
}

@Composable
private fun FinPartidaOverlay(
    mensaje: String,
    onIrAResultados: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(BackgroundDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = mensaje,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onIrAResultados,
                colors = ButtonDefaults.buttonColors(containerColor = SnakeGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "VEURE RESULTATS",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PausaOverlay(
    onReanudar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(BackgroundDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PAUSA",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Button(
                onClick = onReanudar,
                colors = ButtonDefaults.buttonColors(containerColor = SnakeGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "▶ REANUDAR",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun JuegoPortrait(
    partida: Partida,
    enPausa: Boolean,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoardBackground)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoPartida(
            partida.config.alias,
            partida.manzanasComidas,
            partida.serpiente.longitud,
            partida.tiempoParaMostrar,
            partida.config.controlTiempo,
            enPausa
        )

        TableroJuego(
            partida.filas,
            partida.columnas,
            partida.serpiente,
            partida.manzana,
            Modifier
                .fillMaxWidth()
                .aspectRatio(partida.columnas.toFloat() / partida.filas.toFloat())
        )

        if (!enPausa) {
            BotonPausa(enPausa, onTogglePausa)
        }

        ControlesDireccion(onCambiarDireccion, tamanoBoton = 88.dp)
    }
}

@Composable
private fun JuegoLandscape(
    partida: Partida,
    enPausa: Boolean,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BoardBackground)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TableroJuego(
            partida.filas,
            partida.columnas,
            partida.serpiente,
            partida.manzana,
            Modifier
                .fillMaxHeight()
                .aspectRatio(partida.columnas.toFloat() / partida.filas.toFloat())
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoPartida(
                partida.config.alias,
                partida.manzanasComidas,
                partida.serpiente.longitud,
                partida.tiempoParaMostrar,
                partida.config.controlTiempo,
                enPausa
            )

            if (!enPausa) {
                BotonPausa(enPausa, onTogglePausa, Modifier.fillMaxWidth())
            }

            ControlesDireccion(onCambiarDireccion, tamanoBoton = 64.dp)
        }
    }
}

@Composable
private fun InfoPartida(
    alias: String,
    manzanasComidas: Int,
    longitud: Int,
    tiempoMostrar: Int,
    controlTiempo: Boolean,
    enPausa: Boolean
) {
    val colorTiempo = if (controlTiempo) TimeWithControl else TimeNoControl
    val labelTiempo = if (controlTiempo) "⏱ $tiempoMostrar s" else "⏱ $tiempoMostrar s"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = BackgroundDark.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "👤 $alias",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "🍎 $manzanasComidas  •  Longitud: $longitud",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = labelTiempo,
                    color = colorTiempo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (enPausa) {
                    Text(
                        text = "PAUSADO",
                        color = Color.Yellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TableroJuego(
    filas: Int,
    columnas: Int,
    serpiente: Serpiente,
    manzana: Casilla,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(BackgroundDark, RoundedCornerShape(18.dp))
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(BoardBackground, RoundedCornerShape(12.dp))
        ) {
            val cellW = size.width / columnas
            val cellH = size.height / filas

            drawRect(color = BoardBackground, size = size)

            for (f in 0..filas) {
                drawLine(
                    color = BoardGrid,
                    start = Offset(0f, f * cellH),
                    end = Offset(size.width, f * cellH),
                    strokeWidth = 1f
                )
            }

            for (c in 0..columnas) {
                drawLine(
                    color = BoardGrid,
                    start = Offset(c * cellW, 0f),
                    end = Offset(c * cellW, size.height),
                    strokeWidth = 1f
                )
            }

            drawRect(
                color = AppleRed,
                topLeft = Offset(
                    manzana.columna * cellW + 3f,
                    manzana.fila * cellH + 3f
                ),
                size = Size(cellW - 6f, cellH - 6f)
            )

            serpiente.segmentos.forEachIndexed { index, casilla ->
                val color = if (index == 0) SnakeHead else SnakeBody
                val margin = if (index == 0) 1f else 3f

                drawRect(
                    color = color,
                    topLeft = Offset(
                        casilla.columna * cellW + margin,
                        casilla.fila * cellH + margin
                    ),
                    size = Size(
                        cellW - margin * 2,
                        cellH - margin * 2
                    )
                )
            }
        }
    }
}

@Composable
private fun BotonPausa(
    enPausa: Boolean,
    onTogglePausa: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Button(
        onClick = onTogglePausa,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enPausa) SnakeGreen else BtnNeutral
        )
    ) {
        Text(
            if (enPausa) "▶  REANUDAR" else "⏸  PAUSAR",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ControlesDireccion(
    onCambiarDireccion: (Direccion) -> Unit,
    tamanoBoton: Dp = 72.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BotonDireccion("▲", Direccion.ARRIBA, onCambiarDireccion, Modifier.size(tamanoBoton))
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonDireccion("◀", Direccion.IZQUIERDA, onCambiarDireccion, Modifier.size(tamanoBoton))
            Box(Modifier.size(tamanoBoton))
            BotonDireccion("▶", Direccion.DERECHA, onCambiarDireccion, Modifier.size(tamanoBoton))
        }
        BotonDireccion("▼", Direccion.ABAJO, onCambiarDireccion, Modifier.size(tamanoBoton))
    }
}

@Composable
private fun BotonDireccion(
    etiqueta: String,
    direccion: Direccion,
    onClick: (Direccion) -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = { onClick(direccion) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
    ) {
        Text(etiqueta, fontSize = 22.sp, color = Color.White)
    }
}