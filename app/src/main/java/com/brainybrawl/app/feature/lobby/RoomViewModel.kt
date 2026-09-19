package com.brainybrawl.app.feature.lobby

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brainybrawl.app.feature.auth.AuthRepository
import com.brainybrawl.app.feature.auth.AuthState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class LobbyActions(val busy:Boolean=false,val failed:Boolean=false,val noPublicRoom:Boolean=false,val invites:List<RoomInvite> = emptyList(),val matchId:String?=null,val reactions:List<ReactionPreset> = emptyList(),val hiddenReactions:Set<String> = emptySet())
@OptIn(ExperimentalCoroutinesApi::class)
class RoomViewModel(private val repository:RoomRepository,private val auth:AuthRepository):ViewModel() {
    private val selectedRoom=MutableStateFlow<String?>(null)
    private val mutableActions=MutableStateFlow(LobbyActions())
    val actions=mutableActions.asStateFlow()
    private var operation:Job?=null
    private var catalogJob:Job?=null
    val connection=selectedRoom.flatMapLatest { id ->
        if(id==null)flowOf(RoomConnection.Empty) else repository.observe(id).retryWhen{cause,attempt->
            if(cause is CancellationException)throw cause
            emit(RoomConnection.Recovering(null))
            delay((1_000L shl attempt.coerceAtMost(4).toInt()).coerceAtMost(15_000L))
            true
        }
    }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5_000),RoomConnection.Empty)
    init { viewModelScope.launch { auth.state.collectLatest { state ->
        operation?.cancel();catalogJob?.cancel();selectedRoom.value=null;mutableActions.value=LobbyActions()
        if(state is AuthState.SignedIn) {
            try { selectedRoom.value=repository.currentRoom();mutableActions.value=mutableActions.value.copy(invites=repository.invites()) }
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutableActions.value=mutableActions.value.copy(failed=true)}
        }
    } } }
    private fun action(block:suspend()->Unit) {
        if(mutableActions.value.busy||auth.state.value !is AuthState.SignedIn)return
        mutableActions.value=mutableActions.value.copy(busy=true,failed=false,noPublicRoom=false)
        operation=viewModelScope.launch {
            try{block()}
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutableActions.value=mutableActions.value.copy(failed=true)}
            finally{mutableActions.value=mutableActions.value.copy(busy=false)}
        }
    }
    fun loadReactions(locale:String){
        catalogJob?.cancel()
        if(auth.state.value !is AuthState.SignedIn)return
        catalogJob=viewModelScope.launch{
            try{val catalog=repository.reactions(locale);mutableActions.update{it.copy(reactions=catalog)}}
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutableActions.update{it.copy(failed=true)}}
        }
    }
    fun react(id:String)=action{selectedRoom.value?.let{repository.react(it,id)}}
    fun reportReaction(event:ReactionEvent)=action{
        mutableActions.value=mutableActions.value.copy(hiddenReactions=mutableActions.value.hiddenReactions+event.id)
        repository.reportReaction(event)
    }
    fun refreshInvites()=action{mutableActions.value=mutableActions.value.copy(invites=repository.invites())}
    fun create(mode:OnlineMode,quick:Boolean)=action{
        val room=if(quick&&mode!=OnlineMode.DUEL)repository.joinPublic(mode) else repository.create(mode,quick)
        selectedRoom.value=room
        mutableActions.update{it.copy(noPublicRoom=room==null)}
        if(room!=null)Diagnostics.record(ProductEvent.ROOM_JOIN)
    }
    fun join(id:String)=action{selectedRoom.value=repository.join(id);Diagnostics.record(ProductEvent.ROOM_JOIN)}
    fun ready(ready:Boolean)=action{selectedRoom.value?.let{repository.ready(it,ready)}}
    fun matchmaking(enabled:Boolean)=action{selectedRoom.value?.let{repository.matchmaking(it,enabled)}}
    fun invite(friend:String)=action{selectedRoom.value?.let{repository.invite(it,friend)}}
    fun rename(name:String)=action{selectedRoom.value?.let{repository.renameTeam(it,name)}}
    fun leave()=action{selectedRoom.value?.let{repository.leave(it)};selectedRoom.value=null;Diagnostics.record(ProductEvent.ROOM_LEAVE);mutableActions.value=mutableActions.value.copy(matchId=null)}
    fun start(locale:String)=action{selectedRoom.value?.let{mutableActions.value=mutableActions.value.copy(matchId=repository.start(it,locale))}}
    companion object { fun factory(repository:RoomRepository,auth:AuthRepository)=object:ViewModelProvider.Factory {
        override fun<T:ViewModel>create(modelClass:Class<T>):T {
            require(modelClass.isAssignableFrom(RoomViewModel::class.java))
            @Suppress("UNCHECKED_CAST") return RoomViewModel(repository,auth) as T
        }
    } }
}
