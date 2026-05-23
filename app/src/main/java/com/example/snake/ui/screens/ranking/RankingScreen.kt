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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.R
import com.example.snake.model.LogPartida
import com.example.snake.model.ResultadoPartida
import com.example.snake.model.TamanoParrilla
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen
import com.example.snake.ui.theme.SurfaceCard

private const val TABLET_BREAKPOINT_DP = 600

@Composable
fun RankingScreen(
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida?) -> Unit,
    onVolver: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val esTablet      = configuration.screenWidthDp >= TABLET_BREAKPOINT_DP

    val tabs      = TamanoParrilla.entries.map { it.etiqueta }
    var tabActual by remember { mutableIntStateOf(0) }

    val partidasFiltrades = partidas.filter { it.tamanoParrilla == tabs[tabActual] }

    LaunchedEffect(tabActual, partidasFiltrades) {
        if (esTablet && seleccionada == null && partidasFiltrades.isNotEmpty()) {
            onSeleccionar(partidasFiltrades.first())
        }
    }

    LaunchedEffect(tabActual) {
        if (!esTablet) onSeleccionar(null)
    }

    if (esTablet) {
        RankingBiPanel(
            tabs              = tabs,
            tabActual         = tabActual,
            onTabSeleccionada = { tabActual = it; onSeleccionar(null) },
            partidas          = partidasFiltrades,
            seleccionada      = seleccionada,
            onSeleccionar     = onSeleccionar,
            onVolver          = onVolver
        )
    } else {
        RankingMonoPanel(
            tabs              = tabs,
            tabActual         = tabActual,
            onTabSeleccionada = { tabActual = it },
            partidas          = partidasFiltrades,
            seleccionada      = seleccionada,
            onSeleccionar     = onSeleccionar,
            onVolver          = onVolver
        )
    }
}

@Composable
private fun RankingBiPanel(
    tabs: List<String>,
    tabActual: Int,
    onTabSeleccionada: (Int) -> Unit,
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida?) -> Unit,
    onVolver: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            RankingHeader(onVolver = onVolver)
            RankingTabs(tabs = tabs, tabActual = tabActual, onSeleccionar = onTabSeleccionada)
            ListaPartidas(partidas = partidas, seleccionada = seleccionada, onSeleccionar = onSeleccionar)
        }

        VerticalDivider(color = Color.White.copy(alpha = 0.1f))

        Box(
            modifier         = Modifier
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

@Composable
private fun RankingMonoPanel(
    tabs: List<String>,
    tabActual: Int,
    onTabSeleccionada: (Int) -> Unit,
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida?) -> Unit,
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
            RankingTabs(tabs = tabs, tabActual = tabActual, onSeleccionar = onTabSeleccionada)
            ListaPartidas(partidas = partidas, seleccionada = null, onSeleccionar = onSeleccionar)
        } else {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onSeleccionar(null) },
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)
                ) {
                    Text(stringResource(R.string.ranking_tornar_llista))
                }
            }
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DetallePartida(partida = seleccionada)
            }
        }
    }
}

@Composable
private fun RankingHeader(onVolver: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
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
private fun RankingTabs(
    tabs: List<String>,
    tabActual: Int,
    onSeleccionar: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = tabActual,
        containerColor   = BackgroundDark,
        contentColor     = SnakeGreen
    ) {
        tabs.forEachIndexed { index, etiqueta ->
            Tab(
                selected = tabActual == index,
                onClick  = { onSeleccionar(index) },
                text     = {
                    Text(
                        text     = etiqueta,
                        fontSize = 13.sp,
                        color    = if (tabActual == index) SnakeGreen
                        else Color.White.copy(alpha = 0.4f)
                    )
                }
            )
        }
    }
}

@Composable
private fun ListaPartidas(
    partidas: List<LogPartida>,
    seleccionada: LogPartida?,
    onSeleccionar: (LogPartida?) -> Unit
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
                posicio      = index + 1,
                partida      = partida,
                seleccionada = partida == seleccionada,
                onClick      = { onSeleccionar(partida) }
            )
        }
    }
}

@Composable
private fun FilaRanking(
    posicio: Int,
    partida: LogPartida,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val medalla = when (posicio) {
        1    -> stringResource(R.string.emoji_or)
        2    -> stringResource(R.string.emoji_plata)
        3    -> stringResource(R.string.emoji_bronze)
        else -> stringResource(R.string.ranking_posicio, posicio)
    }

    val medallaColor = when (posicio) {
        1    -> Color(0xFFFFD700)
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> Color.White.copy(alpha = 0.5f)
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
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
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
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    stringResource(R.string.ranking_fila_pomes, partida.manzanasComidas),
                    color      = SnakeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                Text(
                    stringResource(R.string.ranking_fila_temps, partida.tiempoTotalSeg),
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
            FilaDetalle(stringResource(R.string.ranking_tamano),      partida.tamanoParrilla)
            FilaDetalle(stringResource(R.string.ranking_manzanas),    "${partida.manzanasComidas}")
            FilaDetalle(stringResource(R.string.ranking_longitud),    "${partida.longitudFinal}")
            FilaDetalle(stringResource(R.string.ranking_temps_total), "${partida.tiempoTotalSeg}s")
            if (partida.controlTiempo && partida.tiempoSobranteSeg > 0) {
                FilaDetalle(stringResource(R.string.ranking_temps_sobrant), "${partida.tiempoSobranteSeg}s")
            }
            partida.fechaHoraFin?.let {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                FilaDetalle(stringResource(R.string.resultados_fecha_label), partida.fechaHoraFormateada())
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