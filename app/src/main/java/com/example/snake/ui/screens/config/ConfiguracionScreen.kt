package com.example.snake.ui.screens.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.R
import com.example.snake.model.TamanoParrilla
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.BtnError
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen
import com.example.snake.viewmodel.ConfiguracionUiState
import com.example.snake.viewmodel.GameViewModel

@Composable
fun ConfiguracionScreen(
    configuracion: ConfiguracionUiState,
    onAliasChange: (String) -> Unit,
    onTamanoChange: (TamanoParrilla) -> Unit,
    onControlTiempoChange: (Boolean) -> Unit,
    onTiempoTextoChange: (String) -> Unit,
    onEmpezar: () -> Unit,
    onVolver: () -> Unit
) {
    val errorAliasMsg = when (configuracion.errorAlias) {
        GameViewModel.ERROR_ALIAS_BUIT -> stringResource(R.string.error_alias_vacio)
        else -> configuracion.errorAlias
    }
    val errorTiempoMsg = when (configuracion.errorTiempo) {
        GameViewModel.ERROR_TIEMPO_INVALIDO -> stringResource(R.string.error_tiempo_invalido)
        else -> configuracion.errorTiempo
    }

    val esLandscape =
        LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        if (esLandscape) {
            // ── Landscape: dues columnes, sense scroll ─────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Títol compacte
                Text(
                    stringResource(R.string.config_titulo),
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = SnakeGreen
                )

                Row(
                    modifier              = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Columna esquerra: àlies + mida
                    Column(
                        modifier            = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SeccionAlias(
                            alias         = configuracion.alias,
                            errorMsg      = errorAliasMsg,
                            onAliasChange = onAliasChange
                        )
                        SeccionTamano(
                            tamanoSeleccionado = configuracion.tamanoParrilla,
                            onTamanoChange     = onTamanoChange
                        )
                    }

                    // Columna dreta: temps + botons
                    Column(
                        modifier            = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SeccionTiempo(
                            controlTiempo         = configuracion.controlTiempo,
                            tiempoMaximoTexto     = configuracion.tiempoMaximoTexto,
                            errorMsg              = errorTiempoMsg,
                            onControlTiempoChange = onControlTiempoChange,
                            onTiempoTextoChange   = onTiempoTextoChange
                        )
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick  = onEmpezar,
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
                        ) {
                            Text(
                                stringResource(R.string.config_empezar),
                                fontWeight = FontWeight.Bold, fontSize = 14.sp
                            )
                        }
                        OutlinedButton(
                            onClick  = onVolver,
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)
                        ) {
                            Text(stringResource(R.string.config_volver), fontSize = 13.sp)
                        }
                    }
                }
            }

        } else {
            // ── Portrait: columna amb scroll ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    stringResource(R.string.config_titulo),
                    fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = SnakeGreen
                )
                Text(
                    stringResource(R.string.config_subtitulo),
                    fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(4.dp))
                SeccionAlias(
                    alias         = configuracion.alias,
                    errorMsg      = errorAliasMsg,
                    onAliasChange = onAliasChange
                )
                SeccionTamano(
                    tamanoSeleccionado = configuracion.tamanoParrilla,
                    onTamanoChange     = onTamanoChange
                )
                SeccionTiempo(
                    controlTiempo         = configuracion.controlTiempo,
                    tiempoMaximoTexto     = configuracion.tiempoMaximoTexto,
                    errorMsg              = errorTiempoMsg,
                    onControlTiempoChange = onControlTiempoChange,
                    onTiempoTextoChange   = onTiempoTextoChange
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick  = onEmpezar,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
                ) {
                    Text(
                        stringResource(R.string.config_empezar),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        modifier   = Modifier.padding(vertical = 4.dp)
                    )
                }
                OutlinedButton(
                    onClick  = onVolver,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen)
                ) {
                    Text(stringResource(R.string.config_volver), fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun SeccionAlias(
    alias: String,
    errorMsg: String?,
    onAliasChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaSeccion(stringResource(R.string.config_alias_seccio))
        OutlinedTextField(
            value         = alias,
            onValueChange = onAliasChange,
            placeholder   = {
                Text(stringResource(R.string.alias_placeholder), color = Color.White.copy(alpha = 0.3f))
            },
            label         = {
                Text(stringResource(R.string.config_alias_label), color = SnakeLightGreen.copy(alpha = 0.7f))
            },
            modifier       = Modifier.fillMaxWidth(),
            singleLine     = true,
            isError        = errorMsg != null,
            supportingText = errorMsg?.let { { Text(it, color = BtnError, fontSize = 12.sp) } },
            colors         = camposColores()
        )
    }
}

@Composable
private fun SeccionTamano(
    tamanoSeleccionado: TamanoParrilla,
    onTamanoChange: (TamanoParrilla) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EtiquetaSeccion(stringResource(R.string.config_tamano_label))
        TamanoParrilla.entries.forEach { tamano ->
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tamanoSeleccionado == tamano,
                    onClick  = { onTamanoChange(tamano) },
                    colors   = RadioButtonDefaults.colors(selectedColor = SnakeGreen)
                )
                Column {
                    Text(tamano.etiqueta, color = Color.White, fontSize = 14.sp)
                    Text(
                        stringResource(R.string.config_tamano_caselles, tamano.filas, tamano.columnas),
                        color    = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionTiempo(
    controlTiempo: Boolean,
    tiempoMaximoTexto: String,
    errorMsg: String?,
    onControlTiempoChange: (Boolean) -> Unit,
    onTiempoTextoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        EtiquetaSeccion(stringResource(R.string.config_tiempo_label))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked         = controlTiempo,
                onCheckedChange = onControlTiempoChange,
                colors          = CheckboxDefaults.colors(checkedColor = SnakeGreen)
            )
            Text(stringResource(R.string.config_tiempo_checkbox), color = Color.White, fontSize = 14.sp)
        }
        OutlinedTextField(
            value         = tiempoMaximoTexto,
            onValueChange = onTiempoTextoChange,
            label         = {
                Text(stringResource(R.string.config_tiempo_campo), color = SnakeLightGreen.copy(alpha = 0.7f))
            },
            modifier        = Modifier.fillMaxWidth(),
            enabled         = controlTiempo,
            singleLine      = true,
            isError         = errorMsg != null,
            supportingText  = errorMsg?.let { { Text(it, color = BtnError, fontSize = 12.sp) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors          = camposColores()
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