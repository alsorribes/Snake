package com.example.snake.ui.screens.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.R
import com.example.snake.model.LogPartida
import com.example.snake.model.ResultadoPartida
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen
import com.example.snake.ui.theme.SurfaceCard

private val TABLET_BREAKPOINT_DP = 600

// ── Stateful ─────────────────────────────────────────────────────────────────

@Composable
fun RankingScreen(
    partidas: List<LogPartida>,
    onVolver: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val esTablet      = configuration.screenWidthDp >= TABLET_BREAKPOINT_DP

    var seleccionada by remember { mutableStateOf<LogPartida?>(null) }

    // En tablet, seleccionar la primera por defecto si hay partidas
    LaunchedEffect(partidas) {
        if (esTablet && seleccionada == null && partidas.isNotEmpty()) {
            seleccionada = partidas.first()
        }
    }

    if (esTablet) {
        RankingBiPanel(
            partidas     = partidas,
            seleccionada = seleccionada,
            onSeleccionar = { seleccionada = it },
            onVolver     = onVolver
        )
    } else {
        RankingMonoPanel(
            partidas      = partidas,
            onSeleccionar = { seleccionada = it },
            seleccionada  = seleccionada,
            onVolver      = onVolver
        )
    }
}

// ── Tablet: bi-panel ─────────────────────────────────────────────────────────

@Composable
private fun RankingBiPanel(
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida) -> Unit,
    onVolver: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        // Panel esquerra: llista
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            RankingHeader(onVolver = onVolver)
            ListaPartidas(
                partidas      = partidas,
                seleccionada  = seleccionada,
                onSeleccionar = onSeleccionar
            )
        }

        HorizontalDivider(
            modifier  = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color     = Color.White.copy(alpha = 0.1f)
        )

        // Panel dret: detall
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (seleccionada != null) {
                DetallePartida(partida = seleccionada)
            } else {
                Text(
                    stringResource(R.string.ranking_selecciona),
                    color    = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ── Smartphone: mono-panel ────────────────────────────────────────────────────

@Composable
private fun RankingMonoPanel(
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        if (seleccionada == null) {
            RankingHeader(onVolver = onVolver)
            ListaPartidas(
                partidas      = partidas,
                seleccionada  = null,
                onSeleccionar = onSeleccionar
            )
        } else {
            // Pantalla de detalle
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onSeleccionar(seleccionada.copy()) /* reset via null trick */ }) { }
                // Botón volver al ranking
                OutlinedButton(
                    onClick = { onSeleccionar(LogPartida()) },
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)
                ) {
                    Text("◀ ${stringResource(R.string.ranking_tornar_llista)}")
                }
            }
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DetallePartida(partida = seleccionada)
            }
        }
    }
}

// ── Componentes compartits ────────────────────────────────────────────────────

@Composable
private fun RankingHeader(onVolver: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = stringResource(R.string.ranking_titulo),
            fontSize   = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = SnakeGreen
        )
        OutlinedButton(
            onClick = onVolver,
            colors  = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)
        ) {
            Text(stringResource(R.string.ranking_volver))
        }
    }
}

@Composable
private fun ListaPartidas(
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida) -> Unit
) {
    if (partidas.isEmpty()) {
        Box(
            modifier         = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.ranking_buit),
                color    = Color.White.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        }
        return
    }

    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(partidas) { index, partida ->
            FilaRanking(
                posicion     = index + 1,
                partida      = partida,
                seleccionada = partida == seleccionada,
                onClick      = { onSeleccionar(partida) }
            )
        }
    }
}

@Composable
private fun FilaRanking(
    posicion: Int,
    partida: LogPartida,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val medallaColor = when (posicion) {
        1    -> Color(0xFFFFD700) // Or
        2    -> Color(0xFFC0C0C0) // Plata
        3    -> Color(0xFFCD7F32) // Bronze
        else -> Color.White.copy(alpha = 0.5f)
    }

    val medalla = when (posicion) {
        1    -> "🥇"
        2    -> "🥈"
        3    -> "🥉"
        else -> "#$posicion"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) SnakeGreen.copy(alpha = 0.2f) else SurfaceCard
        )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Posició + àlies
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(medalla, fontSize = 20.sp, color = medallaColor)
                Column {
                    Text(
                        partida.alias,
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                    Text(
                        partida.tamanoParrilla,
                        color    = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }
            }

            // Puntuació + resultat
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "🍎 ${partida.manzanasComidas}",
                    color      = SnakeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                Text(
                    "⏱ ${partida.tiempoTotalSeg}s",
                    color    = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun DetallePartida(partida: LogPartida) {
    val resultatColor = when (partida.resultado) {
        ResultadoPartida.GANADA           -> SnakeGreen
        ResultadoPartida.PERDIDA_COLISION -> Color(0xFFE53935)
        ResultadoPartida.PERDIDA_TIEMPO   -> Color(0xFFFF8F00)
        null                              -> Color.White
    }

    val resultatText = when (partida.resultado) {
        ResultadoPartida.GANADA           -> stringResource(R.string.resultados_guanyat)
        ResultadoPartida.PERDIDA_COLISION -> stringResource(R.string.resultados_perdut_collisio)
        ResultadoPartida.PERDIDA_TIEMPO   -> stringResource(R.string.resultados_perdut_temps)
        null                              -> stringResource(R.string.resultados_no_resultat)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier            = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                partida.alias,
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White
            )
            Text(
                resultatText,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = resultatColor
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            FilaDetalle("📐 ${stringResource(R.string.config_tamano_label)}", partida.tamanoParrilla)
            FilaDetalle("🍎 ${stringResource(R.string.ranking_manzanas)}", "${partida.manzanasComidas}")
            FilaDetalle("🐍 ${stringResource(R.string.ranking_longitud)}", "${partida.longitudFinal}")
            FilaDetalle("⏱ ${stringResource(R.string.ranking_temps_total)}", "${partida.tiempoTotalSeg}s")
            if (partida.controlTiempo && partida.tiempoSobranteSeg > 0) {
                FilaDetalle("⏳ ${stringResource(R.string.ranking_temps_sobrant)}", "${partida.tiempoSobranteSeg}s")
            }
            partida.fechaHoraFin?.let {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                FilaDetalle("📅 ${stringResource(R.string.resultados_fecha_label)}", partida.fechaHoraFormateada())
            }
        }
    }
}

@Composable
private fun FilaDetalle(label: String, valor: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
        Text(valor, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}