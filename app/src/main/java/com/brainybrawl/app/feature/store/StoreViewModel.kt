package com.brainybrawl.app.feature.store

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brainybrawl.app.feature.auth.AuthRepository
import com.brainybrawl.app.feature.auth.AuthState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StoreUi(val snapshot: StoreSnapshot?=null,val busy:Boolean=false,val failed:Boolean=false,val saved:Boolean=false)
class StoreViewModel(private val repository:StoreRepository,private val auth:AuthRepository):ViewModel() {
    private val mutable=MutableStateFlow(StoreUi())
    val ui=mutable.asStateFlow()
    private var job:Job?=null
    init { viewModelScope.launch { auth.state.collect { state ->
        job?.cancel();mutable.value=StoreUi()
        if(state is AuthState.SignedIn) refresh()
    } } }
    private fun action(block:suspend()->Unit) {
        if(mutable.value.busy || auth.state.value !is AuthState.SignedIn) return
        mutable.value=mutable.value.copy(busy=true,failed=false,saved=false)
        job=viewModelScope.launch {
            try { block();mutable.value=mutable.value.copy(snapshot=repository.snapshot()) }
            catch(e:CancellationException){throw e}
            catch(_:Exception){mutable.value=mutable.value.copy(failed=true)}
            finally{mutable.value=mutable.value.copy(busy=false)}
        }
    }
    fun refresh()=action {}
    fun purchase(item:String)=action {
        Diagnostics.record(ProductEvent.PURCHASE_ATTEMPT)
        try{repository.purchase(item);Diagnostics.record(ProductEvent.PURCHASE_SUCCESS)}
        catch(e:CancellationException){throw e}
        catch(e:Exception){Diagnostics.record(ProductEvent.PURCHASE_FAILURE);throw e}
    }
    fun consumeSaved(){mutable.value=mutable.value.copy(saved=false)}
    fun saveLoadout(items:List<String>)=action { repository.loadout(items);mutable.value=mutable.value.copy(saved=true) }
    companion object { fun factory(repository:StoreRepository,auth:AuthRepository)=object:ViewModelProvider.Factory {
        override fun<T:ViewModel>create(modelClass:Class<T>):T {
            require(modelClass.isAssignableFrom(StoreViewModel::class.java))
            @Suppress("UNCHECKED_CAST") return StoreViewModel(repository,auth) as T
        }
    } }
}
