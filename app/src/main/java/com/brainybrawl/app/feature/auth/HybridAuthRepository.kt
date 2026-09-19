package com.brainybrawl.app.feature.auth

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable data class PendingAccountConnection(val owner:String,val username:String,val email:String,val password:String,val register:Boolean,val createdAt:Long){
    override fun toString()="PendingAccountConnection(owner=$owner, credentials=[redacted])"
}
/** Android implementation stores this short-lived envelope with Android Keystore AES-GCM. */
interface PendingAccountStore { suspend fun read():PendingAccountConnection?;suspend fun write(value:PendingAccountConnection?) }
class MemoryPendingAccountStore:PendingAccountStore{
    private var value:PendingAccountConnection?=null
    override suspend fun read()=value
    override suspend fun write(value:PendingAccountConnection?){this.value=value}
}

class HybridAuthRepository(
    private val remote:AuthRepository,val localAccounts:LocalAccounts,private val scope:CoroutineScope,
    private val connectivity:StateFlow<Boolean> = MutableStateFlow(true),
    private val pending:PendingAccountStore=MemoryPendingAccountStore(),
    private val now:()->Long=System::currentTimeMillis
):AuthRepository{
    override val onlineConfigured get()=remote.onlineConfigured
    private val connectionLock=Mutex()
    private val mutableConnection=MutableStateFlow(AuthNotice.NONE)
    val connection=mutableConnection.asStateFlow()
    override val state=combine(remote.state,localAccounts.state){remoteState,local->
        when{
            !local.ready->AuthState.Loading
            remoteState is AuthState.SignedIn->remoteState
            local.current!=null->AuthState.SignedIn(local.current.id,local.current.email,local=true)
            else->remoteState
        }
    }.stateIn(scope,SharingStarted.Eagerly,AuthState.Loading)
    init{
        scope.launch{try{localAccounts.initialize()}catch(e:CancellationException){throw e}catch(_:Exception){localAccounts.storageFailed()}}
        scope.launch{connectivity.collectLatest{connected->
            if(connected&&onlineConfigured){
                localAccounts.state.first{it.ready}
                // Retry transient outages without looping on bad credentials or confirmation requirements.
                while(isActive){
                    val result=ensureOnline()
                    if(result!=AuthNotice.NETWORK_ERROR)break
                    delay(15_000)
                }
            }
        }}
    }
    private suspend fun safe(block:suspend()->AuthNotice)=try{block()}catch(e:CancellationException){throw e}catch(_:Exception){AuthNotice.REQUEST_FAILED}
    private suspend fun rememberPending(username:String,email:String,password:String,register:Boolean){
        val identity=requireNotNull(localAccounts.state.value.current)
        pending.write(PendingAccountConnection(identity.id,username,email.trim(),password,register,now()))
    }
    private suspend fun remoteConnected():Boolean=withTimeoutOrNull(5_000){remote.state.first{it is AuthState.SignedIn}}!=null
    override suspend fun ensureOnline():AuthNotice=connectionLock.withLock{connectPending()}
    private suspend fun connectPending():AuthNotice=safe{
        if(!connectivity.value)return@safe AuthNotice.NETWORK_ERROR
        if(remote.state.value is AuthState.SignedIn){pending.write(null);return@safe AuthNotice.NONE}
        if(!onlineConfigured)return@safe AuthNotice.BACKEND_REQUIRED
        val identity=localAccounts.state.value.current?:return@safe AuthNotice.REAUTH_REQUIRED
        val candidate=pending.read()?:return@safe AuthNotice.REAUTH_REQUIRED
        if(candidate.owner!=identity.id||now()-candidate.createdAt !in 0..604_800_000L){pending.write(null);return@safe AuthNotice.REAUTH_REQUIRED}
        var result=remote.login(candidate.email,candidate.password)
        if(result==AuthNotice.REQUEST_FAILED&&candidate.register){
            result=remote.register(candidate.username,candidate.email,candidate.password)
            if(result==AuthNotice.NONE||result==AuthNotice.VERIFY_EMAIL)pending.write(candidate.copy(register=false))
        }
        if(result==AuthNotice.NONE&&remoteConnected()){pending.write(null);return@safe AuthNotice.NONE}
        result
    }.also{mutableConnection.value=it}
    override suspend fun loginLocal(email:String,password:String)=login(email,password)
    override suspend fun registerLocal(username:String,email:String,password:String)=register(username,email,password)
    override suspend fun login(email:String,password:String):AuthNotice=connectionLock.withLock{safe{
        val localResult=localAccounts.login(email,password)
        val address=if(localResult==AuthNotice.NONE)localAccounts.state.value.current!!.email else email.trim()
        if(connectivity.value&&onlineConfigured){
            val result=remote.login(address,password)
            if(result==AuthNotice.NONE){
                localAccounts.rememberVerifiedLogin(address,password);pending.write(null);return@safe AuthNotice.NONE
            }
            if(localResult!=AuthNotice.NONE)return@safe result
        }
        if(localResult==AuthNotice.NONE){
            val user=localAccounts.state.value.current!!
            rememberPending(user.username,user.email,password,true)
            if(connectivity.value&&onlineConfigured)connectPending()
            AuthNotice.NONE
        }else localResult
    }}
    override suspend fun register(username:String,email:String,password:String):AuthNotice=connectionLock.withLock{safe{
        val result=localAccounts.register(username,email,password)
        if(result!=AuthNotice.NONE)return@safe result
        rememberPending(username,email,password,true)
        if(connectivity.value&&onlineConfigured)connectPending()else AuthNotice.NONE
    }}
    override suspend fun recover(email:String)=if(!connectivity.value)AuthNotice.NETWORK_ERROR else if(!onlineConfigured)AuthNotice.BACKEND_REQUIRED else remote.recover(email)
    override suspend fun changePassword(password:String):AuthNotice=connectionLock.withLock{safe{
        // Keep one account password: never report a local-only change for a configured server.
        if(onlineConfigured){
            if(!connectivity.value)return@safe AuthNotice.NETWORK_ERROR
            if(remote.state.value !is AuthState.SignedIn){
                val connected=connectPending()
                if(connected!=AuthNotice.NONE)return@safe connected
                if(remote.state.value !is AuthState.SignedIn)return@safe AuthNotice.REAUTH_REQUIRED
            }
        }
        if(remote.state.value is AuthState.SignedIn){
            val result=remote.changePassword(password)
            if(result==AuthNotice.PASSWORD_UPDATED){
                (remote.state.value as? AuthState.SignedIn)?.email?.let{localAccounts.rememberVerifiedLogin(it,password)}
                pending.write(null)
            }
            result
        }else{
            val result=localAccounts.changePassword(password)
            if(result==AuthNotice.PASSWORD_UPDATED){
                val user=requireNotNull(localAccounts.state.value.current)
                rememberPending(user.username,user.email,password,true)
            }
            result
        }
    }}
    override suspend fun oauth(provider:AuthProvider)=if(!connectivity.value)AuthNotice.NETWORK_ERROR else if(!onlineConfigured)AuthNotice.BACKEND_REQUIRED else remote.oauth(provider)
    override suspend fun callback(uri:String)=connectionLock.withLock{
        val result=remote.callback(uri)
        if(result in setOf(AuthNotice.NONE,AuthNotice.PASSWORD_RESET_READY)&&remoteConnected()){pending.write(null);mutableConnection.value=AuthNotice.NONE}
        result
    }
    override suspend fun logout():AuthNotice=connectionLock.withLock{
        val result=if(remote.state.value is AuthState.SignedIn)remote.logout()else AuthNotice.NONE
        if(result==AuthNotice.NONE)return@withLock safe{pending.write(null);localAccounts.logout();mutableConnection.value=AuthNotice.NONE;AuthNotice.NONE}
        result
    }
}
