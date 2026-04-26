package com.example.snake.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.ui.components.GridBackground
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.BtnDanger
import com.example.snake.ui.theme.BtnError
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen
import com.example.snake.viewmodel.GameUiState

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun ResultadosScreen(
    uiState: GameUiState,
    onEnviarEmail: (String) -> Unit,
    onNuevaPartida: () -> Unit,
    onSalir: () -> Unit,
    onEmailCambiado: (String) -> Unit
) {
    val log = uiState.log

    if (log == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                // FIX [P4]: padding de insets del sistema
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No hi ha resultats disponibles", color = Color.White, fontSize = 16.sp)
                Button(onClick = onNuevaPartida, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
                ) { Text("← Nova partida") }
            }
        }
        return
    }

    var email by rememberSaveable(uiState.emailDestinatario) {
        mutableStateOf(uiState.emailDestinatario)
    }
    val claveLog = "${log.alias}_${log.resultado}_${log.tiempoTotalSeg}"
    var textoLog by rememberSaveable(claveLog) { mutableStateOf(log.generarTexto()) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        try { focusRequester.requestFocus() } catch (_: Exception) { }
    }

    // FIX [P4]: safeDrawingPadding en el contenedor raíz
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        GridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("🏁  Resultats", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                color = SnakeGreen)
            Text("Resum de la partida", fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f))

            EtiquetaResultado("📅  Dia i hora de finalització")
            OutlinedTextField(
                value = log.fechaHoraFormateada(), onValueChange = {},
                readOnly = true, modifier = Modifier.fillMaxWidth(), colors = camposColores()
            )

            EtiquetaResultado("📋  Valors del log")
            OutlinedTextField(
                value = textoLog, onValueChange = { textoLog = it },
                modifier = Modifier.fillMaxWidth(), minLines = 5, colors = camposColores()
            )

            EtiquetaResultado("📧  E-mail destinatari")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null; onEmailCambiado(it) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                singleLine = true,
                isError = emailError != null,
                supportingText = emailError?.let {
                    { Text(it, color = BtnError, fontSize = 12.sp) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = camposColores()
            )

            Button(
                onClick = {
                    if (!EMAIL_REGEX.matches(email)) emailError = "Introdueix un email vàlid"
                    else { emailError = null; onEnviarEmail(email) }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📧  Enviar e-mail", fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp))
            }

            OutlinedButton(
                onClick = onNuevaPartida, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, SnakeLightGreen.copy(0.4f))
            ) {
                Text("← Nova partida", modifier = Modifier.padding(vertical = 4.dp))
            }

            Button(
                onClick = onSalir, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BtnDanger),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("✕  Sortir", fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun EtiquetaResultado(texto: String) {
    Text(texto, color = SnakeLightGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun camposColores() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = SnakeGreen,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White,
    cursorColor          = SnakeGreen,
    focusedLabelColor    = SnakeLightGreen,
    unfocusedLabelColor  = SnakeLightGreen.copy(alpha = 0.6f)
)