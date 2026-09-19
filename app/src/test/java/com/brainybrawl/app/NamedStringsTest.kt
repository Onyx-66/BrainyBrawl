package com.brainybrawl.app

import com.brainybrawl.app.core.localization.formatNamed
import org.junit.Assert.assertEquals
import org.junit.Test

class NamedStringsTest {
    @Test fun translatorsCanReorderNamedValues() {
        assertEquals("7 / Player",formatNamed("{score} / {name}",mapOf("name" to "Player","score" to "7")))
    }
    @Test(expected=IllegalArgumentException::class)
    fun missingTranslationValueFailsExplicitly() { formatNamed("{score}",emptyMap()) }
}
