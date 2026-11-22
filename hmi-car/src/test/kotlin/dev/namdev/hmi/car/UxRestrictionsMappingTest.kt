package dev.namdev.hmi.car

import android.car.drivingstate.CarUxRestrictions.UX_RESTRICTIONS_FULLY_RESTRICTED
import android.car.drivingstate.CarUxRestrictions.UX_RESTRICTIONS_LIMIT_CONTENT
import android.car.drivingstate.CarUxRestrictions.UX_RESTRICTIONS_LIMIT_STRING_LENGTH
import android.car.drivingstate.CarUxRestrictions.UX_RESTRICTIONS_NO_KEYBOARD
import android.car.drivingstate.CarUxRestrictions.UX_RESTRICTIONS_NO_VIDEO
import dev.namdev.hmi.ux.UxRestrictions
import org.junit.Assert.assertEquals
import org.junit.Test

/** The Car API constants are compile-time constants, so this runs on the plain JVM. */
class UxRestrictionsMappingTest {

    @Test
    fun `parked means unrestricted, whatever flags are set`() {
        val mapped = uxRestrictionsOf(
            requiresDistractionOptimization = false,
            activeRestrictions = UX_RESTRICTIONS_FULLY_RESTRICTED,
            maxStringLength = 120,
            maxContentItems = 21,
        )

        assertEquals(UxRestrictions.Unrestricted, mapped)
    }

    @Test
    fun `fully restricted maps every supported restriction`() {
        val mapped = uxRestrictionsOf(
            requiresDistractionOptimization = true,
            activeRestrictions = UX_RESTRICTIONS_FULLY_RESTRICTED,
            maxStringLength = 120,
            maxContentItems = 21,
        )

        assertEquals(
            UxRestrictions(
                requiresDistractionOptimization = true,
                noKeyboard = true,
                noVideo = true,
                maxStringLength = 120,
                maxContentItems = 21,
            ),
            mapped,
        )
    }

    @Test
    fun `limits apply only when their flag is active`() {
        val mapped = uxRestrictionsOf(
            requiresDistractionOptimization = true,
            activeRestrictions = UX_RESTRICTIONS_NO_KEYBOARD or UX_RESTRICTIONS_LIMIT_CONTENT,
            maxStringLength = 120,
            maxContentItems = 10,
        )

        assertEquals(true, mapped.noKeyboard)
        assertEquals(false, mapped.noVideo)
        assertEquals(null, mapped.maxStringLength)
        assertEquals(10, mapped.maxContentItems)
    }

    @Test
    fun `a non-positive string limit from the vehicle is ignored`() {
        val mapped = uxRestrictionsOf(
            requiresDistractionOptimization = true,
            activeRestrictions = UX_RESTRICTIONS_LIMIT_STRING_LENGTH or UX_RESTRICTIONS_NO_VIDEO,
            maxStringLength = 0,
            maxContentItems = 21,
        )

        assertEquals(null, mapped.maxStringLength)
        assertEquals(true, mapped.noVideo)
    }
}
