package dev.namdev.hmi.car

import android.car.Car
import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.namdev.hmi.ux.UxRestrictions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Streams the vehicle's driving UX restrictions from `CarUxRestrictionsManager`.
 *
 * Create it with [create]; it returns null on devices that are not Android Automotive, so the same APK
 * runs on a phone. No permission is needed to read UX restrictions.
 */
class CarUxRestrictionsSource private constructor(
    private val car: Car,
    private val manager: CarUxRestrictionsManager,
) {
    val restrictions: Flow<UxRestrictions> = callbackFlow {
        trySend(manager.currentCarUxRestrictions.toUxRestrictions())
        manager.registerListener { trySend(it.toUxRestrictions()) }
        awaitClose { manager.unregisterListener() }
    }

    fun release() {
        car.disconnect()
    }

    companion object {
        fun create(context: Context): CarUxRestrictionsSource? {
            if (!isAutomotive(context)) return null
            val car = Car.createCar(context)
            val manager = car?.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as? CarUxRestrictionsManager
            if (manager == null) car?.disconnect()
            return manager?.let { CarUxRestrictionsSource(car, it) }
        }

        /** Checked before any Car API class is touched, because those classes exist only on AAOS. */
        fun isAutomotive(context: Context): Boolean =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
    }
}

/**
 * The vehicle's current UX restrictions as Compose state, or [fallback] when not running on a car.
 * Typical use: `ProvideUxRestrictions(rememberCarUxRestrictions().value) { App() }`.
 */
@Composable
fun rememberCarUxRestrictions(fallback: UxRestrictions = UxRestrictions.Unrestricted): State<UxRestrictions> {
    val context = LocalContext.current.applicationContext
    val source = remember(context) { CarUxRestrictionsSource.create(context) }
    DisposableEffect(source) {
        onDispose { source?.release() }
    }
    val flow = remember(source, fallback) { source?.restrictions ?: flowOf(fallback) }
    return flow.collectAsState(initial = fallback)
}

internal fun CarUxRestrictions.toUxRestrictions(): UxRestrictions = uxRestrictionsOf(
    requiresDistractionOptimization = isRequiresDistractionOptimization,
    activeRestrictions = activeRestrictions,
    maxStringLength = maxRestrictedStringLength,
    maxContentItems = maxCumulativeContentItems,
)

/** Pure mapping from the Car API flags, kept separate so it is unit-testable on the JVM. */
internal fun uxRestrictionsOf(
    requiresDistractionOptimization: Boolean,
    activeRestrictions: Int,
    maxStringLength: Int,
    maxContentItems: Int,
): UxRestrictions {
    if (!requiresDistractionOptimization) return UxRestrictions.Unrestricted
    fun has(flag: Int) = activeRestrictions and flag != 0
    return UxRestrictions(
        requiresDistractionOptimization = true,
        noKeyboard = has(CarUxRestrictions.UX_RESTRICTIONS_NO_KEYBOARD),
        noVideo = has(CarUxRestrictions.UX_RESTRICTIONS_NO_VIDEO),
        maxStringLength = maxStringLength
            .takeIf { has(CarUxRestrictions.UX_RESTRICTIONS_LIMIT_STRING_LENGTH) && it > 0 },
        maxContentItems = maxContentItems
            .takeIf { has(CarUxRestrictions.UX_RESTRICTIONS_LIMIT_CONTENT) && it >= 0 },
    )
}
