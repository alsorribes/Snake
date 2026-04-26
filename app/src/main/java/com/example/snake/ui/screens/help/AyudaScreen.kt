package com.example.snake.ui.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.ui.components.GridBackground
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.MiniBoardBackground
import com.example.snake.ui.theme.MiniBoardEmptyCell
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen
import com.example.snake.ui.theme.SurfaceCard

@Composable
fun AyudaScreen(
    onIrAlJuego: () -> Unit,
    onVolver: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            // FIX [P5]: padding de insets del sistema
            .safeDrawingPadding()
    ) {
        GridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("❓", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "AJUDA", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                color = SnakeGreen, letterSpacing = 6.sp
            )
            Text(
                text = "Com jugar al Snake", fontSize = 13.sp,
                color = SnakeLightGreen.copy(alpha = 0.6f), letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(32.dp))
            MiniBoard()
            Spacer(Modifier.height(28.dp))

            HelpCard("🐍", "La Serp",
                "Controla la serp usant els botons de direcció. " +
                        "Comença amb 3 segments al centre del tauler, movent-se cap a la dreta.")
            Spacer(Modifier.height(12.dp))
            HelpCard("🍎", "Menja Pomes",
                "Cada cop que el cap toca una poma, creix un segment i " +
                        "apareix una nova poma en una posició aleatòria lliure.")
            Spacer(Modifier.height(12.dp))
            HelpCard("💀", "Game Over",
                "La partida acaba si la serp xoca amb una paret o amb el seu propi " +
                        "cos. Els girs de 180° no estan permesos!")
            Spacer(Modifier.height(12.dp))
            HelpCard("⏱️", "Control del Temps",
                "Si actives el control del temps a Configuració, tindràs un temps màxim. " +
                        "El temporitzador es mostra en vermell durant el joc.")
            Spacer(Modifier.height(12.dp))
            HelpCard("🏆", "Victòria",
                "Guanyes si la serp ocupa totes les caselles del tauler. " +
                        "En acabar podràs enviar el teu resultat per email.")

            Spacer(Modifier.height(32.dp))
            ConfigInfoCard()
            Spacer(Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(
                        listOf(SnakeDarkGreen, SnakeGreen, SnakeLightGreen)))
            ) {
                Button(
                    onClick = onIrAlJuego,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("▶  ANAR AL JOC", fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 6.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, SnakeLightGreen.copy(alpha = 0.4f))
            ) {
                Text("← TORNAR AL MENÚ", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MiniBoard() {
    val boardSize  = 7
    val snakeCells = setOf(Pair(3, 4), Pair(3, 3), Pair(3, 2))
    val headCell   = Pair(3, 4)
    val appleCells = setOf(Pair(1, 5), Pair(5, 2))

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SnakeGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            // FIX [E2][F3]: usar constante de SnakeColors en lugar de hardcoded
            .background(MiniBoardBackground)
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(boardSize) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(boardSize) { col ->
                        val cell = Pair(row, col)
                        // FIX [E2][F4]: usar constante de SnakeColors en lugar de hardcoded
                        val color = when {
                            cell == headCell   -> SnakeGreen
                            cell in snakeCells -> SnakeLightGreen.copy(alpha = 0.6f)
                            cell in appleCells -> Color(0xFFE53935)
                            else               -> MiniBoardEmptyCell
                        }
                        Box(modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color))
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpCard(emoji: String, title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SnakeGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text(body, fontSize = 13.sp, color = Color.White.copy(alpha = 0.65f),
                    lineHeight = 19.sp)
            }
        }
    }
}

@Composable
private fun ConfigInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SnakeDarkGreen.copy(alpha = 0.25f)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(SnakeGreen.copy(0.3f), SnakeLightGreen.copy(0.3f))))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("⚙️  Opcions de Configuració", fontSize = 14.sp,
                fontWeight = FontWeight.Bold, color = SnakeLightGreen)
            Spacer(Modifier.height(10.dp))
            ConfigOption("👤", "Àlies del jugador")
            ConfigOption("📐", "Mida de la parrilla: Petita · Mitjana · Gran")
            ConfigOption("⏱️", "Control del temps amb límit configurable")
        }
    }
}

@Composable
private fun ConfigOption(emoji: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
    }
}