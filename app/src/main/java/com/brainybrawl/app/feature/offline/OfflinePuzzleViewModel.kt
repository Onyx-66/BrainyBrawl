package com.brainybrawl.app.feature.offline

import android.os.SystemClock
import androidx.lifecycle.*
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.OfflinePuzzle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

data class OfflinePuzzleUi(val game:OfflinePuzzle?=null,val now:Long=0,val loading:Boolean=false,val failed:Boolean=false,val best:Int=0)
class OfflinePuzzleViewModel(private val content:ContentRepository,private val stats:OfflineStatistics):ViewModel(){
    private val mutable=MutableStateFlow(OfflinePuzzleUi());val state=mutable.asStateFlow()
    private var task:Job?=null;private val visible=MutableStateFlow(false)
    fun setVisible(value:Boolean){visible.value=value}
    fun start(locale:String){
        task?.cancel();mutable.value=OfflinePuzzleUi(loading=true)
        val owner="puzzles:"+stats.currentOwner();val session=UUID.randomUUID().toString()
        task=viewModelScope.launch{
            try{
                val puzzle=content.load(ContentKind.PUZZLE,locale).filterIsInstance<PuzzleContent>().random()
                val time=SystemClock.elapsedRealtime();mutable.value=OfflinePuzzleUi(game=OfflinePuzzle(puzzle,time),now=time)
                while(isActive){
                    visible.first{it};val now=SystemClock.elapsedRealtime();mutable.update{it.copy(now=now,game=it.game?.tick(now))}
                    val game=mutable.value.game!!
                    if(game.finished){val saved=withContext(Dispatchers.IO){stats.record(session,game.score,96,owner)};mutable.update{it.copy(best=saved.best)};break}
                    delay(100)
                }
            }catch(e:CancellationException){throw e}catch(_:Exception){mutable.update{it.copy(loading=false,failed=true)}}
        }
    }
    fun place(piece:String,slot:String,rotation:Int){mutable.update{it.copy(game=it.game?.place(piece,slot,rotation,SystemClock.elapsedRealtime()))}}
    fun stop(){task?.cancel();mutable.value=OfflinePuzzleUi()}
    companion object{fun factory(content:ContentRepository,stats:OfflineStatistics)=object:ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass:Class<T>):T=OfflinePuzzleViewModel(content,stats) as T
    }}
}
