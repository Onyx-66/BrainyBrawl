package com.brainybrawl.app

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.brainybrawl.app.core.security.EncryptedAuthStore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class EncryptedAuthStoreTest {
    @Test fun verifierIsEncryptedAndSurvivesRepositoryRecreation() = runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val store=EncryptedAuthStore(context)
        store.saveCodeVerifier("test-only-code-verifier")
        val raw=context.getSharedPreferences("auth_encrypted",Context.MODE_PRIVATE).getString("verifier",null)
        assertNotNull(raw);assertFalse(raw!!.contains("test-only-code-verifier"))
        assertEquals("test-only-code-verifier",EncryptedAuthStore(context).loadCodeVerifier())
        store.deleteCodeVerifier();assertNull(store.loadCodeVerifier())
    }
    @Test fun pendingAccountCredentialsAreEncryptedAndCanBeErased()=runBlocking{
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val store=EncryptedAuthStore(context)
        store.writePendingAccount("test-only-pending-password")
        val raw=context.getSharedPreferences("auth_encrypted",Context.MODE_PRIVATE).getString("pending_account",null)
        assertNotNull(raw);assertFalse(raw!!.contains("test-only-pending-password"))
        assertEquals("test-only-pending-password",EncryptedAuthStore(context).readPendingAccount())
        store.writePendingAccount(null);assertNull(store.readPendingAccount())
    }
    @Test fun corruptedCiphertextIsDiscarded() = runBlocking {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("auth_encrypted",Context.MODE_PRIVATE).edit().putString("verifier","corrupt").commit()
        assertNull(EncryptedAuthStore(context).loadCodeVerifier())
    }
}
