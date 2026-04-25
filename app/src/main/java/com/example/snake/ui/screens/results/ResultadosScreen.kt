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

// FIX [7]: regex de validación de email
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
    var textoLog by remember(log) { mutableStateOf(log.generarTexto()) }
    // FIX [7]: estado de error de validación del email
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }

    // FIX (requisito enunciado): foco automático en el campo email al abrir la pantalla
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // FIX [1 diseño]: scroll vertical garantiza que los botones sean visibles
            // tanto en portrait como en landscape
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Resultados de la partida",
            style = MaterialTheme.typography.headlineSmall
        )

        // Fecha y hora (editable según enunciado)
        OutlinedTextField(
            value = log.fechaHoraFormateada(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Día y hora de finalización") },
            modifier = Modifier.fillMaxWidth()
        )

        // Log editable (se enviará como cuerpo del email)
        OutlinedTextField(
            value = textoLog,
            onValueChange = { textoLog = it },
            label = { Text("Valores del log") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )

        // FIX [7]: campo email con validación de formato y foco automático
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                onEmailCambiado(it)
            },
            label = { Text("E-mail destinatario") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Enviar email con validación previa
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
        ) {
            Text("📧  Enviar e-mail")
        }

        OutlinedButton(
            onClick = onNuevaPartida,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Nueva partida")
        }

        Button(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
        ) {
            Text("✕  Salir")
        }
    }
}