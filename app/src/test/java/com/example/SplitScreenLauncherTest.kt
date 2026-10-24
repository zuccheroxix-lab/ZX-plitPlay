package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.launcher.LaunchResult
import com.example.launcher.SplitScreenLauncher
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SplitScreenLauncherTest {

    @Test
    fun `launchSplitPair returns Error when packages are blank`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val launcher = SplitScreenLauncher(context)

        var resultReceived: LaunchResult? = null
        launcher.launchSplitPair("", "") { result ->
            resultReceived = result
        }

        assertTrue(resultReceived is LaunchResult.Error)
    }

    @Test
    fun `launchSplitPair returns Error when game and second app are identical`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val launcher = SplitScreenLauncher(context)

        var resultReceived: LaunchResult? = null
        launcher.launchSplitPair("com.example.game", "com.example.game") { result ->
            resultReceived = result
        }

        assertTrue(resultReceived is LaunchResult.Error)
    }
}
