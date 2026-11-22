package dev.namdev.hmi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextOverflow
import dev.namdev.hmi.focus.hmiFocusRing
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.ux.LocalUxRestrictions

/** One row of a list. Text is shortened to the current UX string-length limit while driving. */
@Composable
fun HmiListItem(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    val ux = LocalUxRestrictions.current
    val interaction = remember { MutableInteractionSource() }
    val clickModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(interaction, ripple(), role = Role.Button, onClick = onClick)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .hmiFocusRing(interaction)
            .heightIn(min = dimens.touchTarget)
            .clip(RoundedCornerShape(dimens.corner))
            .background(colors.surface)
            .then(clickModifier)
            .padding(horizontal = dimens.spacingM, vertical = dimens.spacingS),
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = colors.onSurfaceMuted,
                modifier = Modifier.size(dimens.iconSize),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                ux.limit(title),
                style = HmiTheme.typography.body,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (supportingText != null) {
                Text(
                    ux.limit(supportingText),
                    style = HmiTheme.typography.caption,
                    color = colors.onSurfaceMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.onSurfaceMuted,
                modifier = Modifier.size(dimens.iconSize),
            )
        }
    }
}

/**
 * A lazy list that honours the UX content limit: while driving only the first `maxContentItems` items
 * are shown, followed by [restrictedMessage].
 */
@Composable
fun <T> HmiList(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    restrictedMessage: String = "More items are available when parked",
    itemContent: @Composable (T) -> Unit,
) {
    val visible = LocalUxRestrictions.current.limit(items)
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(HmiTheme.dimens.spacingXs)) {
        items(visible, key = key) { itemContent(it) }
        if (visible.size < items.size) {
            item(key = "dev.namdev.hmi.restricted-footer") {
                RestrictionNotice(restrictedMessage, Modifier.padding(top = HmiTheme.dimens.spacingS))
            }
        }
    }
}
