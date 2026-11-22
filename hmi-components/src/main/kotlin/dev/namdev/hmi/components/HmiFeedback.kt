package dev.namdev.hmi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.ux.LocalUxRestrictions
import dev.namdev.hmi.ux.UxRestrictions

enum class HmiBannerLevel { Info, Warning, Critical }

/**
 * A vehicle or app message. Warnings and critical messages are announced by accessibility services
 * (assertively for critical ones) as soon as they appear.
 */
@Composable
fun HmiBanner(
    message: String,
    modifier: Modifier = Modifier,
    level: HmiBannerLevel = HmiBannerLevel.Info,
    title: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    val (container, content) = when (level) {
        HmiBannerLevel.Info -> colors.surfaceRaised to colors.onSurface
        HmiBannerLevel.Warning -> colors.warning to colors.onWarning
        HmiBannerLevel.Critical -> colors.critical to colors.onCritical
    }
    val icon = if (level == HmiBannerLevel.Info) Icons.Filled.Info else Icons.Filled.Warning
    val ux = LocalUxRestrictions.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimens.touchTarget)
            .clip(RoundedCornerShape(dimens.corner))
            .background(container)
            .semantics {
                liveRegion = if (level == HmiBannerLevel.Critical) LiveRegionMode.Assertive else LiveRegionMode.Polite
            }
            .padding(horizontal = dimens.spacingM, vertical = dimens.spacingS),
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = level.name, tint = content, modifier = Modifier.size(dimens.iconSize))
        Column(Modifier.weight(1f)) {
            if (title != null) Text(ux.limit(title), style = HmiTheme.typography.label, color = content)
            Text(ux.limit(message), style = HmiTheme.typography.body, color = content)
        }
        action?.invoke()
    }
}

/** The standard "not available while driving" notice. */
@Composable
fun RestrictionNotice(message: String, modifier: Modifier = Modifier) {
    val colors = HmiTheme.colors
    val dimens = HmiTheme.dimens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimens.touchTarget)
            .clip(RoundedCornerShape(dimens.corner))
            .background(colors.surfaceRaised)
            .padding(horizontal = dimens.spacingM, vertical = dimens.spacingS),
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null, tint = colors.onSurfaceMuted)
        Text(message, style = HmiTheme.typography.label, color = colors.onSurfaceMuted)
    }
}

/**
 * Shows [content] only while [blockedWhen] is false for the current restrictions, otherwise a
 * [RestrictionNotice]. Example: `UxRestricted({ it.noVideo }, "Video is paused while driving") { Player() }`.
 */
@Composable
fun UxRestricted(
    blockedWhen: (UxRestrictions) -> Boolean,
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (blockedWhen(LocalUxRestrictions.current)) {
        RestrictionNotice(message, modifier)
    } else {
        Column(modifier) { content() }
    }
}
