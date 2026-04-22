package com.example.snake.ui.screens.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snake.model.ConfiguracionPartida
import com.example.snake.model.TamanoParrilla
import com.example.snake.model.TIEMPO_MAXIMO_DEFECTO_SEG

@Composable
fun ConfiguracionScreen(
    onEmpezar: (ConfiguracionPartida) -> Unit,
    onVolver: () -> Unit
) {
    var alias by rememberSaveable { mutableStateOf("") }
    var tamanoSeleccionado by rememberSaveable { mutableStateOf(TamanoParrilla.MEDIANA.name) }
    var controlTiempo by rememberSaveable { mutableStateOf(false) }
    var tiempoMaximoTexto by rememberSaveable { mutableStateOf(TIEMPO_MAXIMO_DEFECTO_SEG.toString()) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val tamanoParrilla = TamanoParrilla.valueOf(tamanoSeleccionado)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Configuración de la partida",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = alias,
            onValueChange = {
                alias = it
                error = null
            },
            label = { Text("Alias") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Tamaño de parrilla")

        TamanoParrilla.entries.forEach { tamano ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tamanoSeleccionado == tamano.name,
                    onClick = {
                        tamanoSeleccionado = tamano.name
                        error = null
                    }
                )
                Text(tamano.etiqueta)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = controlTiempo,
                onCheckedChange = {
                    controlTiempo = it
                    error = null
                }
            )
            Text("Activar control de tiempo")
        }

        OutlinedTextField(
            value = tiempoMaximoTexto,
            onValueChange = {
                tiempoMaximoTexto = it
                error = null
            },
            label = { Text("Tiempo máximo (segundos)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = controlTiempo,
            singleLine = true
        )

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val tiempoMaximo = tiempoMaximoTexto.toIntOrNull()
                    ?: TIEMPO_MAXIMO_DEFECTO_SEG

                val config = ConfiguracionPartida(
                    alias = alias,
                    tamanoParrilla = tamanoParrilla,
                    controlTiempo = controlTiempo,
                    tiempoMaximoSeg = tiempoMaximo
                )

                val mensajeError = config.validar()
                if (mensajeError != null) {
                    error = mensajeError
                } else {
                    onEmpezar(config)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar partida")
        }

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}