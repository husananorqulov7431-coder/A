package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = TextPrimary,
    primaryContainer = Slate800,
    onPrimaryContainer = TextPrimary,
    secondary = SubtitleCyan,
    onSecondary = Slate950,
    secondaryContainer = Slate850,
    onSecondaryContainer = SubtitleCyan,
    tertiary = SuccessGreen,
    onTertiary = Slate950,
    background = Slate950,
    onBackground = TextPrimary,
    surface = Slate900,
    onSurface = TextPrimary,
    surfaceVariant = Slate850,
    onSurfaceVariant = TextSecondary,
    outline = Slate700
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark video player UI
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        dynamicDarkColorScheme(context)
    } else {
        DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Slate950.toArgb()
                window.navigationBarColor = Slate950.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
