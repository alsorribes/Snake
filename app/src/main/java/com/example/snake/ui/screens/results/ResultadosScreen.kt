package com.example.snake.ui.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snake.R
import com.example.snake.model.LogPartida
import com.example.snake.model.ResultadoPartida
import com.example.snake.ui.components.GridBackground
import com.example.snake.ui.theme.BackgroundDark
import com.example.snake.ui.theme.BtnDanger
import com.example.snake.ui.theme.BtnError
import com.example.snake.ui.theme.SnakeDarkGreen
import com.example.snake.ui.theme.SnakeGreen
import com.example.snake.ui.theme.SnakeLightGreen

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

@Composable
fun ResultadosScreen(
    log: LogPartida?,
    emailDestinatario: String,
    onEnviarEmail: (String) -> Unit,
    onNuevaPartida: () -> Unit,
    onSalir: () -> Unit,
    onEmailCambiado: (String) -> Unit
) {
    if (log == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.resultados_sin_datos), color = Color.White, fontSize = 16.sp)
                Button(
                    onClick  = onNuevaPartida,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen)
                ) { Text(stringResource(R.string.resultados_nueva_partida)) }
            }
        }
        return
    }

    val clauLog = "${log.alias}_${log.resultado}_${log.tiempoTotalSeg}"
    var textoLog   by rememberSaveable(clauLog) { mutableStateOf(log.generarTexto()) }
    var email      by rememberSaveable(emailDestinatario) { mutableStateOf(emailDestinatario) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        try { focusRequester.requestFocus() } catch (_: Exception) {}
    }

    val esLandscape =
        LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeDrawingPadding()
    ) {
        GridBackground()

        if (esLandscape) {
            // ── Landscape: dues columnes, sense scroll ─────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Columna esquerra: títol + estat + data + log
                Column(
                    modifier            = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.resultados_titulo),
                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = SnakeGreen
                    )
                    TarjetaEstatResultat(log.resultado)
                    EtiquetaResultado(stringResource(R.string.resultados_fecha_label))
                    OutlinedTextField(
                        value         = log.fechaHoraFormateada(),
                        onValueChange = {},
                        readOnly      = true,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = camposColores()
                    )
                    EtiquetaResultado(stringResource(R.string.resultados_log_label))
                    OutlinedTextField(
                        value         = textoLog,
                        onValueChange = { textoLog = it },
                        modifier      = Modifier.fillMaxWidth().weight(1f),
                        colors        = camposColores()
                    )
                }

                // Columna dreta: email + botons
                Column(
                    modifier            = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EtiquetaResultado(stringResource(R.string.resultados_email_label))
                    OutlinedTextField(
                        value         = email,
                        onValueChange = {
                            email = it
                            emailError = null
                            onEmailCambiado(it)
                        },
                        modifier        = Modifier.fillMaxWidth().focusRequester(focusRequester),
                        singleLine      = true,
                        isError         = emailError != null,
                        supportingText  = emailError?.let { { Text(it, color = BtnError, fontSize = 12.sp) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors          = camposColores()
                    )
                    Button(
                        onClick = {
                            if (!EMAIL_REGEX.matches(email)) {
                                emailError = "Introdueix un email vàlid"
                            } else {
                                emailError = null
                                onEnviarEmail(email)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.resultados_enviar), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(
                        onClick  = onNuevaPartida,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, SnakeLightGreen.copy(alpha = 0.4f))
                    ) {
                        Text(stringResource(R.string.resultados_nueva_partida))
                    }
                    Button(
                        onClick  = onSalir,
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = BtnDanger),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.resultados_salir), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

        } else {
            // ── Portrait: columna amb scroll ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .background(Color.Black.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    stringResource(R.string.resultados_titulo),
                    fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = SnakeGreen
                )
                Text(
                    stringResource(R.string.resultados_resum),
                    fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f)
                )
                TarjetaEstatResultat(log.resultado)
                EtiquetaResultado(stringResource(R.string.resultados_fecha_label))
                OutlinedTextField(
                    value         = log.fechaHoraFormateada(),
                    onValueChange = {},
                    readOnly      = true,
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = camposColores()
                )
                EtiquetaResultado(stringResource(R.string.resultados_log_label))
                OutlinedTextField(
                    value         = textoLog,
                    onValueChange = { textoLog = it },
                    modifier      = Modifier.fillMaxWidth(),
                    minLines      = 7,
                    colors        = camposColores()
                )
                EtiquetaResultado(stringResource(R.string.resultados_email_label))
                OutlinedTextField(
                    value         = email,
                    onValueChange = {
                        email = it
                        emailError = null
                        onEmailCambiado(it)
                    },
                    modifier        = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    singleLine      = true,
                    isError         = emailError != null,
                    supportingText  = emailError?.let { { Text(it, color = BtnError, fontSize = 12.sp) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors          = camposColores()
                )
                Button(
                    onClick = {
                        if (!EMAIL_REGEX.matches(email)) {
                            emailError = "Introdueix un email vàlid"
                        } else {
                            emailError = null
                            onEnviarEmail(email)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = SnakeDarkGreen),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.resultados_enviar),
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick  = onNuevaPartida,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = SnakeLightGreen),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, SnakeLightGreen.copy(alpha = 0.4f))
                ) {
                    Text(stringResource(R.string.resultados_nueva_partida), modifier = Modifier.padding(vertical = 4.dp))
                }
                Button(
                    onClick  = onSalir,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = BtnDanger),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.resultados_salir),
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaEstatResultat(resultado: ResultadoPartida?) {
    val (titol, color) = when (resultado) {
        ResultadoPartida.GANADA           -> stringResource(R.string.resultados_guanyat)         to SnakeGreen
        ResultadoPartida.PERDIDA_COLISION -> stringResource(R.string.resultados_perdut_collisio) to BtnDanger
        ResultadoPartida.PERDIDA_TIEMPO   -> stringResource(R.string.resultados_perdut_temps)    to BtnError
        null                              -> stringResource(R.string.resultados_no_resultat)     to SnakeLightGreen
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(titol, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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