package com.brainybrawl.app

import com.brainybrawl.app.feature.auth.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class LocalAccountsTest {
    private class Vault:LocalAccountVault {var value:String?=null;override suspend fun read()=value;override suspend fun write(value:String){this.value=value}}
    @Test fun accountPersistsWithoutPlaintextPassword()=runBlocking{
        val vault=Vault();val accounts=LocalAccounts(vault)
        assertEquals(AuthNotice.NONE,accounts.register("Mr.onyx","tester@example.invalid","ExamplePass123"))
        assertFalse(vault.value!!.contains("ExamplePass123"))
        val restored=LocalAccounts(vault);restored.initialize();assertEquals("Mr.onyx",restored.state.value.current!!.username)
        restored.logout();assertNull(restored.state.value.current)
        assertEquals(AuthNotice.REQUEST_FAILED,restored.login("tester@example.invalid","WrongPassword"))
        assertEquals(AuthNotice.NONE,restored.login("TESTER@example.invalid","ExamplePass123"))
        assertEquals(AuthNotice.REQUEST_FAILED,restored.register("Another","tester@example.invalid","ExamplePass123"))
    }
    @Test fun queueIsPersistentDeduplicatedAndAccountScoped()=runBlocking{
        val vault=Vault();val accounts=LocalAccounts(vault)
        accounts.register("First","first@example.invalid","ExamplePass123")
        accounts.enqueue("Friend","friend");accounts.enqueue("FRIEND","friend")
        val item=accounts.state.value.queue.single()
        accounts.logout();accounts.register("Second","second@example.invalid","ExamplePass123")
        assertTrue(accounts.state.value.queue.isEmpty());accounts.cancel(item.id)
        accounts.logout();accounts.login("First","ExamplePass123")
        assertEquals(item,accounts.state.value.queue.single())
        accounts.cancel(item.id);assertTrue(accounts.state.value.queue.isEmpty())
    }
    @Test fun deletionRemovesOnlyConfirmedIdentityAndItsQueue()=runBlocking{
        val vault=Vault();val accounts=LocalAccounts(vault)
        accounts.register("First","first@example.invalid","ExamplePass123")
        val first=accounts.state.value.current!!.id
        accounts.enqueue("FirstFriend","friend");accounts.logout()
        accounts.register("Second","second@example.invalid","ExamplePass123")
        val second=accounts.state.value.current!!.id
        accounts.enqueue("SecondFriend","friend")
        try{accounts.deleteCurrent(first);fail("Stale identity must not delete another account")}catch(_:IllegalArgumentException){}
        accounts.deleteCurrent(second);assertNull(accounts.state.value.current)
        assertFalse(vault.value!!.contains("SecondFriend"))
        assertEquals(AuthNotice.REQUEST_FAILED,accounts.login("Second","ExamplePass123"))
        assertEquals(AuthNotice.NONE,accounts.login("First","ExamplePass123"))
        assertEquals("FirstFriend",accounts.state.value.queue.single().target)
    }
    @Test fun passwordChangesInvalidatePreviousVerifier()=runBlocking{
        val accounts=LocalAccounts(Vault());accounts.register("First","first@example.invalid","ExamplePass123")
        assertEquals(AuthNotice.PASSWORD_UPDATED,accounts.changePassword("NewPassword123"));accounts.logout()
        assertEquals(AuthNotice.REQUEST_FAILED,accounts.login("First","ExamplePass123"))
        assertEquals(AuthNotice.NONE,accounts.login("First","NewPassword123"))
    }
    @Test fun damagedVaultIsNeverOverwritten()=runBlocking{
        val vault=Vault();vault.value="damaged"
        val accounts=LocalAccounts(vault)
        try{accounts.initialize();fail("Expected damaged storage failure")}catch(_:kotlinx.serialization.SerializationException){}
        accounts.storageFailed();assertTrue(accounts.state.value.failed)
        try{accounts.register("First","first@example.invalid","ExamplePass123");fail("Expected failure")}catch(_:kotlinx.serialization.SerializationException){}
        assertEquals("damaged",vault.value)
    }
}
