package com.example.snake.ui.screens.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.model.Casilla
import com.example.snake.model.Direccion
import com.example.snake.model.Partida
import com.example.snake.model.Serpiente
import com.example.snake.viewmodel.GameUiState

// ─── Colores del juego ───────────────────────────────────────────────────────
private val ColorFondoTablero  = Color(0xFF101820)
private val ColorCuadricula    = Color(0xFF2A3B47)
private val ColorCabeza        = Color(0xFF4CAF50)
private val ColorCuerpo        = Color(0xFF81C784)
private val ColorManzana       = Color(0xFFE53935)
// FIX [11]: colores explícitos para el tiempo según el enunciado
private val ColorTiempoSinControl = Color(0xFF2196F3)  // Azul
private val ColorTiempoConControl = Color(0xFFF44336)  // Rojo

// =============================================================================
// COMPOSABLE STATEFUL: JuegoScreen
// Recibe el estado completo y las lambdas de acción.
// Es el único punto que conoce GameUiState.
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
            Text("No hay partida en curso", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // Delega en el composable stateless pasando solo los datos necesarios
    JuegoContenido(
        partida          = partida,
        enPausa          = uiState.enPausa,
        onCambiarDireccion = onCambiarDireccion,
        onTogglePausa    = onTogglePausa
    )
}

// =============================================================================
// COMPOSABLE STATELESS: JuegoContenido
// No conoce GameUiState. Recibe exactamente lo que necesita renderizar.
// Facilita la preview y el testing.
// =============================================================================
@Composable
private fun JuegoContenido(
    partida: Partida,
    enPausa: Boolean,
    onCambiarDireccion: (Direccion) -> Unit,
    onTogglePausa: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B2F))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Cabecera con info de partida ──────────────────────────────────
        InfoPartida(
            alias          = partida.config.alias,
            manzanasComidas = partida.manzanasComidas,
            longitud       = partida.serpiente.longitud,
            tiempoRestante = partida.tiempoRestanteSeg,
            controlTiempo  = partida.config.controlTiempo,
            enPausa        = enPausa
        )

        // ── Tablero del juego (stateless) ─────────────────────────────────
        TableroJuego(
            filas      = partida.filas,
            columnas   = partida.columnas,
            serpiente  = partida.serpiente,
            manzana    = partida.manzana,
            modifier   = Modifier
                .fillMaxWidth()
                .aspectRatio(partida.columnas.toFloat() / partida.filas.toFloat())
        )

        // ── Botón pausa ───────────────────────────────────────────────────
        Button(
            onClick = onTogglePausa,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enPausa) ColorCabeza else Color(0xFF37474F)
            )
        ) {
            Text(if (enPausa) "▶  REANUDAR" else "⏸  PAUSAR", fontWeight = FontWeight.SemiBold)
        }

        // ── Controles de dirección ────────────────────────────────────────
        ControlesDireccion(onCambiarDireccion = onCambiarDireccion)
    }
}

// =============================================================================
// COMPOSABLE STATELESS: InfoPartida
// Muestra alias, manzanas, longitud y tiempo con el color correcto.
// FIX [11]: tiempo en azul (sin control) o rojo (con control), según enunciado.
// =============================================================================
@Composable
private fun InfoPartida(
    alias: String,
    manzanasComidas: Int,
    longitud: Int,
    tiempoRestante: Int,
    controlTiempo: Boolean,
    enPausa: Boolean
) {
    // FIX [11]: color del tiempo según el enunciado
    val colorTiempo = if (controlTiempo) ColorTiempoConControl else ColorTiempoSinControl

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "👤 $alias",
                color = Color.White,
                fontSize = 13.sp
            )
            Text(
                text = "🍎 $manzanasComidas  •  🐍 $longitud seg.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (controlTiempo) "⏱ ${tiempoRestante}s" else "⏱ ${tiempoRestante}s",
                color = colorTiempo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (enPausa) {
                Text("PAUSADO", color = Color.Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =============================================================================
// COMPOSABLE STATELESS: TableroJuego
// Dibuja el tablero con Canvas. No conoce nada del estado de la app.
// FIX [10]: separado correctamente como stateless según criterio de corrección.
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

        // Fondo
        drawRect(color = ColorFondoTablero, size = size)

        // Cuadrícula
        for (f in 0..filas) {
            drawLine(ColorCuadricula, Offset(0f, f * cellH), Offset(size.width, f * cellH), 1f)
        }
        for (c in 0..columnas) {
            drawLine(ColorCuadricula, Offset(c * cellW, 0f), Offset(c * cellW, size.height), 1f)
        }

        // Manzana
        drawRect(
            color   = ColorManzana,
            topLeft = Offset(manzana.columna * cellW + 2f, manzana.fila * cellH + 2f),
            size    = Size(cellW - 4f, cellH - 4f)
        )

        // Serpiente
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
// COMPOSABLE STATELESS: ControlesDireccion
// Botonera de dirección desacoplada del resto.
// =============================================================================
@Composable
private fun ControlesDireccion(
    onCambiarDireccion: (Direccion) -> Unit
) {
    val colorBoton = Color(0xFF2E7D32)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Arriba
        BotonDireccion("▲", Direccion.ARRIBA, colorBoton, onCambiarDireccion,
            Modifier.size(64.dp))

        // Izquierda / Derecha
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonDireccion("◀", Direccion.IZQUIERDA, colorBoton, onCambiarDireccion,
                Modifier.size(64.dp))
            Box(Modifier.size(64.dp))  // espacio central
            BotonDireccion("▶", Direccion.DERECHA, colorBoton, onCambiarDireccion,
                Modifier.size(64.dp))
        }

        // Abajo
        BotonDireccion("▼", Direccion.ABAJO, colorBoton, onCambiarDireccion,
            Modifier.size(64.dp))
    }
}

// =============================================================================
// COMPOSABLE STATELESS: BotonDireccion
// =============================================================================
@Composable
private fun BotonDireccion(
    etiqueta: String,
    direccion: Direccion,
    color: Color,
    onClick: (Direccion) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(direccion) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(etiqueta, fontSize = 20.sp, color = Color.White)
    }
}