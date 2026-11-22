package dev.namdev.hmi.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.theme.HmiThemeMode
import dev.namdev.hmi.ux.ProvideUxRestrictions
import dev.namdev.hmi.ux.UxRestrictions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ComponentsTest {

    @get:Rule
    val compose = createComposeRule()

    private fun show(restrictions: UxRestrictions = UxRestrictions.Unrestricted, content: @Composable () -> Unit) {
        compose.setContent {
            HmiTheme(HmiThemeMode.Day) { ProvideUxRestrictions(restrictions, content) }
        }
    }

    @Test
    fun `button meets the automotive touch target and reacts to clicks`() {
        var clicks = 0
        show { HmiButton("Add stop", onClick = { clicks++ }) }

        compose.onNodeWithText("Add stop")
            .assertHeightIsAtLeast(76.dp)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun `disabled button ignores clicks`() {
        var clicks = 0
        show { HmiButton("Offline", onClick = { clicks++ }, enabled = false) }

        compose.onNodeWithText("Offline").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }

    @Test
    fun `toggle tile is a switch that shows its state in text`() {
        show {
            var checked by remember { mutableStateOf(false) }
            HmiToggleTile("Recirculate", checked, { checked = it }, Icons.Filled.Refresh)
        }

        compose.onNodeWithText("Recirculate")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertIsOff()
            .assertTextContains("Off")
            .performClick()
            .assertIsOn()
            .assertTextContains("On")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `stepper changes by touch, and by d-pad once focused`() {
        var fan by mutableIntStateOf(3)
        show {
            HmiStepper(fan.toString(), onDecrement = { fan-- }, onIncrement = { fan++ }, Modifier.testTag("fan"))
        }

        compose.onNodeWithContentDescription("Increase").performClick()
        assertEquals(4, fan)

        val stepper = compose.onNodeWithTag("fan")
        stepper.requestFocus()
        stepper.performKeyInput { pressKey(Key.DirectionRight) }
        stepper.performKeyInput { pressKey(Key.DirectionRight) }
        assertEquals(6, fan)

        stepper.performKeyInput { pressKey(Key.DirectionLeft) }
        assertEquals(5, fan)
    }

    @Test
    fun `stepper buttons respect their limits`() {
        var fan by mutableIntStateOf(6)
        show {
            HmiStepper(fan.toString(), onDecrement = { fan-- }, onIncrement = { fan++ }, canIncrement = fan < 6)
        }

        compose.onNodeWithContentDescription("Increase").assertIsNotEnabled().performClick()

        assertEquals(6, fan)
    }

    @Test
    fun `segmented control selects exactly one option`() {
        show {
            var mode by remember { mutableStateOf("Comfort") }
            HmiSegmentedControl(listOf("Eco", "Comfort", "Sport"), mode, { mode = it })
        }

        compose.onNodeWithText("Comfort").assertIsSelected()
        compose.onNodeWithText("Sport").performClick().assertIsSelected()
        compose.onNodeWithText("Comfort").assertIsNotSelected()
    }

    @Test
    fun `list shows only the allowed number of items while driving`() {
        show(UxRestrictions.Driving.copy(maxContentItems = 2)) {
            HmiList(items = (1..5).map { "Item $it" }) { HmiListItem(it) }
        }

        compose.onNodeWithText("Item 2").assertExists()
        compose.onNodeWithText("Item 3").assertDoesNotExist()
        compose.onNodeWithText("More items are available when parked").assertExists()
    }

    @Test
    fun `list shows everything when parked`() {
        show { HmiList(items = (1..5).map { "Item $it" }) { HmiListItem(it) } }

        compose.onNodeWithText("Item 5").assertExists()
        compose.onAllNodesWithText("More items are available when parked").assertCountEquals(0)
    }

    @Test
    fun `long text is shortened while driving`() {
        show(UxRestrictions.Driving.copy(maxStringLength = 10)) { HmiListItem("Central Station, Platform Road") }

        compose.onNodeWithText("Central S…").assertExists()
    }

    @Test
    fun `text input becomes a notice while the keyboard is restricted`() {
        show(UxRestrictions.Driving) { HmiTextInput("", {}, "Search") }

        compose.onAllNodes(hasSetTextAction()).assertCountEquals(0)
        compose.onNodeWithText("Search: Typing is unavailable while driving").assertExists()
    }

    @Test
    fun `text input is editable when parked`() {
        show {
            var text by remember { mutableStateOf("") }
            HmiTextInput(text, { text = it }, "Search")
        }

        compose.onNode(hasSetTextAction()).performTextInput("Home")

        compose.onNodeWithText("Home").assertExists()
    }

    @Test
    fun `video gate hides its content while video is restricted`() {
        show(UxRestrictions.Driving) {
            UxRestricted({ it.noVideo }, "Video is paused while driving") { Text("Player") }
        }

        compose.onNodeWithText("Player").assertDoesNotExist()
        compose.onNodeWithText("Video is paused while driving").assertExists()
    }
}
