package dev.namdev.hmi.ux

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class UxRestrictionsTest {

    @Test
    fun `text within the limit is unchanged`() {
        assertEquals("Home", UxRestrictions(maxStringLength = 4).limit("Home"))
        assertEquals("No limit at all", UxRestrictions.Unrestricted.limit("No limit at all"))
    }

    @Test
    fun `long text is cut to the limit including the ellipsis`() {
        val limited = UxRestrictions(maxStringLength = 10).limit("Central Station, Platform Road")

        assertEquals("Central S…", limited)
        assertEquals(10, limited.length)
    }

    @Test
    fun `trailing spaces are not kept before the ellipsis`() {
        assertEquals("Central…", UxRestrictions(maxStringLength = 9).limit("Central Station"))
    }

    @Test
    fun `a limit of one leaves only the ellipsis`() {
        assertEquals("…", UxRestrictions(maxStringLength = 1).limit("Anything"))
    }

    @Test
    fun `lists are cut to the content limit`() {
        val items = (1..10).toList()

        assertEquals(listOf(1, 2, 3), UxRestrictions(maxContentItems = 3).limit(items))
        assertSame(items, UxRestrictions.Unrestricted.limit(items))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a zero string length is rejected`() {
        UxRestrictions(maxStringLength = 0)
    }

    @Test
    fun `driving preset matches the AAOS baseline limits`() {
        val driving = UxRestrictions.Driving

        assertEquals(true, driving.noKeyboard)
        assertEquals(true, driving.noVideo)
        assertEquals(120, driving.maxStringLength)
        assertEquals(21, driving.maxContentItems)
    }
}
