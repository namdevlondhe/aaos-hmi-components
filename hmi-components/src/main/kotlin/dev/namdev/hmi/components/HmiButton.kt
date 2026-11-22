package dev.namdev.hmi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import dev.namdev.hmi.focus.hmiFocusRing
import dev.namdev.hmi.theme.HmiTheme

enum class HmiButtonStyle { Primary, Secondary, Critical }

/** A button with an AAOS-sized touch target (76dp minimum) and a rotary focus ring. */
@Composable
fun HmiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: HmiButtonStyle = HmiButtonStyle.Primary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    val interaction = remember { MutableInteractionSource() }
    val (container, content) = when {
        !enabled -> colors.surfaceRaised to colors.disabledContent
        style == HmiButtonStyle.Primary -> colors.primary to colors.onPrimary
        style == HmiButtonStyle.Secondary -> colors.surfaceRaised to colors.onSurface
        else -> colors.critical to colors.onCritical
    }

    Row(
        modifier = modifier
            .hmiFocusRing(interaction)
            .defaultMinSize(minWidth = dimens.touchTarget * 2, minHeight = dimens.touchTarget)
            .clip(RoundedCornerShape(dimens.corner))
            .background(container)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = dimens.spacingL),
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingS, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(dimens.iconSize))
        }
        Text(text, style = HmiTheme.typography.label, color = content)
    }
}

@Preview(widthDp = 600)
@Composable
private fun HmiButtonPreview() {
    HmiTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(HmiTheme.dimens.spacingS)) {
            HmiButton("Add stop", onClick = {}, icon = Icons.Filled.Add)
            HmiButton("Cancel", onClick = {}, style = HmiButtonStyle.Secondary)
        }
    }
}
