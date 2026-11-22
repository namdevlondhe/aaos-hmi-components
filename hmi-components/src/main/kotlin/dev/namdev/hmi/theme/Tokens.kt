package dev.namdev.hmi.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Colour roles of the kit. Every `onX` colour is tested to reach WCAG 4.5:1 against `X`,
 * and the focus ring 3:1 against the surfaces it is drawn on (see ContrastTest).
 */
@Immutable
data class HmiColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val outline: Color,
    val primary: Color,
    val onPrimary: Color,
    /** "On" state of toggles: distinct from [primary] so state never relies on one hue alone. */
    val active: Color,
    val onActive: Color,
    val warning: Color,
    val onWarning: Color,
    val critical: Color,
    val onCritical: Color,
    /** Rotary/D-pad focus indicator. */
    val focusRing: Color,
    val disabledContent: Color,
    val isNight: Boolean,
)

val DayColors = HmiColors(
    background = Color(0xFFEEF1F5),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFE2E7EE),
    onSurface = Color(0xFF11151B),
    onSurfaceMuted = Color(0xFF465060),
    outline = Color(0xFF98A2B0),
    primary = Color(0xFF1B57C2),
    onPrimary = Color(0xFFFFFFFF),
    active = Color(0xFF07705A),
    onActive = Color(0xFFFFFFFF),
    warning = Color(0xFFF5B400),
    onWarning = Color(0xFF221800),
    critical = Color(0xFFB3261E),
    onCritical = Color(0xFFFFFFFF),
    focusRing = Color(0xFF0061D6),
    disabledContent = Color(0xFF8B94A1),
    isNight = false,
)

/** Night palette: true-black background to avoid glare in a dark cabin. */
val NightColors = HmiColors(
    background = Color(0xFF000000),
    surface = Color(0xFF14181E),
    surfaceRaised = Color(0xFF222831),
    onSurface = Color(0xFFE8ECF2),
    onSurfaceMuted = Color(0xFFA3ADBB),
    outline = Color(0xFF4A5361),
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF0A1931),
    active = Color(0xFF4FD1A5),
    onActive = Color(0xFF00281C),
    warning = Color(0xFFFFC94D),
    onWarning = Color(0xFF2B1F00),
    critical = Color(0xFFFF8A80),
    onCritical = Color(0xFF3B0000),
    focusRing = Color(0xFF6FB6FF),
    disabledContent = Color(0xFF5C6573),
    isNight = true,
)

/** Type scale sized for arm's-length reading in a moving vehicle (body text never below 24sp). */
@Immutable
data class HmiTypography(
    val display: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
)

val DefaultTypography = HmiTypography(
    // Tabular figures keep values like 21.5° from shifting sideways as they change.
    display = TextStyle(
        fontSize = 52.sp,
        lineHeight = 60.sp,
        fontWeight = FontWeight.Medium,
        fontFeatureSettings = "tnum",
    ),
    title = TextStyle(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.SemiBold),
    body = TextStyle(fontSize = 26.sp, lineHeight = 34.sp, fontWeight = FontWeight.Normal),
    label = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium),
    caption = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Normal),
)

/** Sizes. [touchTarget] follows the Android Automotive minimum of 76dp. */
@Immutable
data class HmiDimens(
    val touchTarget: Dp = 76.dp,
    val spacingXs: Dp = 4.dp,
    val spacingS: Dp = 8.dp,
    val spacingM: Dp = 16.dp,
    val spacingL: Dp = 24.dp,
    val corner: Dp = 20.dp,
    val iconSize: Dp = 36.dp,
    val focusRingWidth: Dp = 4.dp,
    /** Gap between a control and its focus ring, so the ring stays visible on any fill colour. */
    val focusRingGap: Dp = 3.dp,
)
