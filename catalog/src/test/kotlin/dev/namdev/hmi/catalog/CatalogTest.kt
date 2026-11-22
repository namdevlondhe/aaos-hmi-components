package dev.namdev.hmi.catalog

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * End-to-end through the real CatalogApp, including :hmi-car. Robolectric is not an automotive device,
 * so rememberCarUxRestrictions must fall back to "unrestricted" instead of touching the Car API.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = CATALOG_QUALIFIERS)
class CatalogTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `starts unrestricted when not on a car`() {
        compose.setContent { CatalogApp() }

        compose.onNodeWithText("Parked: all content available").assertExists()
        compose.onNodeWithText("Airport Terminal 2 Departures").assertExists()
        compose.onNodeWithText("Search destinations").assertExists()
    }

    @Test
    fun `simulated driving restricts the whole catalog`() {
        compose.setContent { CatalogApp() }

        compose.onNodeWithTag("simulate-driving").performClick()

        compose.onNodeWithText("Driving: distraction-optimised layout").assertExists()
        compose.onNodeWithText("Airport Terminal 2 Departures").assertDoesNotExist()
        compose.onNodeWithText("More items are available when parked").assertExists()
        compose.onNodeWithText("Search destinations: Typing is unavailable while driving").assertExists()
        compose.onNodeWithText("Video is paused while driving").assertExists()
    }
}
