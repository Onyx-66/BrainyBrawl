package com.brainybrawl.app

import com.brainybrawl.app.feature.auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HybridAuthRepositoryTest {
    private class Vault:LocalAccountVault{var data:String?=null;override suspend fun read()=data;override suspend fun write(value:String){data=value}}
    private class Remote(val confirmation:Boolean=false):AuthRepository{
        override val onlineConfigured=true
        override val state=MutableStateFlow<AuthState>(AuthState.SignedOut)
        val users=mutableMapOf<String,String>();var registrations=0;var calls=0;var confirmed=false;var linked=0;var oauthCalls=0;var requestedEmail:String?=null
        override suspend fun login(email:String,password:String):AuthNotice{
            calls++
            if(users[email]!=password)return AuthNotice.REQUEST_FAILED
            if(confirmation&&!confirmed)return AuthNotice.VERIFY_EMAIL
            state.value=AuthState.SignedIn("remote-id",email);return AuthNotice.NONE
        }
        override suspend fun register(username:String,email:String,password:String):AuthNotice{
            registrations++;users[email]=password
            return if(confirmation)AuthNotice.VERIFY_EMAIL else login(email,password)
        }
        override suspend fun logout():AuthNotice{state.value=AuthState.SignedOut;return AuthNotice.NONE}
        override suspend fun recover(email:String)=AuthNotice.RECOVERY_SENT
        override suspend fun changePassword(password:String):AuthNotice{
            (state.value as? AuthState.SignedIn)?.email?.let{users[it]=password}
            return AuthNotice.PASSWORD_UPDATED
        }
        override suspend fun oauth(provider:AuthProvider):AuthNotice{oauthCalls++;return AuthNotice.NONE}
        override suspend fun changeEmail(email:String):AuthNotice{requestedEmail=email;return AuthNotice.EMAIL_UPDATE_SENT}
        override suspend fun linkProvider(provider:AuthProvider):AuthNotice{linked++;return AuthNotice.NONE}
        override suspend fun callback(uri:String)=AuthNotice.NONE
    }
    @Test fun offlineCreationConnectsAutomaticallyWhenNetworkReturns()=runTest{
        val local=LocalAccounts(Vault());val remote=Remote();val network=MutableStateFlow(false);val pending=MemoryPendingAccountStore()
        val repo=HybridAuthRepository(remote,local,backgroundScope,network,pending)
        assertEquals(AuthNotice.NONE,repo.register("PlayerOne","one@example.invalid","ExamplePass123"));runCurrent()
        val owner=local.state.value.current!!.id
        assertTrue((repo.state.value as AuthState.SignedIn).local);assertEquals(0,remote.calls)
        network.value=true;runCurrent()
        assertEquals(1,remote.registrations);assertFalse((repo.state.value as AuthState.SignedIn).local)
        assertEquals(owner,local.state.value.current!!.id);assertNull(pending.read())
    }
    @Test fun verifiedRemoteLoginAlsoWorksOfflineWithoutChangingIdentity()=runTest{
        val local=LocalAccounts(Vault());val remote=Remote();remote.users["one@example.invalid"]="ExamplePass123"
        val network=MutableStateFlow(true);val repo=HybridAuthRepository(remote,local,backgroundScope,network)
        assertEquals(AuthNotice.NONE,repo.login("one@example.invalid","ExamplePass123"));runCurrent()
        val owner=local.state.value.current!!.id;local.rememberProfile("one@example.invalid",10000001,"PlayerOne");repo.logout();network.value=false;runCurrent()
        val calls=remote.calls
        assertEquals(AuthNotice.NONE,repo.login("one@example.invalid","ExamplePass123"));runCurrent()
        assertEquals(calls,remote.calls);assertTrue((repo.state.value as AuthState.SignedIn).local)
        assertEquals(owner,local.state.value.current!!.id);assertEquals(10000001L,local.state.value.current!!.playerNumber)
        assertEquals(AuthNotice.REQUEST_FAILED,repo.login("one@example.invalid","WrongPassword"))
    }
    @Test fun oldDeviceAccountCanConnectAfterCredentialsAreReentered()=runTest{
        val local=LocalAccounts(Vault());local.register("Legacy","legacy@example.invalid","ExamplePass123")
        val remote=Remote();remote.users["legacy@example.invalid"]="ExamplePass123";val repo=HybridAuthRepository(remote,local,backgroundScope)
        assertEquals(AuthNotice.REAUTH_REQUIRED,repo.ensureOnline())
        assertEquals(AuthNotice.NONE,repo.loginLocal("Legacy","ExamplePass123"));runCurrent()
        assertFalse((repo.state.value as AuthState.SignedIn).local);assertEquals(0,remote.registrations)
    }
    @Test fun emailConfirmationIsNotBypassedAndDoesNotRepeatSignup()=runTest{
        val remote=Remote(confirmation=true);val pending=MemoryPendingAccountStore()
        val repo=HybridAuthRepository(remote,LocalAccounts(Vault()),backgroundScope,MutableStateFlow(true),pending)
        assertEquals(AuthNotice.VERIFY_EMAIL,repo.register("PlayerOne","one@example.invalid","ExamplePass123"))
        runCurrent();assertFalse(repo.state.value is AuthState.SignedIn)
        assertEquals(AuthNotice.REAUTH_REQUIRED,repo.ensureOnline());assertEquals(1,remote.registrations)
        remote.confirmed=true;assertEquals(AuthNotice.NONE,repo.login("one@example.invalid","ExamplePass123"));runCurrent()
        assertFalse((repo.state.value as AuthState.SignedIn).local);assertNull(pending.read())
    }
    @Test fun expiredOrWrongOwnerPendingCredentialsCannotConnect()=runTest{
        val local=LocalAccounts(Vault());local.register("One","one@example.invalid","ExamplePass123")
        val remote=Remote();val pending=MemoryPendingAccountStore();val network=MutableStateFlow(false)
        val repo=HybridAuthRepository(remote,local,backgroundScope,network,pending){700_000_000L}
        pending.write(PendingAccountConnection(local.state.value.current!!.id,"One","one@example.invalid","ExamplePass123",true,0))
        network.value=true;assertEquals(AuthNotice.REAUTH_REQUIRED,repo.ensureOnline());assertNull(pending.read())
        pending.write(PendingAccountConnection("different-owner","One","one@example.invalid","ExamplePass123",true,700_000_000L))
        assertEquals(AuthNotice.REAUTH_REQUIRED,repo.ensureOnline());assertNull(pending.read());assertEquals(0,remote.calls)
    }
    @Test fun emailCacheChangesOnlyAfterTheServerConfirmsIt()=runTest{
        val local=LocalAccounts(Vault());val remote=Remote();val network=MutableStateFlow(true)
        remote.users["old@example.invalid"]="ExamplePass123"
        val repo=HybridAuthRepository(remote,local,backgroundScope,network)
        repo.login("old@example.invalid","ExamplePass123");runCurrent();val owner=local.state.value.current!!.id
        network.value=false;runCurrent();assertEquals(AuthNotice.NETWORK_ERROR,repo.changeEmail("new@example.invalid"));assertNull(remote.requestedEmail)
        network.value=true;runCurrent();assertEquals(AuthNotice.EMAIL_UPDATE_SENT,repo.changeEmail("new@example.invalid"))
        assertEquals("old@example.invalid",local.state.value.current!!.email)
        remote.state.value=AuthState.SignedIn("remote-id","new@example.invalid");runCurrent()
        assertEquals(owner,local.state.value.current!!.id);assertEquals("new@example.invalid",local.state.value.current!!.email)
        repo.logout();network.value=false;runCurrent();assertEquals(AuthNotice.NONE,repo.login("new@example.invalid","ExamplePass123"))
    }
    @Test fun linkingUsesTheExistingIdentityRatherThanAnotherSignIn()=runTest{
        val remote=Remote();remote.users["one@example.invalid"]="ExamplePass123"
        val repo=HybridAuthRepository(remote,LocalAccounts(Vault()),backgroundScope)
        repo.login("one@example.invalid","ExamplePass123");runCurrent()
        assertEquals(AuthNotice.NONE,repo.linkProvider(AuthProvider.GOOGLE));assertEquals(1,remote.linked);assertEquals(0,remote.oauthCalls)
        assertEquals("remote-id",(repo.state.value as AuthState.SignedIn).userId)
    }
    @Test fun passwordChangesCannotDivergeBetweenServerAndOfflineCache()=runTest{
        val remote=Remote();remote.users["one@example.invalid"]="ExamplePass123"
        val network=MutableStateFlow(true);val repo=HybridAuthRepository(remote,LocalAccounts(Vault()),backgroundScope,network)
        repo.login("one@example.invalid","ExamplePass123");runCurrent()
        network.value=false;runCurrent()
        assertEquals(AuthNotice.NETWORK_ERROR,repo.changePassword("NewExamplePass123"))
        assertEquals("ExamplePass123",remote.users["one@example.invalid"])
        network.value=true;runCurrent()
        assertEquals(AuthNotice.PASSWORD_UPDATED,repo.changePassword("NewExamplePass123"))
        repo.logout();network.value=false;runCurrent()
        assertEquals(AuthNotice.REQUEST_FAILED,repo.login("one@example.invalid","ExamplePass123"))
        assertEquals(AuthNotice.NONE,repo.login("one@example.invalid","NewExamplePass123"))
    }
    @Test fun logoutRemovesPendingSecretsAndDoesNotReconnectOnNetworkChange()=runTest{
        val local=LocalAccounts(Vault());val remote=Remote();val network=MutableStateFlow(false);val pending=MemoryPendingAccountStore()
        val repo=HybridAuthRepository(remote,local,backgroundScope,network,pending)
        repo.register("PlayerOne","one@example.invalid","ExamplePass123")
        assertFalse(pending.read().toString().contains("ExamplePass123"))
        repo.logout()
        assertNull(pending.read());network.value=true;runCurrent()
        assertEquals(AuthState.SignedOut,repo.state.value);assertEquals(0,remote.calls)
    }
    @Test fun cachedPasswordCannotHideFailedOnlineSignIn()=runTest{
        val local=LocalAccounts(Vault());local.register("Legacy","legacy@example.invalid","OldPassword123");local.logout()
        val remote=Remote();remote.users["legacy@example.invalid"]="NewPassword123"
        val repo=HybridAuthRepository(remote,local,backgroundScope)
        assertEquals(AuthNotice.REQUEST_FAILED,repo.login("Legacy","OldPassword123"));runCurrent()
        assertFalse(repo.state.value is AuthState.SignedIn);assertNull(local.state.value.current);assertEquals(0,remote.registrations)
        assertEquals(AuthNotice.NONE,repo.login("Legacy","NewPassword123"));runCurrent()
        assertFalse((repo.state.value as AuthState.SignedIn).local)
    }

}
