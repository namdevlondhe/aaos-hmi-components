package dev.namdev.hmi.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

enum class HmiThemeMode {
    /**
     * Follow the system night mode. On Android Automotive the car service drives it from the
     * vehicle's day/night sensor, so this is the right default in a car.
     */
    FollowSystem,
    Day,
    Night,
}

private val LocalHmiColors = staticCompositionLocalOf { DayColors }
private val LocalHmiTypography = staticCompositionLocalOf { DefaultTypography }
private val LocalHmiDimens = staticCompositionLocalOf { HmiDimens() }

/**
 * Root of the kit. Provides the HMI tokens and a matching Material 3 theme, so plain Material
 * components placed inside an HMI screen pick up the same colours.
 */
@Composable
fun HmiTheme(
    mode: HmiThemeMode = HmiThemeMode.FollowSystem,
    typography: HmiTypography = DefaultTypography,
    dimens: HmiDimens = HmiDimens(),
    content: @Composable () -> Unit,
) {
    val night = when (mode) {
        HmiThemeMode.FollowSystem -> isSystemInDarkTheme()
        HmiThemeMode.Day -> false
        HmiThemeMode.Night -> true
    }
    val colors = if (night) NightColors else DayColors
    CompositionLocalProvider(
        LocalHmiColors provides colors,
        LocalHmiTypography provides typography,
        LocalHmiDimens provides dimens,
    ) {
        MaterialTheme(colorScheme = colors.toColorScheme()) {
            CompositionLocalProvider(
                LocalContentColor provides colors.onSurface,
                LocalTextStyle provides typography.body,
                content = content,
            )
        }
    }
}

/** Access to the current tokens: `HmiTheme.colors.primary`, `HmiTheme.dimens.touchTarget` … */
object HmiTheme {
    val colors: HmiColors
        @Composable @ReadOnlyComposable
        get() = LocalHmiColors.current

    val typography: HmiTypography
        @Composable @ReadOnlyComposable
        get() = LocalHmiTypography.current

    val dimens: HmiDimens
        @Composable @ReadOnlyComposable
        get() = LocalHmiDimens.current
}

internal fun HmiColors.toColorScheme(): ColorScheme {
    val base = if (isNight) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        background = background,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceRaised,
        onSurfaceVariant = onSurfaceMuted,
        outline = outline,
        error = critical,
        onError = onCritical,
    )
}
