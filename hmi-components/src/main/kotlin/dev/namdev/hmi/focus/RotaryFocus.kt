package dev.namdev.hmi.focus

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.unit.Dp
import dev.namdev.hmi.theme.HmiTheme
import kotlin.math.sign

/**
 * Reserves space for, and draws, the focus ring of a control.
 *
 * In a car, many drivers move through the UI with a rotary controller or steering-wheel keys, so the focused
 * element must always be obvious. The ring sits [dev.namdev.hmi.theme.HmiDimens.focusRingGap] outside the control,
 * which keeps it visible on any fill colour, and the space is reserved even when unfocused so nothing jumps.
 *
 * Put it before the control's `clip`/`background` so the ring is drawn outside them.
 */
@Composable
fun Modifier.hmiFocusRing(interactionSource: InteractionSource, cornerRadius: Dp = HmiTheme.dimens.corner): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    val dimens = HmiTheme.dimens
    val inset = dimens.focusRingWidth + dimens.focusRingGap
    val ringColor = if (focused) HmiTheme.colors.focusRing else Color.Transparent
    return this
        .border(dimens.focusRingWidth, ringColor, RoundedCornerShape(cornerRadius + inset))
        .padding(inset)
}

/**
 * Lets a focused control be adjusted without touching it: D-pad left/right and rotary scroll call [onStep]
 * with -1 or +1. Up/down are not consumed, so the user can still move focus away.
 *
 * Key events reach a modifier only on the focused element or its ancestors, so place this
 * before `focusable()`/`clickable()` in the chain.
 */
fun Modifier.rotaryAdjustable(onStep: (Int) -> Unit): Modifier = this
    .onKeyEvent { event ->
        val step = when {
            event.type != KeyEventType.KeyDown -> 0
            event.key == Key.DirectionRight -> 1
            event.key == Key.DirectionLeft -> -1
            else -> 0
        }
        if (step != 0) onStep(step)
        step != 0
    }
    .onRotaryScrollEvent { event ->
        val step = sign(event.verticalScrollPixels).toInt()
        if (step != 0) onStep(step)
        step != 0
    }
