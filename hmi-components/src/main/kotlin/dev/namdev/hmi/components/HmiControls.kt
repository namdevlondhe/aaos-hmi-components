package dev.namdev.hmi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import dev.namdev.hmi.focus.hmiFocusRing
import dev.namdev.hmi.focus.rotaryAdjustable
import dev.namdev.hmi.theme.HmiTheme

/**
 * A large on/off tile (A/C, recirculation, seat heating …). The state is shown by colour *and* by text,
 * so it is readable at a glance and without colour vision.
 */
@Composable
fun HmiToggleTile(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onText: String = "On",
    offText: String = "Off",
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    val interaction = remember { MutableInteractionSource() }
    val (container, content) = when {
        !enabled -> colors.surfaceRaised to colors.disabledContent
        checked -> colors.active to colors.onActive
        else -> colors.surfaceRaised to colors.onSurface
    }

    Column(
        modifier = modifier
            .hmiFocusRing(interaction)
            .defaultMinSize(minWidth = dimens.touchTarget * 2, minHeight = dimens.touchTarget * 2)
            .clip(RoundedCornerShape(dimens.corner))
            .background(container)
            .toggleable(
                value = checked,
                interactionSource = interaction,
                indication = ripple(),
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingXs),
    ) {
        Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(dimens.iconSize))
        Text(label, style = HmiTheme.typography.label, color = content)
        Text(if (checked) onText else offText, style = HmiTheme.typography.caption, color = content)
    }
}

/**
 * A value with -/+ buttons, e.g. temperature or fan speed.
 *
 * For rotary and D-pad users the whole stepper is a single focus stop: once focused, left/right or turning
 * the knob changes the value. The -/+ buttons are for touch only and are skipped by focus traversal.
 */
@Composable
fun HmiStepper(
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    canDecrement: Boolean = true,
    canIncrement: Boolean = true,
    decrementDescription: String = "Decrease",
    incrementDescription: String = "Increase",
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .hmiFocusRing(interaction)
            .clip(RoundedCornerShape(dimens.corner))
            .background(colors.surface)
            .rotaryAdjustable { step ->
                if (step > 0 && canIncrement) onIncrement()
                if (step < 0 && canDecrement) onDecrement()
            }
            .semantics {
                stateDescription = value
                if (label != null) contentDescription = label
            }
            .focusable(interactionSource = interaction)
            .padding(dimens.spacingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton(HmiIcons.Minus, decrementDescription, canDecrement, onDecrement, dimens.touchTarget)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimens.spacingS),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (label != null) {
                Text(label, style = HmiTheme.typography.caption, color = colors.onSurfaceMuted)
            }
            // Never wrap a value mid-number ("21." / "5°"); a too-narrow stepper clips instead.
            Text(
                value,
                style = HmiTheme.typography.display,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
            )
        }
        StepButton(Icons.Filled.Add, incrementDescription, canIncrement, onIncrement, dimens.touchTarget)
    }
}

@Composable
private fun StepButton(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit, size: Dp) {
    val colors = HmiTheme.colors
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.surfaceRaised)
            .focusProperties { canFocus = false }
            .clickable(enabled = enabled, role = Role.Button, onClickLabel = description, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = description,
            tint = if (enabled) colors.onSurface else colors.disabledContent,
            modifier = Modifier.size(HmiTheme.dimens.iconSize),
        )
    }
}

/** Mutually exclusive options, e.g. drive mode. Each segment is its own focus stop. */
@Composable
fun <T> HmiSegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    val segmentCorner = dimens.corner - dimens.spacingXs

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(dimens.corner))
            .background(colors.surfaceRaised)
            .selectableGroup(),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val interaction = remember(option) { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .hmiFocusRing(interaction, segmentCorner)
                    .heightIn(min = dimens.touchTarget - (dimens.focusRingWidth + dimens.focusRingGap) * 2)
                    .clip(RoundedCornerShape(segmentCorner))
                    .background(if (isSelected) colors.primary else Color.Transparent)
                    .selectable(
                        selected = isSelected,
                        interactionSource = interaction,
                        indication = ripple(),
                        role = Role.RadioButton,
                        onClick = { onSelect(option) },
                    )
                    .padding(horizontal = dimens.spacingM),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label(option),
                    style = HmiTheme.typography.label,
                    color = if (isSelected) colors.onPrimary else colors.onSurface,
                )
            }
        }
    }
}
