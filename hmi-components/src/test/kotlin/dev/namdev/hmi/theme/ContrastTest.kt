package dev.namdev.hmi.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** WCAG 2.x contrast checks for every colour pairing the components actually draw. */
class ContrastTest {

    @Test
    fun `day palette meets contrast requirements`() = assertPalette(DayColors)

    @Test
    fun `night palette meets contrast requirements`() = assertPalette(NightColors)

    private fun assertPalette(c: HmiColors) {
        val text = listOf(
            "onSurface/surface" to (c.onSurface to c.surface),
            "onSurface/background" to (c.onSurface to c.background),
            "onSurface/surfaceRaised" to (c.onSurface to c.surfaceRaised),
            "onSurfaceMuted/surface" to (c.onSurfaceMuted to c.surface),
            "onSurfaceMuted/surfaceRaised" to (c.onSurfaceMuted to c.surfaceRaised),
            "onPrimary/primary" to (c.onPrimary to c.primary),
            "onActive/active" to (c.onActive to c.active),
            "onWarning/warning" to (c.onWarning to c.warning),
            "onCritical/critical" to (c.onCritical to c.critical),
        )
        text.forEach { (name, pair) -> assertContrast(name, pair, TEXT_MINIMUM) }

        // Non-text UI (WCAG 1.4.11): the focus ring against what surrounds it.
        listOf(
            "focusRing/background" to (c.focusRing to c.background),
            "focusRing/surface" to (c.focusRing to c.surface),
        ).forEach { (name, pair) -> assertContrast(name, pair, NON_TEXT_MINIMUM) }
    }

    private fun assertContrast(name: String, pair: Pair<Color, Color>, minimum: Double) {
        val ratio = contrast(pair.first, pair.second)
        assertTrue("$name contrast is %.2f:1, needs %.1f:1".format(ratio, minimum), ratio >= minimum)
    }

    private fun contrast(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun luminance(color: Color): Double =
        0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)

    private fun linear(channel: Float): Double =
        if (channel <= 0.03928f) channel / 12.92 else ((channel + 0.055) / 1.055).pow(2.4)

    private companion object {
        const val TEXT_MINIMUM = 4.5
        const val NON_TEXT_MINIMUM = 3.0
    }
}
