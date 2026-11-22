package dev.namdev.hmi.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.namdev.hmi.car.rememberCarUxRestrictions
import dev.namdev.hmi.theme.HmiTheme
import dev.namdev.hmi.theme.HmiThemeMode
import dev.namdev.hmi.ux.ProvideUxRestrictions

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CatalogApp() }
    }
}

/**
 * On a car, restrictions come from the vehicle (CarUxRestrictionsManager). Anywhere else, or to try
 * the behaviour while parked, "Simulate driving" applies [SimulatedDriving].
 */
@Composable
fun CatalogApp() {
    var themeMode by rememberSaveable { mutableStateOf(HmiThemeMode.FollowSystem) }
    var simulateDriving by rememberSaveable { mutableStateOf(false) }
    val vehicleRestrictions by rememberCarUxRestrictions()

    HmiTheme(mode = themeMode) {
        ProvideUxRestrictions(if (simulateDriving) SimulatedDriving else vehicleRestrictions) {
            CatalogScreen(
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it },
                simulateDriving = simulateDriving,
                onSimulateDrivingChange = { simulateDriving = it },
            )
        }
    }
}
