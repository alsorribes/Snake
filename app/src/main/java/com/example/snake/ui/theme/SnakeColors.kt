package com.example.snake.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// FIX [E1]: Colores centralizados del tema Snake.
// Antes cada pantalla redefinía sus propias constantes de color (código redundante).
// Ahora todas importan desde aquí → máxima cohesión, mínimo acoplamiento.
// =============================================================================

// Verdes principales
val SnakeGreen      = Color(0xFF4CAF50)
val SnakeDarkGreen  = Color(0xFF2E7D32)
val SnakeLightGreen = Color(0xFF81C784)

// Fondos
val BackgroundDark = Color(0xFF1B1B2F)
val SurfaceCard    = Color(0xFF252540)

// Tablero de juego
val BoardBackground = Color(0xFF101820)
val BoardGrid       = Color(0xFF2A3B47)

// Elementos del juego
val SnakeHead  = Color(0xFF4CAF50)  // mismo que SnakeGreen — alias semántico
val SnakeBody  = Color(0xFF81C784)  // mismo que SnakeLightGreen — alias semántico
val AppleRed   = Color(0xFFE53935)

// Tiempo
val TimeNoControl   = Color(0xFF2196F3)  // Azul — sin control de tiempo
val TimeWithControl = Color(0xFFF44336)  // Rojo — con control de tiempo

// Acciones
val BtnDanger  = Color(0xFFB71C1C)
val BtnNeutral = Color(0xFF37474F)
val BtnError   = Color(0xFFEF5350)

// Material theme colors — FIX [F4]: verdes en lugar de morados
// Se usan en DarkColorScheme / LightColorScheme de Theme.kt
// para que MaterialTheme.colorScheme.primary, .error, etc.
// sean coherentes con el tema Snake
val SnakePrimary   = Color(0xFF4CAF50)  // verde principal
val SnakeSecondary = Color(0xFF2E7D32)  // verde oscuro
val SnakeError     = Color(0xFFEF5350)  // rojo de error
val SnakeSurface   = Color(0xFF1B1B2F)  // fondo oscuro
val SnakeOnSurface = Color(0xFFFFFFFF)  // texto sobre fondo oscuro