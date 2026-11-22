package dev.namdev.hmi.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.ux.LocalUxRestrictions

/**
 * A single-line text field that turns into a notice while the keyboard is restricted (`noKeyboard`),
 * so no screen can open an on-screen keyboard while the vehicle is moving.
 */
@Composable
fun HmiTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    blockedMessage: String = "Typing is unavailable while driving",
) {
    if (LocalUxRestrictions.current.noKeyboard) {
        RestrictionNotice("$label: $blockedMessage", modifier)
        return
    }
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = HmiTheme.typography.caption) },
        singleLine = true,
        textStyle = HmiTheme.typography.body,
        shape = RoundedCornerShape(dimens.corner),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.focusRing,
            unfocusedBorderColor = colors.outline,
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            cursorColor = colors.primary,
            focusedLabelColor = colors.focusRing,
            unfocusedLabelColor = colors.onSurfaceMuted,
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimens.touchTarget),
    )
}
