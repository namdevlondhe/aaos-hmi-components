package dev.namdev.hmi.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.namdev.hmi.components.HmiBanner
import dev.namdev.hmi.components.HmiBannerLevel
import dev.namdev.hmi.components.HmiButton
import dev.namdev.hmi.components.HmiButtonStyle
import dev.namdev.hmi.components.HmiList
import dev.namdev.hmi.components.HmiListItem
import dev.namdev.hmi.components.HmiSegmentedControl
import dev.namdev.hmi.components.HmiStepper
import dev.namdev.hmi.components.HmiTextInput
import dev.namdev.hmi.components.HmiToggleTile
import dev.namdev.hmi.components.UxRestricted
import dev.namdev.hmi.theme.HmiTheme
import java.util.Locale

// The demo content of each catalog section. Each keeps its own state, like a real screen would.

@Composable
internal fun ClimateSteppers() {
    var temperature by remember { mutableFloatStateOf(21.5f) }
    var fan by remember { mutableIntStateOf(3) }
    Column(verticalArrangement = Arrangement.spacedBy(HmiTheme.dimens.spacingXs)) {
        HmiStepper(
            value = String.format(Locale.US, "%.1f°", temperature),
            label = "Driver",
            onDecrement = { temperature -= 0.5f },
            onIncrement = { temperature += 0.5f },
            canDecrement = temperature > 16f,
            canIncrement = temperature < 28f,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("temperature"),
        )
        HmiStepper(
            value = fan.toString(),
            label = "Fan",
            onDecrement = { fan-- },
            onIncrement = { fan++ },
            canDecrement = fan > 1,
            canIncrement = fan < 6,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
internal fun ToggleTiles() {
    var recirculation by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(true) }
    var profile by remember { mutableStateOf(false) }
    val gap = Arrangement.spacedBy(HmiTheme.dimens.spacingXs)
    Column(verticalArrangement = gap) {
        Row(horizontalArrangement = gap) {
            HmiToggleTile(
                "Recirculate",
                recirculation,
                { recirculation = it },
                Icons.Filled.Refresh,
                Modifier.weight(1f),
            )
            HmiToggleTile(
                "Do not disturb",
                notifications,
                { notifications = it },
                Icons.Filled.Notifications,
                Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = gap) {
            HmiToggleTile("Door lock", locked, { locked = it }, Icons.Filled.Lock, Modifier.weight(1f))
            HmiToggleTile(
                "Guest profile",
                profile,
                { profile = it },
                Icons.Filled.Person,
                Modifier.weight(1f),
                enabled = false,
            )
        }
    }
}

private enum class DriveMode { Eco, Comfort, Sport }

@Composable
internal fun DriveModes() {
    var mode by remember { mutableStateOf(DriveMode.Comfort) }
    HmiSegmentedControl(
        options = DriveMode.entries,
        selected = mode,
        onSelect = { mode = it },
        modifier = Modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Buttons() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(HmiTheme.dimens.spacingXs),
        verticalArrangement = Arrangement.spacedBy(HmiTheme.dimens.spacingXs),
    ) {
        HmiButton("Add stop", onClick = {}, icon = Icons.Filled.Add)
        HmiButton("Details", onClick = {}, style = HmiButtonStyle.Secondary)
        HmiButton("End trip", onClick = {}, style = HmiButtonStyle.Critical)
        HmiButton("Offline", onClick = {}, enabled = false)
    }
}

@Composable
internal fun Banners() {
    Column(verticalArrangement = Arrangement.spacedBy(HmiTheme.dimens.spacingS)) {
        HmiBanner("Charging stop added to the route", level = HmiBannerLevel.Info)
        HmiBanner("Rear left tyre pressure is low", title = "Check tyres", level = HmiBannerLevel.Warning)
        HmiBanner("Brake system fault: stop safely", level = HmiBannerLevel.Critical)
    }
}

private val destinations = listOf(
    "Central Station, Platform Road" to "12 min · 6.4 km",
    "City Library and Community Learning Centre" to "18 min · 9.1 km",
    "Riverside Charging Hub" to "7 min · 3.2 km",
    "Airport Terminal 2 Departures" to "35 min · 28 km",
    "Office" to "22 min · 14 km",
    "Home" to "16 min · 8.7 km",
)

@Composable
internal fun Destinations() {
    HmiList(
        items = destinations,
        key = { it.first },
        modifier = Modifier.height(360.dp),
    ) { (name, eta) ->
        HmiListItem(title = name, supportingText = eta, icon = Icons.Filled.Place, onClick = {})
    }
}

@Composable
internal fun Search() {
    var query by remember { mutableStateOf("") }
    HmiTextInput(value = query, onValueChange = { query = it }, label = "Search destinations")
}

@Composable
internal fun Video() {
    UxRestricted(blockedWhen = { it.noVideo }, message = "Video is paused while driving") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(HmiTheme.dimens.corner))
                .background(HmiTheme.colors.surfaceRaised),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Video", modifier = Modifier.size(64.dp))
        }
    }
}
