package com.brainybrawl.app

import com.brainybrawl.app.feature.auth.*
import com.brainybrawl.app.feature.profile.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerStateTest {
    private class Auth : AuthRepository {
        override val state=MutableStateFlow<AuthState>(AuthState.SignedIn("test-user",null))
        override suspend fun login(email:String,password:String)=AuthNotice.NONE
        override suspend fun register(username:String,email:String,password:String)=AuthNotice.NONE
        override suspend fun recover(email:String)=AuthNotice.NONE
        override suspend fun changePassword(password:String)=AuthNotice.NONE
        override suspend fun oauth(provider:AuthProvider)=AuthNotice.NONE
        override suspend fun callback(uri:String)=AuthNotice.NONE
        override suspend fun logout():AuthNotice { state.value=AuthState.SignedOut;return AuthNotice.NONE }
    }
    private class Repository(private val pending:CompletableDeferred<ProfileSnapshot>?=null) : PlayerRepository {
        override suspend fun profile()=pending?.await() ?: ProfileSnapshot(PlayerProfile("test-user",10000000,"TestPlayer"),emptyList(),emptyList(),0,0)
        override suspend fun preview(userId:String)=PlayerPreview(PlayerProfile(userId,10000001,"Friend"),emptyList(),emptyList())
        override suspend fun social()=SocialSnapshot(emptyList(),emptyList())
        override suspend fun search(query:String)=emptyList<PlayerProfile>()
        override suspend fun rename(username:String) {}
        override suspend fun friend(userId:String,action:String) {}
        override suspend fun block(userId:String,blocked:Boolean) {}
        override suspend fun report(userId:String,category:String,details:String) {}
        override suspend fun equip(item:String) {}
    }
    @Test fun signOutClearsThePreviousAccountsData()=runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val auth=Auth();val vm=PlayerViewModel(Repository(),auth)
            advanceUntilIdle();assertTrue(vm.profile.value is PlayerDataState.Ready)
            auth.logout();advanceUntilIdle();assertEquals(PlayerDataState.SignedOut,vm.profile.value)
            assertTrue(vm.social.value.snapshot.friends.isEmpty())
        } finally { Dispatchers.resetMain() }
    }
    @Test fun lateResponseAfterLogoutCannotRestorePersonalData()=runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val pending=CompletableDeferred<ProfileSnapshot>();val auth=Auth();val vm=PlayerViewModel(Repository(pending),auth)
            runCurrent();assertEquals(PlayerDataState.Loading,vm.profile.value)
            auth.logout();runCurrent()
            pending.complete(ProfileSnapshot(PlayerProfile("test-user",10000000,"TestPlayer"),emptyList(),emptyList(),0,0))
            advanceUntilIdle();assertEquals(PlayerDataState.SignedOut,vm.profile.value)
        } finally { Dispatchers.resetMain() }
    }
}
