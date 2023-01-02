package cn.chitanda.app.imovie.core.design.theme

/**
 *@author: Chen
 *@createTime: 2022/11/22 14:11
 *@description:
 **/

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import cn.chitanda.app.imovie.core.design.windowsize.LocalWindowSizeClass

private val DarkColorPalette = darkColorScheme()

private val LightColorPalette = lightColorScheme()

@Composable
fun IMovieTheme(
    windowSizeClass: WindowSizeClass,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        darkTheme -> if (dynamicColor) dynamicDarkColorScheme(LocalContext.current) else DarkColorPalette
        else -> if (dynamicColor) dynamicLightColorScheme(LocalContext.current) else LightColorPalette
    }

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        MaterialTheme(colorScheme = colors, content = content)
    }
}