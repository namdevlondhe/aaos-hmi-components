package dev.namdev.hmi.catalog

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.requestFocus
import com.github.takahirom.roborazzi.captureRoboImage
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.theme.HmiThemeMode
import dev.namdev.hmi.ux.ProvideUxRestrictions
import dev.namdev.hmi.ux.UxRestrictions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** A tall landscape canvas, so the whole catalog fits in one screenshot. */
const val CATALOG_QUALIFIERS = "w1280dp-h1560dp-mdpi"

/**
 * Renders the catalog on the JVM (no emulator) into docs/screenshots.
 * Record with `./gradlew :catalog:recordRoborazziDebug`; a plain test run only checks that rendering works.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = CATALOG_QUALIFIERS)
class CatalogScreenshotTest {

    @get:Rule
    val compose = createComposeRule()

    private fun show(mode: HmiThemeMode, driving: Boolean) {
        compose.setContent {
            HmiTheme(mode) {
                ProvideUxRestrictions(if (driving) SimulatedDriving else UxRestrictions.Unrestricted) {
                    CatalogScreen(mode, {}, driving, {})
                }
            }
        }
    }

    private fun capture(name: String) {
        compose.onRoot().captureRoboImage("../docs/screenshots/$name.png")
    }

    @Test
    fun day() {
        show(HmiThemeMode.Day, driving = false)
        capture("catalog-day")
    }

    @Test
    fun night() {
        show(HmiThemeMode.Night, driving = false)
        capture("catalog-night")
    }

    @Test
    fun driving() {
        show(HmiThemeMode.Night, driving = true)
        capture("catalog-driving")
    }

    @Test
    fun rotaryFocus() {
        show(HmiThemeMode.Night, driving = false)
        compose.onNodeWithTag("temperature").requestFocus()
        capture("rotary-focus")
    }
}
