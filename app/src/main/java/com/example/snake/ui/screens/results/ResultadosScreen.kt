package com.example.snake.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("No hay resultados disponibles")
            Button(onClick = onNuevaPartida, modifier = Modifier.fillMaxWidth()) {
                Text("Nueva partida")
            }
        }
        return
    }

    var email by rememberSaveable(uiState.emailDestinatario) {
        mutableStateOf(uiState.emailDestinatario)
    }

    // FIX [6]: clave estable basada en alias+resultado en vez de generarTexto()
    // (generarTexto() incluye Date que puede recalcularse entre composiciones)
    val claveLog = "${log.alias}_${log.resultado}_${log.tiempoTotalSeg}"
    var textoLog by rememberSaveable(claveLog) { mutableStateOf(log.generarTexto()) }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }

    val focusRequester = remember { FocusRequester() }

    // FIX [7]: try-catch para evitar IllegalStateException si el layout
    // no está listo todavía cuando se solicita el foco
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
            // El composable no estaba adjunto al árbol todavía; el usuario
            // puede tocar el campo manualmente
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Resultados de la partida", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = log.fechaHoraFormateada(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Día y hora de finalización") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = textoLog,
            onValueChange = { textoLog = it },
            label = { Text("Valores del log") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                onEmailCambiado(it)
            },
            label = { Text("E-mail destinatario") },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            singleLine = true,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Button(
            onClick = {
                if (!EMAIL_REGEX.matches(email)) {
                    emailError = "Introduce un email válido"
                } else {
                    emailError = null
                    onEnviarEmail(email)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("📧  Enviar e-mail") }

        OutlinedButton(onClick = onNuevaPartida, modifier = Modifier.fillMaxWidth()) {
            Text("← Nova partida")
        }

        Button(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
        ) { Text("✕  Sortir") }
    }
}