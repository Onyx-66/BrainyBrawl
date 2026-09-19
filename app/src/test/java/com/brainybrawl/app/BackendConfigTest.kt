package com.brainybrawl.app

import com.brainybrawl.app.core.network.BackendConfig
import org.junit.Assert.*
import org.junit.Test

class BackendConfigTest {
    @Test fun missingConfigurationDisablesOnline() { assertFalse(BackendConfig("", "").configured) }
    @Test fun publicHttpsConfigurationIsAccepted() {
        assertTrue(BackendConfig("https://example.supabase.co", "sb_publishable_example").configured)
    }
    @Test(expected = IllegalArgumentException::class)
    fun secretKeyIsRejected() { BackendConfig("https://example.supabase.co", "sb_secret_invalid") }
    @Test(expected = IllegalArgumentException::class)
    fun cleartextIsRejected() { BackendConfig("http://example.supabase.co", "sb_publishable_example") }
}
