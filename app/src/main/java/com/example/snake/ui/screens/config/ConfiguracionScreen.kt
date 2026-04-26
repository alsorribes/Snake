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
import androidx.compose.material3.MaterialTheme
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

private val SnakeGreen      = Color(0xFF4CAF50)
private val SnakeDarkGreen  = Color(0xFF2E7D32)
private val SnakeLightGreen = Color(0xFF81C784)
private val BackgroundDark  = Color(0xFF1B1B2F)
private val SurfaceCard     = Color(0xFF252540)

// =============================================================================
// COMPOSABLE STATEFUL: ConfiguracionScreen
// Gestiona todo el estado local de la pantalla y delega en stateless.
// FIX [F]: descompuesto correctamente según criterio @Composable stateful/stateless
// =============================================================================
@Composable
fun ConfiguracionScreen(
    onEmpezar: (ConfiguracionPartida) -> Unit,
    onVolver: () -> Unit
) {
    // FIX [A]: alias con placeholder "Jugador" como valor inicial visible
    var alias           by rememberSaveable { mutableStateOf("") }
    var tamanoSeleccionado by rememberSaveable { mutableStateOf(TamanoParrilla.MEDIANA.name) }
    var controlTiempo   by rememberSaveable { mutableStateOf(false) }
    var tiempoMaximoTexto by rememberSaveable { mutableStateOf(TIEMPO_MAXIMO_DEFECTO_SEG.toString()) }
    var errorGeneral    by rememberSaveable { mutableStateOf<String?>(null) }
    // FIX [B]: error específico para el campo tiempo
    var errorTiempo     by rememberSaveable { mutableStateOf<String?>(null) }

    val tamanoParrilla = TamanoParrilla.valueOf(tamanoSeleccionado)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabecera
        Text(
            text = "⚙️  Configuración",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SnakeGreen
        )
        Text(
            text = "Ajusta los parámetros de tu partida",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.5f)
        )

        Spacer(Modifier.height(4.dp))

        // FIX [F]: cada sección es un composable stateless independiente
        SeccionAlias(
            alias = alias,
            onAliasChange = { alias = it; errorGeneral = null }
        )

        SeccionTamano(
            tamanoSeleccionado = tamanoSeleccionado,
            onTamanoChange = { tamanoSeleccionado = it; errorGeneral = null }
        )

        SeccionTiempo(
            controlTiempo = controlTiempo,
            tiempoMaximoTexto = tiempoMaximoTexto,
            errorTiempo = errorTiempo,
            onControlTiempoChange = { controlTiempo = it; errorTiempo = null },
            onTiempoTextoChange = { tiempoMaximoTexto = it; errorTiempo = null }
        )

        // Error general
        if (errorGeneral != null) {
            Text(errorGeneral!!, color = Color(0xFFEF5350), fontSize = 13.sp)
        }

        Spacer(Modifier.height(4.dp))

        // Botón Empezar
        Button(
            onClick = {
                // FIX [B]: validar tiempo antes de continuar
                if (controlTiempo) {
                    val t = tiempoMaximoTexto.toIntOrNull()
                    if (t == null || t <= 0) {
                        errorTiempo = "Introduce un número de segundos válido (> 0)"
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
                if (err != null) { errorGeneral = err } else { onEmpezar(config) }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
        ) {
            Text("▶  EMPEZAR PARTIDA", fontWeight = FontWeight.Bold,
                fontSize = 15.sp, modifier = Modifier.padding(vertical = 4.dp))
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)
        ) {
            Text("← VOLVER", fontSize = 14.sp)
        }
    }
}

// =============================================================================
// COMPOSABLE STATELESS: SeccionAlias
// FIX [A]: placeholder visible con el texto de ejemplo "Jugador"
// =============================================================================
@Composable
private fun SeccionAlias(
    alias: String,
    onAliasChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaSeccion("👤  Alias del jugador")
        OutlinedTextField(
            value = alias,
            onValueChange = onAliasChange,
            // FIX [A]: placeholder visible cuando el campo está vacío
            placeholder = { Text("Ej: Jugador", color = Color.White.copy(alpha = 0.3f)) },
            label = { Text("Alias", color = SnakeLightGreen.copy(alpha = 0.7f)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = camposColores()
        )
    }
}

// =============================================================================
// COMPOSABLE STATELESS: SeccionTamano
// =============================================================================
@Composable
private fun SeccionTamano(
    tamanoSeleccionado: String,
    onTamanoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EtiquetaSeccion("📐  Tamaño de la parrilla")
        TamanoParrilla.entries.forEach { tamano ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tamanoSeleccionado == tamano.name,
                    onClick = { onTamanoChange(tamano.name) },
                    colors = RadioButtonDefaults.colors(selectedColor = SnakeGreen)
                )
                Column {
                    Text(tamano.etiqueta, color = Color.White, fontSize = 14.sp)
                    Text(
                        "${tamano.filas}×${tamano.columnas} casillas",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// =============================================================================
// COMPOSABLE STATELESS: SeccionTiempo
// FIX [B]: muestra error si el tiempo no es numérico cuando está activo
// =============================================================================
@Composable
private fun SeccionTiempo(
    controlTiempo: Boolean,
    tiempoMaximoTexto: String,
    errorTiempo: String?,
    onControlTiempoChange: (Boolean) -> Unit,
    onTiempoTextoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaSeccion("⏱️  Control de tiempo")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = controlTiempo,
                onCheckedChange = onControlTiempoChange,
                colors = CheckboxDefaults.colors(checkedColor = SnakeGreen)
            )
            Text("Activar tiempo máximo", color = Color.White, fontSize = 14.sp)
        }

        OutlinedTextField(
            value = tiempoMaximoTexto,
            onValueChange = onTiempoTextoChange,
            label = { Text("Tiempo máximo (segundos)", color = SnakeLightGreen.copy(alpha = 0.7f)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = controlTiempo,
            singleLine = true,
            isError = errorTiempo != null,
            supportingText = errorTiempo?.let {
                { Text(it, color = Color(0xFFEF5350), fontSize = 12.sp) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = camposColores()
        )
    }
}

// ── Helpers compartidos ───────────────────────────────────────────────────────

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