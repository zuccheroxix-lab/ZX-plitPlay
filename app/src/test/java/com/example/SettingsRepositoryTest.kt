package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsRepositoryTest {

    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = SettingsRepository(context)
        repository.resetSettings()
    }

    @Test
    fun `setSelectedGame saves and reads back correctly`() {
        repository.setSelectedGame("com.example.game", "My Epic Game")
        val current = repository.settings.value
        assertEquals("com.example.game", current.selectedGamePackage)
        assertEquals("My Epic Game", current.selectedGameLabel)
    }

    @Test
    fun `setSelectedSecondApp saves and reads back correctly`() {
        repository.setSelectedSecondApp("com.android.chrome", "Google Chrome")
        val current = repository.settings.value
        assertEquals("com.android.chrome", current.selectedSecondAppPackage)
        assertEquals("Google Chrome", current.selectedSecondAppLabel)
    }

    @Test
    fun `setFloatingOpacity clamps within valid range`() {
        repository.setFloatingOpacity(0.1f)
        assertEquals(0.3f, repository.settings.value.floatingOpacity, 0.01f)

        repository.setFloatingOpacity(1.5f)
        assertEquals(1.0f, repository.settings.value.floatingOpacity, 0.01f)

        repository.setFloatingOpacity(0.75f)
        assertEquals(0.75f, repository.settings.value.floatingOpacity, 0.01f)
    }

    @Test
    fun `setFloatingSize enforces standard sizes`() {
        repository.setFloatingSize(48)
        assertEquals(48, repository.settings.value.floatingSizeDp)

        repository.setFloatingSize(64)
        assertEquals(64, repository.settings.value.floatingSizeDp)

        repository.setFloatingSize(100) // Fallback should be 56
        assertEquals(56, repository.settings.value.floatingSizeDp)
    }

    @Test
    fun `resetSettings clears all persisted preferences`() {
        repository.setSelectedGame("com.game.pkg", "Game")
        repository.resetSettings()
        val current = repository.settings.value
        assertTrue(current.selectedGamePackage.isEmpty())
        assertTrue(current.selectedGameLabel.isEmpty())
    }
}
