package com.example.snake.ui.screens.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.ui.components.GridBackground

private val SnakeGreen      = Color(0xFF4CAF50)
private val SnakeDarkGreen  = Color(0xFF2E7D32)
private val SnakeLightGreen = Color(0xFF81C784)
// FIX [I]: eliminadas SnakeAccent y BackgroundDark que no se usaban directamente aquí
private val BackgroundDark  = Color(0xFF1B1B2F)
private val SurfaceCard     = Color(0xFF252540)

@Composable
fun MenuPrincipalScreen(
    onEmpezarPartida: () -> Unit,
    onAyuda: () -> Unit,
    onSalir: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "logoScale"
    )

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        GridBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🐍", fontSize = 88.sp, modifier = Modifier.scale(scale))

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "SNAKE", fontSize = 52.sp, fontWeight = FontWeight.ExtraBold,
                color = SnakeGreen, letterSpacing = 8.sp)
            Text(text = "El juego de la serpiente", fontSize = 14.sp,
                color = SnakeLightGreen.copy(alpha = 0.7f),
                textAlign = TextAlign.Center, letterSpacing = 2.sp)

            Spacer(modifier = Modifier.height(56.dp))

            SnakeMenuButton("▶  EMPEZAR PARTIDA", onEmpezarPartida, primary = true)
            Spacer(modifier = Modifier.height(16.dp))
            SnakeMenuButton("❓  AYUDA", onAyuda, primary = false)
            Spacer(modifier = Modifier.height(16.dp))
            SnakeMenuButton("✕  SALIR", onSalir, primary = false, tint = Color(0xFFEF5350))

            Spacer(modifier = Modifier.height(48.dp))

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Text("🍎", fontSize = 18.sp)
                Spacer(Modifier.width(6.dp))
                Text("Come manzanas · Crece · Sobrevive", fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.35f), textAlign = TextAlign.Center)
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

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(backgroundBrush)) {
        Button(
            onClick = onClick, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor   = if (primary) Color.White else tint
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 6.dp))
        }
    }
}