package com.example.snake.ui.screens.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.R
import com.example.snake.ui.components.GridBackground
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.BtnError
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen
import com.example.snake.ui.theme.SurfaceCard

@Composable
fun MenuPrincipalScreen(
    onEmpezarPartida: () -> Unit,
    onConfigurar: () -> Unit,
    onRanquing: () -> Unit,
    onAyuda: () -> Unit,
    onSalir: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "logoScale"
    )

    val esLandscape =
        LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        GridBackground()

        if (esLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // Esquerra: logo + títol
                Column(
                    modifier            = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.emoji_serp), fontSize = 56.sp, modifier = Modifier.scale(scale))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text          = stringResource(R.string.menu_titol),
                        fontSize      = 40.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = SnakeGreen,
                        letterSpacing = 6.sp
                    )
                    Text(
                        text          = stringResource(R.string.menu_subtitulo),
                        fontSize      = 13.sp,
                        color         = SnakeLightGreen.copy(alpha = 0.7f),
                        textAlign     = TextAlign.Center,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.emoji_poma), fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.menu_tagline),
                            fontSize  = 11.sp,
                            color     = Color.White.copy(alpha = 0.35f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Dreta: botons
                Column(
                    modifier            = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SnakeMenuButton(stringResource(R.string.menu_empezar), onEmpezarPartida, primary = true)
                    SnakeMenuButton(stringResource(R.string.menu_configurar), onConfigurar, primary = false)
                    SnakeMenuButton(stringResource(R.string.menu_ranquing), onRanquing, primary = false)
                    SnakeMenuButton(stringResource(R.string.menu_ayuda), onAyuda, primary = false)
                    SnakeMenuButton(stringResource(R.string.menu_salir), onSalir, primary = false, tint = BtnError)
                }
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(32.dp))
                Text(stringResource(R.string.emoji_serp), fontSize = 88.sp, modifier = Modifier.scale(scale))
                Spacer(Modifier.height(8.dp))
                Text(
                    text          = stringResource(R.string.menu_titol),
                    fontSize      = 52.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = SnakeGreen,
                    letterSpacing = 8.sp
                )
                Text(
                    text          = stringResource(R.string.menu_subtitulo),
                    fontSize      = 14.sp,
                    color         = SnakeLightGreen.copy(alpha = 0.7f),
                    textAlign     = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(56.dp))
                SnakeMenuButton(stringResource(R.string.menu_empezar), onEmpezarPartida, primary = true)
                Spacer(Modifier.height(16.dp))
                SnakeMenuButton(stringResource(R.string.menu_configurar), onConfigurar, primary = false)
                Spacer(Modifier.height(16.dp))
                SnakeMenuButton(stringResource(R.string.menu_ranquing), onRanquing, primary = false)
                Spacer(Modifier.height(16.dp))
                SnakeMenuButton(stringResource(R.string.menu_ayuda), onAyuda, primary = false)
                Spacer(Modifier.height(16.dp))
                SnakeMenuButton(stringResource(R.string.menu_salir), onSalir, primary = false, tint = BtnError)
                Spacer(Modifier.height(48.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.emoji_poma), fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.menu_tagline),
                        fontSize  = 12.sp,
                        color     = Color.White.copy(alpha = 0.35f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SnakeMenuButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean,
    tint: Color = SnakeGreen
) {
    val backgroundBrush = if (primary)
        Brush.horizontalGradient(listOf(SnakeDarkGreen, SnakeGreen, SnakeLightGreen))
    else
        Brush.horizontalGradient(listOf(SurfaceCard, SurfaceCard))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundBrush)
    ) {
        Button(
            onClick   = onClick,
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(14.dp),
            colors    = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor   = if (primary) Color.White else tint
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text,
                fontSize      = 15.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier      = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}