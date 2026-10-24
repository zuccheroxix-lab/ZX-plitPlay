package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.compatibility.DeviceCompatibilityChecker
import com.example.model.SupportLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CompatibilityCheckerTest {

    @Test
    fun `generateReport returns complete device report`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val checker = DeviceCompatibilityChecker(context)
        val report = checker.generateReport()

        assertNotNull(report)
        assertFalse(report.androidRelease.isBlank())
        assertTrue(report.apiLevel >= 24)
        assertTrue(report.items.isNotEmpty())

        val apiItem = report.items.find { it.id == "android_version" }
        assertNotNull(apiItem)
        assertEquals(SupportLevel.SUPPORTED, apiItem?.level)

        val ramItem = report.items.find { it.id == "device_ram" }
        assertNotNull(ramItem)
    }
}
