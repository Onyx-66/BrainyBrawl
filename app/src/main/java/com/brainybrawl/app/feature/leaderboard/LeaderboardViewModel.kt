package com.brainybrawl.app.feature.leaderboard
import androidx.lifecycle.*
import com.brainybrawl.app.feature.auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class LeaderboardUi(val filter:LeaderboardFilter=LeaderboardFilter(),val loading:Boolean=false,val failed:Boolean=false,val board:LeaderboardSnapshot?=null)
class LeaderboardViewModel(private val repository:LeaderboardRepository,private val auth:AuthRepository):ViewModel(){
    private val mutable=MutableStateFlow(LeaderboardUi())
    val state=mutable.asStateFlow()
    private var request:Job?=null
    init{viewModelScope.launch{auth.state.collect{request?.cancel();mutable.value=LeaderboardUi();if(it is AuthState.SignedIn)load(LeaderboardFilter())}}}
    fun load(filter:LeaderboardFilter){
        request?.cancel();mutable.value=LeaderboardUi(filter,loading=true)
        if(auth.state.value !is AuthState.SignedIn){mutable.value=LeaderboardUi(filter);return}
        request=viewModelScope.launch{
            try{val board=repository.load(filter);mutable.value=LeaderboardUi(filter,board=board)}
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutable.value=LeaderboardUi(filter,failed=true)}
        }
    }
    companion object{fun factory(repository:LeaderboardRepository,auth:AuthRepository)=object:ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")override fun<T:ViewModel>create(modelClass:Class<T>):T=LeaderboardViewModel(repository,auth) as T
    }}
}
