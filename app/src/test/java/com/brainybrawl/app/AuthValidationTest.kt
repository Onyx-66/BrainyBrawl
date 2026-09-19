package com.brainybrawl.app

import com.brainybrawl.app.feature.auth.AuthValidation
import org.junit.Assert.*
import org.junit.Test

class AuthValidationTest {
    @Test fun passwordRequiresEightCharacters() {
        assertFalse(AuthValidation.password("1234567")); assertTrue(AuthValidation.password("12345678"))
    }
    @Test fun usernameMatchesServerContract() {
        assertTrue(AuthValidation.username("Player_42")); assertFalse(AuthValidation.username("ab"))
        assertFalse(AuthValidation.username("hello world")); assertFalse(AuthValidation.username("x".repeat(25)))
    }
    @Test fun emailRejectsWhitespaceAndMissingDomain() {
        assertTrue(AuthValidation.email("person@example.com"))
        assertFalse(AuthValidation.email("person @example.com")); assertFalse(AuthValidation.email("person@"))
    }
}
