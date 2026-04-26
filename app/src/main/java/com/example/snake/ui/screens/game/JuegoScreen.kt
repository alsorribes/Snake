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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.model.Casilla
import com.example.snake.model.Direccion
import com.example.snake.model.Partida
import com.example.snake.model.Serpiente
import com.example.snake.viewmodel.GameUiState

private val ColorFondoTablero     = Color(0xFF101820)
private val ColorCuadricula       = Color(0xFF2A3B47)
private val ColorCabeza           = Color(0xFF4CAF50)
private val ColorCuerpo           = Color(0xFF81C784)
private val ColorManzana          = Color(0xFFE53935)
// FIX [E][K]: colores del tiempo según enunciado
private val ColorTiempoSinControl = Color(0xFF2196F3)  // Azul  (tiempo transcurrido)
private val ColorTiempoConControl = Color(0xFFF44336)  // Rojo  (cuenta atrás)

// =============================================================================
// STATEFUL: JuegoScreen — único punto que conoce GameUiState
// =============================================================================
@Composable
fun JuegoScreen(
    uiState: GameUiState,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit
) {
    val partida = uiState.partida
    if (partida == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay partida en curso", style = MaterialTheme.typography.bodyLarge,
                color = Color.White)
        }
        return
    }

    // FIX [C]: detectar orientación y usar layout diferente
    val esLandscape = LocalConfiguration.current.screenWidthDp >
            LocalConfiguration.current.screenHeightDp

    if (esLandscape) {
        JuegoLandscape(partida = partida, enPausa = uiState.enPausa,
            onCambiarDireccion = onCambiarDireccion, onTogglePausa = onTogglePausa)
    } else {
        JuegoPortrait(partida = partida, enPausa = uiState.enPausa,
            onCambiarDireccion = onCambiarDireccion, onTogglePausa = onTogglePausa)
    }
}

// =============================================================================
// STATELESS: JuegoPortrait — layout vertical normal
// =============================================================================
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
            .background(ColorFondoTablero)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoPartida(
            alias           = partida.config.alias,
            manzanasComidas = partida.manzanasComidas,
            longitud        = partida.serpiente.longitud,
            tiempoMostrar   = partida.tiempoParaMostrar,   // FIX [E][K]
            controlTiempo   = partida.config.controlTiempo,
            enPausa         = enPausa
        )

        TableroJuego(
            filas     = partida.filas,
            columnas  = partida.columnas,
            serpiente = partida.serpiente,
            manzana   = partida.manzana,
            modifier  = Modifier
                .fillMaxWidth()
                .aspectRatio(partida.columnas.toFloat() / partida.filas.toFloat())
        )

        BotonPausa(enPausa = enPausa, onTogglePausa = onTogglePausa)

        ControlesDireccion(onCambiarDireccion = onCambiarDireccion)
    }
}

// =============================================================================
// STATELESS: JuegoLandscape — FIX [C]: layout horizontal
// Tablero a la izquierda, controles + info a la derecha
// =============================================================================
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
            .background(ColorFondoTablero)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tablero ocupa el lado izquierdo con aspect ratio correcto
        TableroJuego(
            filas     = partida.filas,
            columnas  = partida.columnas,
            serpiente = partida.serpiente,
            manzana   = partida.manzana,
            modifier  = Modifier
                .fillMaxHeight()
                .aspectRatio(partida.columnas.toFloat() / partida.filas.toFloat())
        )

        // Panel derecho: info + pausa + controles
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoPartida(
                alias           = partida.config.alias,
                manzanasComidas = partida.manzanasComidas,
                longitud        = partida.serpiente.longitud,
                tiempoMostrar   = partida.tiempoParaMostrar,
                controlTiempo   = partida.config.controlTiempo,
                enPausa         = enPausa
            )

            BotonPausa(enPausa = enPausa, onTogglePausa = onTogglePausa,
                modifier = Modifier.fillMaxWidth())

            ControlesDireccion(onCambiarDireccion = onCambiarDireccion)
        }
    }
}

// =============================================================================
// STATELESS: InfoPartida
// FIX [E][K]: recibe tiempoMostrar ya calculado desde Partida.tiempoParaMostrar
// (tiempo restante con control, tiempo transcurrido sin control)
// =============================================================================
@Composable
private fun InfoPartida(
    alias: String,
    manzanasComidas: Int,
    longitud: Int,
    tiempoMostrar: Int,
    controlTiempo: Boolean,
    enPausa: Boolean
) {
    val colorTiempo = if (controlTiempo) ColorTiempoConControl else ColorTiempoSinControl
    val labelTiempo = if (controlTiempo) "⏱ ${tiempoMostrar}s restantes" else "⏱ ${tiempoMostrar}s"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("👤 $alias", color = Color.White, fontSize = 13.sp)
            Text("🍎 $manzanasComidas  •  🐍 $longitud seg.",
                color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(labelTiempo, color = colorTiempo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (enPausa) Text("PAUSADO", color = Color.Yellow, fontSize = 11.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}

// =============================================================================
// STATELESS: TableroJuego
// =============================================================================
@Composable
fun TableroJuego(
    filas: Int,
    columnas: Int,
    serpiente: Serpiente,
    manzana: Casilla,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cellW = size.width  / columnas
        val cellH = size.height / filas

        drawRect(color = ColorFondoTablero, size = size)

        for (f in 0..filas)   drawLine(ColorCuadricula, Offset(0f, f*cellH), Offset(size.width, f*cellH), 1f)
        for (c in 0..columnas) drawLine(ColorCuadricula, Offset(c*cellW, 0f), Offset(c*cellW, size.height), 1f)

        drawRect(
            color   = ColorManzana,
            topLeft = Offset(manzana.columna * cellW + 2f, manzana.fila * cellH + 2f),
            size    = Size(cellW - 4f, cellH - 4f)
        )

        serpiente.segmentos.forEachIndexed { index, casilla ->
            val color  = if (index == 0) ColorCabeza else ColorCuerpo
            val margin = if (index == 0) 1f else 2f
            drawRect(
                color   = color,
                topLeft = Offset(casilla.columna * cellW + margin, casilla.fila * cellH + margin),
                size    = Size(cellW - margin * 2, cellH - margin * 2)
            )
        }
    }
}

// =============================================================================
// STATELESS: BotonPausa
// =============================================================================
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
            containerColor = if (enPausa) ColorCabeza else Color(0xFF37474F)
        )
    ) {
        Text(if (enPausa) "▶  REANUDAR" else "⏸  PAUSAR", fontWeight = FontWeight.SemiBold)
    }
}

// =============================================================================
// STATELESS: ControlesDireccion
// =============================================================================
@Composable
private fun ControlesDireccion(onCambiarDireccion: (Direccion) -> Unit) {
    val colorBoton = Color(0xFF2E7D32)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BotonDireccion("▲", Direccion.ARRIBA,    colorBoton, onCambiarDireccion, Modifier.size(56.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            BotonDireccion("◀", Direccion.IZQUIERDA, colorBoton, onCambiarDireccion, Modifier.size(56.dp))
            Box(Modifier.size(56.dp))
            BotonDireccion("▶", Direccion.DERECHA,   colorBoton, onCambiarDireccion, Modifier.size(56.dp))
        }
        BotonDireccion("▼", Direccion.ABAJO,    colorBoton, onCambiarDireccion, Modifier.size(56.dp))
    }
}

// =============================================================================
// STATELESS: BotonDireccion
// =============================================================================
@Composable
private fun BotonDireccion(
    etiqueta: String,
    direccion: Direccion,
    color: Color,
    onClick: (Direccion) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = { onClick(direccion) }, modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color)) {
        Text(etiqueta, fontSize = 18.sp, color = Color.White)
    }
}