package com.example.snake.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snake.viewmodel.GameUiState

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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("No hay resultados disponibles")
            Button(
                onClick = onNuevaPartida,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nueva partida")
            }
        }
        return
    }

    var email by remember(uiState.emailDestinatario) {
        mutableStateOf(uiState.emailDestinatario)
    }

    var textoLog by remember(log) {
        mutableStateOf(log.generarTexto())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Resultados",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = log.fechaHoraFormateada(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha y hora de finalización") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = textoLog,
            onValueChange = { textoLog = it },
            label = { Text("Log de la partida") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onEmailCambiado(it)
            },
            label = { Text("Email destinatario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = { onEnviarEmail(email) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar email")
        }

        Button(
            onClick = onNuevaPartida,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nueva partida")
        }

        Button(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salir")
        }
    }
}