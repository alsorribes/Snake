package com.example.snake.ui.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.model.ConfiguracionPartida
import com.example.snake.model.TIEMPO_MAXIMO_DEFECTO_SEG
import com.example.snake.model.TamanoParrilla
// FIX [E1]: colores importados desde SnakeColors en lugar de redefinirlos localmente
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.BtnError
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen

@Composable
fun ConfiguracionScreen(
    onEmpezar: (ConfiguracionPartida) -> Unit,
    onVolver: () -> Unit
) {
    var alias             by rememberSaveable { mutableStateOf("") }
    var tamanoSeleccionado by rememberSaveable { mutableStateOf(TamanoParrilla.MEDIANA.name) }
    var controlTiempo     by rememberSaveable { mutableStateOf(false) }
    var tiempoMaximoTexto by rememberSaveable { mutableStateOf(TIEMPO_MAXIMO_DEFECTO_SEG.toString()) }
    var errorGeneral      by rememberSaveable { mutableStateOf<String?>(null) }
    var errorTiempo       by rememberSaveable { mutableStateOf<String?>(null) }

    val tamanoParrilla = TamanoParrilla.valueOf(tamanoSeleccionado)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("⚙️  Configuració", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = SnakeGreen)
        Text("Ajusta els paràmetres de la teva partida", fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f))

        Spacer(Modifier.height(4.dp))

        SeccionAlias(alias = alias, onAliasChange = { alias = it; errorGeneral = null })
        SeccionTamano(tamanoSeleccionado = tamanoSeleccionado,
            onTamanoChange = { tamanoSeleccionado = it; errorGeneral = null })
        SeccionTiempo(controlTiempo, tiempoMaximoTexto, errorTiempo,
            onControlTiempoChange = { controlTiempo = it; errorTiempo = null },
            onTiempoTextoChange = { tiempoMaximoTexto = it; errorTiempo = null })

        if (errorGeneral != null) {
            Text(errorGeneral!!, color = BtnError, fontSize = 13.sp)
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = {
                if (controlTiempo) {
                    val t = tiempoMaximoTexto.toIntOrNull()
                    if (t == null || t <= 0) {
                        errorTiempo = "Introdueix un nombre de segons vàlid (> 0)"
                        return@Button
                    }
                }
                val config = ConfiguracionPartida(
                    alias = alias.ifBlank { "Jugador" },
                    tamanoParrilla = tamanoParrilla,
                    controlTiempo = controlTiempo,
                    tiempoMaximoSeg = tiempoMaximoTexto.toIntOrNull() ?: TIEMPO_MAXIMO_DEFECTO_SEG
                )
                val err = config.validar()
                if (err != null) errorGeneral = err else onEmpezar(config)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
        ) {
            Text("▶  COMENÇAR PARTIDA", fontWeight = FontWeight.Bold,
                fontSize = 15.sp, modifier = Modifier.padding(vertical = 4.dp))
        }

        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)) {
            Text("← TORNAR", fontSize = 14.sp)
        }
    }
}

@Composable
private fun SeccionAlias(alias: String, onAliasChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaSeccion("👤  Àlies del jugador")
        OutlinedTextField(
            value = alias, onValueChange = onAliasChange,
            placeholder = { Text("Ex: Jugador", color = Color.White.copy(alpha = 0.3f)) },
            label = { Text("Àlies", color = SnakeLightGreen.copy(alpha = 0.7f)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = camposColores()
        )
    }
}

@Composable
private fun SeccionTamano(tamanoSeleccionado: String, onTamanoChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EtiquetaSeccion("📐  Mida de la parrilla")
        TamanoParrilla.entries.forEach { tamano ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = tamanoSeleccionado == tamano.name,
                    onClick = { onTamanoChange(tamano.name) },
                    colors = RadioButtonDefaults.colors(selectedColor = SnakeGreen))
                Column {
                    Text(tamano.etiqueta, color = Color.White, fontSize = 14.sp)
                    Text("${tamano.filas}×${tamano.columnas} caselles",
                        color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun SeccionTiempo(
    controlTiempo: Boolean, tiempoMaximoTexto: String, errorTiempo: String?,
    onControlTiempoChange: (Boolean) -> Unit, onTiempoTextoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaSeccion("⏱️  Control del temps")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = controlTiempo, onCheckedChange = onControlTiempoChange,
                colors = CheckboxDefaults.colors(checkedColor = SnakeGreen))
            Text("Activar temps màxim", color = Color.White, fontSize = 14.sp)
        }
        OutlinedTextField(
            value = tiempoMaximoTexto, onValueChange = onTiempoTextoChange,
            label = { Text("Temps màxim (segons)", color = SnakeLightGreen.copy(alpha = 0.7f)) },
            modifier = Modifier.fillMaxWidth(), enabled = controlTiempo, singleLine = true,
            isError = errorTiempo != null,
            supportingText = errorTiempo?.let { { Text(it, color = BtnError, fontSize = 12.sp) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = camposColores()
        )
    }
}

@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(texto, color = SnakeLightGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun camposColores() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = SnakeGreen,
    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White,
    cursorColor          = SnakeGreen,
    disabledBorderColor  = Color.White.copy(alpha = 0.1f),
    disabledTextColor    = Color.White.copy(alpha = 0.3f)
)