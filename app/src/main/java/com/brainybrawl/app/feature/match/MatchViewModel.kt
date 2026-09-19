package com.brainybrawl.app.feature.match

import androidx.lifecycle.*
import com.brainybrawl.app.feature.auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import java.util.UUID

data class MatchAction(val round:String?=null,val selected:Set<String> = emptySet(),val busy:Boolean=false,
    val failed:Boolean=false,val accepted:Boolean=false,val feedback:MiniReceipt?=null)
@OptIn(ExperimentalCoroutinesApi::class)
class MatchViewModel(private val repository:MatchRepository,private val auth:AuthRepository):ViewModel(){
    private val match=MutableStateFlow<String?>(null)
    private val mutable=MutableStateFlow(MatchAction())
    val action=mutable.asStateFlow()
    private var operation:Job?=null
    private var cursorJob:Job?=null
    val userId:String? get()=(auth.state.value as? AuthState.SignedIn)?.userId
    private var retry:Triple<String,String,JsonObject>?=null
    val connection=combine(match,auth.state){id,state->if(state is AuthState.SignedIn)id else null}
        .flatMapLatest{id->if(id==null)flowOf(MatchConnection.Loading) else repository.observe(id).retryWhen{cause,attempt->
            if(cause is CancellationException)throw cause
            emit(MatchConnection.Recovering(null))
            delay((1_000L shl attempt.coerceAtMost(4).toInt()).coerceAtMost(15_000L))
            true
        }}
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(0,0),MatchConnection.Loading)
    init{viewModelScope.launch{
        var previousUser:String?=null
        auth.state.map{(it as? AuthState.SignedIn)?.userId}.distinctUntilChanged().collect{user->
            if(user==null||(previousUser!=null&&previousUser!=user)){
                operation?.cancel();cursorJob?.cancel();match.value=null;mutable.value=MatchAction();retry=null
            }
            previousUser=user
        }
    }}
    fun open(id:String){if(match.value!=id){operation?.cancel();cursorJob?.cancel();match.value=id;mutable.value=MatchAction();retry=null}}
    fun now()=repository.clock.nowMillis()
    fun select(round:MatchRound,id:String){
        if(round.submission!=null || mutable.value.busy || mutable.value.accepted && mutable.value.round==round.id)return
        val previous=if(mutable.value.round==round.id)mutable.value.selected else emptySet()
        val selected=if(id in previous)previous-id else if(previous.size<4)previous+id else previous
        mutable.value=MatchAction(round=round.id,selected=selected)
        retry=null
    }
    fun submit(round:MatchRound,option:String?=null){
        val matchId=match.value?:return
        if(mutable.value.busy || round.submission!=null)return
        val selected=if(option!=null)setOf(option) else mutable.value.selected
        if(round.kind=="image_guess" && selected.size!=4)return
        val payload=buildJsonObject{
            if(round.kind=="question_round")put("option_id",requireNotNull(option))
            else put("choice_ids",JsonArray(selected.sorted().map(::JsonPrimitive)))
        }
        val prior=retry
        val key=if(prior?.first==round.id && prior.third==payload)prior.second else UUID.randomUUID().toString()
        retry=Triple(round.id,key,payload)
        mutable.value=MatchAction(round.id,selected,busy=true)
        operation=viewModelScope.launch{
            try{repository.submit(matchId,round.id,key,payload);mutable.update{it.copy(busy=false,accepted=true)}}
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutable.update{it.copy(busy=false,failed=true)}}
        }
    }
    fun chooseTheme(question:Int,theme:String){
        val id=match.value?:return
        if(mutable.value.busy)return
        mutable.value=MatchAction(busy=true)
        operation=viewModelScope.launch{
            try{repository.chooseTheme(id,question,theme);mutable.value=MatchAction()}
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutable.value=MatchAction(failed=true)}
        }
    }
    fun mini(round:MatchRound,payload:JsonObject){
        val id=match.value?:return
        if(mutable.value.busy)return
        val prior=retry
        val key=if(prior?.first==round.id&&prior.third==payload)prior.second else UUID.randomUUID().toString()
        retry=Triple(round.id,key,payload)
        mutable.value=MatchAction(round=round.id,busy=true)
        operation=viewModelScope.launch{
            try{
                val receipt=repository.miniAction(id,round.id,key,payload,round.kind in setOf("precision_tap","speed_sort"))
                retry=null;mutable.value=MatchAction(round=round.id,feedback=receipt)
            }catch(e:CancellationException){throw e}
            catch(_:Exception){mutable.value=MatchAction(round=round.id,failed=true)}
        }
    }
    fun cursor(round:String,x:Float,y:Float){
        val id=match.value?:return
        if(cursorJob?.isActive==true)return
        cursorJob=viewModelScope.launch{
            try{repository.cursor(id,round,x.coerceIn(0f,1f),y.coerceIn(0f,1f));delay(100)}
            catch(e:CancellationException){throw e}catch(_:Exception){ }
        }
    }
    companion object{fun factory(repository:MatchRepository,auth:AuthRepository)=object:ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")override fun<T:ViewModel>create(modelClass:Class<T>):T=MatchViewModel(repository,auth) as T
    }}
}
