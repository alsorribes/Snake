package com.example.snake.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// FIX [F4]: esquemas de color con verdes Snake en lugar de los morados de Material por defecto.
// Así MaterialTheme.colorScheme.error, .primary, etc. son coherentes con el tema.
private val DarkColorScheme = darkColorScheme(
    primary   = SnakePrimary,
    secondary = SnakeSecondary,
    error     = SnakeError,
    surface   = SnakeSurface,
    onSurface = SnakeOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary   = SnakePrimary,
    secondary = SnakeSecondary,
    error     = SnakeError,
    surface   = SnakeSurface,
    onSurface = SnakeOnSurface
)

@Composable
fun SnakeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // dynamicColor=false: preserva la identidad visual del juego.
    // Con true en Android 12+, el sistema sobreescribiría los verdes.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}