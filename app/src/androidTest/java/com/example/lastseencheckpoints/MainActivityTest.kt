package com.example.lastseencheckpoints

import android.os.Build
import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @Rule
    @JvmField
    val mainActivityTestRule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val inputMacAddressFilter = onView(withId(R.id.mac_address_filter_input))
    private val discoveredDevicesDisplay = onView(withId(R.id.discovered_devices))

    @Before
    fun allowPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermissions = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .findObject(UiSelector().text("ALLOW"))

            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click()
                } catch (e: UiObjectNotFoundException) {
                    Log.i("Permission", "No permission dialog found.")
                }
            }
        }
    }

    @Test
    fun bluetoothScanningAndFilter() {
        discoveredDevicesDisplay.check(matches(withText("")))

        Thread.sleep(3000)

        discoveredDevicesDisplay.check(matches(not(withText(""))))

        inputMacAddressFilter.perform(click())
        inputMacAddressFilter.perform(typeText("--------------------"))
        inputMacAddressFilter.perform(closeSoftKeyboard())

        discoveredDevicesDisplay.check(matches(withText("")))
    }

    @Test
    fun logging() {
        mainActivityTestRule.activity.logDevicesFound()
    }

}