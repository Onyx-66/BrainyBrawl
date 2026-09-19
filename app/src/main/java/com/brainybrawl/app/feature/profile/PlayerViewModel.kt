package com.brainybrawl.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brainybrawl.app.feature.auth.AuthRepository
import com.brainybrawl.app.feature.auth.AuthState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface PlayerDataState<out T> {
    data object SignedOut : PlayerDataState<Nothing>
    data object Loading : PlayerDataState<Nothing>
    data object Failed : PlayerDataState<Nothing>
    data class Ready<T>(val data: T) : PlayerDataState<T>
}
data class SocialUi(val snapshot: SocialSnapshot=SocialSnapshot(emptyList(),emptyList()),val search: List<PlayerProfile> = emptyList(),val searched: Boolean=false,val busy: Boolean=false,val failed: Boolean=false,val reportSent: Boolean=false)
class PlayerViewModel(private val repository: PlayerRepository,private val auth: AuthRepository) : ViewModel() {
    private val mutableProfile=MutableStateFlow<PlayerDataState<ProfileSnapshot>>(PlayerDataState.SignedOut)
    val profile=mutableProfile.asStateFlow()
    private val mutableSocial=MutableStateFlow(SocialUi())
    val social=mutableSocial.asStateFlow()
    private val mutablePreview=MutableStateFlow<PlayerDataState<PlayerPreview>?>(null)
    val preview=mutablePreview.asStateFlow()
    private var previewJob:Job?=null
    private var refreshJob: Job?=null
    private var operationJob: Job?=null
    init { viewModelScope.launch { auth.state.collect { state ->
        refreshJob?.cancel();operationJob?.cancel();closePreview()
        mutableProfile.value=PlayerDataState.SignedOut;mutableSocial.value=SocialUi()
        if(state is AuthState.SignedIn&&!state.local) refresh()
    } } }
    fun closePreview(){previewJob?.cancel();mutablePreview.value=null}
    fun preview(id:String){
        closePreview()
        mutablePreview.value=PlayerDataState.Loading
        previewJob=viewModelScope.launch{
            try{mutablePreview.value=PlayerDataState.Ready(repository.preview(id))}
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutablePreview.value=PlayerDataState.Failed}
        }
    }
    fun refresh() {
        if((auth.state.value as? AuthState.SignedIn)?.local!=false) return
        refreshJob?.cancel()
        refreshJob=viewModelScope.launch {
            mutableProfile.value=PlayerDataState.Loading
            try {
                mutableProfile.value=PlayerDataState.Ready(repository.profile())
                mutableSocial.value=mutableSocial.value.copy(snapshot=repository.social(),failed=false)
            } catch(e: CancellationException) { throw e }
            catch(_: Exception) { mutableProfile.value=PlayerDataState.Failed }
        }
    }
    private fun operation(action: suspend () -> Unit) {
        if(mutableSocial.value.busy || (auth.state.value as? AuthState.SignedIn)?.local!=false) return
        mutableSocial.value=mutableSocial.value.copy(busy=true,failed=false,reportSent=false)
        operationJob=viewModelScope.launch {
            try { action(); mutableSocial.value=mutableSocial.value.copy(snapshot=repository.social()) }
            catch(e: CancellationException) { throw e }
            catch(_: Exception) { mutableSocial.value=mutableSocial.value.copy(failed=true) }
            finally { mutableSocial.value=mutableSocial.value.copy(busy=false) }
        }
    }
    fun search(query: String)=operation { mutableSocial.value=mutableSocial.value.copy(search=repository.search(query.trim()),searched=true) }
    fun friend(id: String,action: String)=operation { repository.friend(id,action);mutableSocial.value=mutableSocial.value.copy(search=emptyList(),searched=false) }
    fun block(id: String,blocked: Boolean)=operation { repository.block(id,blocked);mutableSocial.value=mutableSocial.value.copy(search=emptyList(),searched=false) }
    fun report(id: String,category: String,details: String,block: Boolean)=operation {
        repository.report(id,category,details)
        mutableSocial.value=mutableSocial.value.copy(reportSent=true,search=emptyList(),searched=false)
        if(block) repository.block(id,true)
    }
    fun rename(username: String)=operation { repository.rename(username);refresh() }
    fun equip(item: String)=operation { repository.equip(item);refresh() }
    companion object { fun factory(repository: PlayerRepository,auth: AuthRepository)=object : ViewModelProvider.Factory {
        override fun <T:ViewModel> create(modelClass: Class<T>):T {
            require(modelClass.isAssignableFrom(PlayerViewModel::class.java))
            @Suppress("UNCHECKED_CAST") return PlayerViewModel(repository,auth) as T
        }
    } }
}
