package dev.namdev.hmi.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.namdev.hmi.components.HmiButton
import dev.namdev.hmi.components.HmiButtonStyle
import dev.namdev.hmi.components.HmiSegmentedControl
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.theme.HmiThemeMode
import dev.namdev.hmi.ux.LocalUxRestrictions
import dev.namdev.hmi.ux.UxRestrictions

/** The AAOS driving defaults, with a low list limit so the effect is visible in a short demo list. */
val SimulatedDriving = UxRestrictions.Driving.copy(maxStringLength = 24, maxContentItems = 3)

@Composable
fun CatalogScreen(
    themeMode: HmiThemeMode,
    onThemeModeChange: (HmiThemeMode) -> Unit,
    simulateDriving: Boolean,
    onSimulateDrivingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .safeDrawingPadding()
            .padding(horizontal = dimens.spacingL, vertical = dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingM),
    ) {
        TopBar(themeMode, onThemeModeChange, simulateDriving, onSimulateDrivingChange)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 520.dp),
            horizontalArrangement = Arrangement.spacedBy(dimens.spacingM),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingM),
            contentPadding = PaddingValues(bottom = dimens.spacingL),
        ) {
            item { Section("Stepper (rotary adjustable)") { ClimateSteppers() } }
            item { Section("Toggle tiles") { ToggleTiles() } }
            item { Section("Segmented control") { DriveModes() } }
            item { Section("Buttons") { Buttons() } }
            item { Section("Banners") { Banners() } }
            item { Section("List (content limit while driving)") { Destinations() } }
            item { Section("Text input (keyboard restriction)") { Search() } }
            item { Section("Video (UX gate)") { Video() } }
        }
    }
}

@Composable
private fun TopBar(
    themeMode: HmiThemeMode,
    onThemeModeChange: (HmiThemeMode) -> Unit,
    simulateDriving: Boolean,
    onSimulateDrivingChange: (Boolean) -> Unit,
) {
    val dimens = HmiTheme.dimens
    val restricted = LocalUxRestrictions.current.requiresDistractionOptimization
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingL),
    ) {
        Column(Modifier.weight(1f)) {
            Text("HMI components", style = HmiTheme.typography.title, color = HmiTheme.colors.onSurface)
            Text(
                if (restricted) "Driving: distraction-optimised layout" else "Parked: all content available",
                style = HmiTheme.typography.caption,
                color = HmiTheme.colors.onSurfaceMuted,
            )
        }
        HmiSegmentedControl(
            options = HmiThemeMode.entries,
            selected = themeMode,
            onSelect = onThemeModeChange,
            label = { if (it == HmiThemeMode.FollowSystem) "Auto" else it.name },
            modifier = Modifier.width(420.dp),
        )
        HmiButton(
            text = if (simulateDriving) "Stop driving" else "Simulate driving",
            onClick = { onSimulateDrivingChange(!simulateDriving) },
            style = if (simulateDriving) HmiButtonStyle.Critical else HmiButtonStyle.Secondary,
            icon = Icons.Filled.PlayArrow,
            modifier = Modifier.testTag("simulate-driving"),
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    val dimens = HmiTheme.dimens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.corner + dimens.spacingS))
            .background(HmiTheme.colors.surface)
            .padding(dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingS),
    ) {
        Text(title, style = HmiTheme.typography.label, color = HmiTheme.colors.onSurfaceMuted)
        content()
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
private fun CatalogPreview() {
    HmiTheme(HmiThemeMode.Night) {
        CatalogScreen(HmiThemeMode.Night, {}, simulateDriving = false, onSimulateDrivingChange = {})
    }
}
