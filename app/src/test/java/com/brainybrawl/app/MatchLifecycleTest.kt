package com.brainybrawl.app

import androidx.lifecycle.ViewModelStore
import com.brainybrawl.app.core.network.ServerClock
import com.brainybrawl.app.feature.auth.*
import com.brainybrawl.app.feature.match.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.JsonObject
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MatchLifecycleTest {
    private class Auth:AuthRepository {
        override val state=MutableStateFlow<AuthState>(AuthState.SignedIn("user",null))
        override suspend fun login(email:String,password:String)=AuthNotice.NONE
        override suspend fun register(username:String,email:String,password:String)=AuthNotice.NONE
        override suspend fun recover(email:String)=AuthNotice.NONE
        override suspend fun changePassword(password:String)=AuthNotice.NONE
        override suspend fun oauth(provider:AuthProvider)=AuthNotice.NONE
        override suspend fun callback(uri:String)=AuthNotice.NONE
        override suspend fun logout():AuthNotice {state.value=AuthState.SignedOut;return AuthNotice.NONE}
    }
    private class Matches:MatchRepository {
        override val clock=ServerClock{0L}
        var attempts=0
        var active=0
        override fun observe(id:String):Flow<MatchConnection> = flow {
            attempts++
            if(attempts==1)throw java.io.IOException("connection interrupted")
            active++
            try{emit(MatchConnection.Loading);awaitCancellation()}finally{active--}
        }
        override suspend fun chooseTheme(match:String,question:Int,theme:String){}
        override suspend fun miniAction(match:String,round:String,key:String,action:JsonObject,relay:Boolean)=MiniReceipt(true,true,1,key)
        override suspend fun cursor(match:String,round:String,x:Float,y:Float){}
        override suspend fun submit(match:String,round:String,key:String,action:JsonObject){}
    }
    @Test fun transportFailureRetriesAndLastCollectorCleanupCancelsSubscription()=runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store=ViewModelStore()
        try{
            val repository=Matches();val auth=Auth();val model=MatchViewModel(repository,auth)
            store.put("match",model);model.open("match")
            val collector=backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)){model.connection.collect()}
            runCurrent();assertEquals(1,repository.attempts)
            assertTrue(model.connection.value is MatchConnection.Recovering)
            advanceTimeBy(1_000);runCurrent();assertEquals(2,repository.attempts);assertEquals(1,repository.active)
            collector.cancel();runCurrent();assertEquals(0,repository.active)
            advanceTimeBy(30_000);runCurrent();assertEquals(2,repository.attempts)
        }finally{backgroundScope.coroutineContext.cancelChildren();store.clear();runCurrent();Dispatchers.resetMain()}
    }
    @Test fun logoutCancelsObservationAndClearsMatch()=runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store=ViewModelStore()
        try{
            val repository=Matches().apply{attempts=1};val auth=Auth();val model=MatchViewModel(repository,auth)
            store.put("match",model);model.open("match")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)){model.connection.collect()}
            runCurrent();assertEquals(1,repository.active)
            auth.logout();runCurrent();assertEquals(0,repository.active);assertNull(model.userId)
        }finally{backgroundScope.coroutineContext.cancelChildren();store.clear();runCurrent();Dispatchers.resetMain()}
    }
    @Test fun directAccountReplacementClearsPreviousSelectionsAndSubscription()=runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store=ViewModelStore()
        try{
            val repository=Matches().apply{attempts=1};val auth=Auth();val model=MatchViewModel(repository,auth)
            store.put("match",model);model.open("match")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)){model.connection.collect()}
            runCurrent()
            val round=MatchRound("round",0,"active","image_guess","2026-09-19T10:00:00Z","2026-09-19T10:00:00Z","2026-09-19T10:00:30Z",RoundDisplay())
            model.select(round,"choice")
            assertEquals(setOf("choice"),model.action.value.selected)
            auth.state.value=AuthState.SignedIn("different-user",null)
            runCurrent()
            assertEquals(MatchAction(),model.action.value)
            assertEquals(0,repository.active)
        }finally{backgroundScope.coroutineContext.cancelChildren();store.clear();runCurrent();Dispatchers.resetMain()}
    }
}
