package dev.namdev.hmi.ux

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf

/**
 * Driver-distraction rules currently in force, a platform-independent mirror of AAOS `CarUxRestrictions`.
 *
 * Components read it from [LocalUxRestrictions] and adapt themselves: text is shortened, long lists are cut,
 * keyboards and video are replaced by a notice. On a car, `:hmi-car` feeds it from `CarUxRestrictionsManager`.
 */
@Immutable
data class UxRestrictions(
    val requiresDistractionOptimization: Boolean = false,
    val noKeyboard: Boolean = false,
    val noVideo: Boolean = false,
    /** Maximum characters of a single string, or null for no limit. */
    val maxStringLength: Int? = null,
    /** Maximum items in a list or other cumulative content, or null for no limit. */
    val maxContentItems: Int? = null,
) {
    init {
        require(maxStringLength == null || maxStringLength > 0) { "maxStringLength must be positive" }
        require(maxContentItems == null || maxContentItems >= 0) { "maxContentItems must not be negative" }
    }

    /** Shortens [text] to [maxStringLength] characters, the last one being an ellipsis. */
    fun limit(text: String): String {
        val max = maxStringLength
        return if (max == null || text.length <= max) text else text.take(max - 1).trimEnd() + ELLIPSIS
    }

    /** Keeps the first [maxContentItems] items. */
    fun <T> limit(items: List<T>): List<T> = maxContentItems?.let { items.take(it) } ?: items

    companion object {
        private const val ELLIPSIS = "…"

        val Unrestricted = UxRestrictions()

        /** Typical restrictions while moving; the limits match the AAOS default (baseline) configuration. */
        val Driving = UxRestrictions(
            requiresDistractionOptimization = true,
            noKeyboard = true,
            noVideo = true,
            maxStringLength = 120,
            maxContentItems = 21,
        )
    }
}

val LocalUxRestrictions = compositionLocalOf { UxRestrictions.Unrestricted }

/** Makes [restrictions] visible to every HMI component in [content]. */
@Composable
fun ProvideUxRestrictions(restrictions: UxRestrictions, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalUxRestrictions provides restrictions, content = content)
}
