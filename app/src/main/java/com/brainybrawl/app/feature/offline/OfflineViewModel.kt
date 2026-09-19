package com.brainybrawl.app.feature.offline

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.OfflineQuestions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

data class OfflineStats(val best:Int=0,val earned:Long=0,val possible:Long=0)
data class OfflineUi(val loading:Boolean=false,val failed:Boolean=false,val game:OfflineQuestions?=null,
    val now:Long=0,val stats:OfflineStats=OfflineStats(),val saved:Boolean=false)
class OfflineStatistics(context:Context) {
    private val preferences=context.getSharedPreferences("offline_question_statistics",Context.MODE_PRIVATE)
    fun read()=OfflineStats(preferences.getInt("best",0),preferences.getLong("earned",0),preferences.getLong("possible",0))
    @Synchronized fun record(id:String,score:Int,total:Int):OfflineStats {
        require(score in 0..total && total>0)
        if(preferences.getString("last_session",null)==id)return read()
        val old=read()
        check(preferences.edit().putString("last_session",id).putInt("best",maxOf(old.best,score))
            .putLong("earned",old.earned+score).putLong("possible",old.possible+total).commit())
        return read()
    }
}
class OfflineViewModel(private val content:ContentRepository,private val statistics:OfflineStatistics):ViewModel() {
    private val mutable=MutableStateFlow(OfflineUi())
    val state=mutable.asStateFlow()
    private var ticker:Job?=null
    private val visible=MutableStateFlow(false)
    fun setVisible(value:Boolean){visible.value=value}
    private var session=UUID.randomUUID().toString()
    fun start(locale:String) {
        if(mutable.value.loading)return
        ticker?.cancel()
        session=UUID.randomUUID().toString()
        mutable.value=OfflineUi(loading=true)
        ticker=viewModelScope.launch {
            try {
                val questions=content.load(ContentKind.QUESTION,locale).filterIsInstance<QuestionContent>().shuffled().take(15)
                require(questions.isNotEmpty())
                mutable.value=OfflineUi(game=OfflineQuestions(questions,startedAt=SystemClock.elapsedRealtime()),
                    now=SystemClock.elapsedRealtime(),stats=withContext(Dispatchers.IO){statistics.read()})
                while(isActive) {
                    visible.first{it}
                    val now=SystemClock.elapsedRealtime()
                    mutable.update{it.copy(now=now,game=it.game?.tick(now))}
                    val current=mutable.value
                    if(current.game?.finished==true && !current.saved) {
                        val game=current.game
                        val stats=withContext(Dispatchers.IO){statistics.record(session,game.score,game.questions.size)}
                        mutable.update{it.copy(stats=stats,saved=true)}
                        break
                    }
                    delay(100)
                }
            } catch(cancelled:CancellationException){throw cancelled}
            catch(_:Exception){Diagnostics.record(ProductEvent.CONTENT_ERROR);mutable.update{it.copy(loading=false,failed=true)}}
        }
    }
    fun answer(id:String){mutable.update{it.copy(game=it.game?.answer(id,SystemClock.elapsedRealtime()))}}
    fun next(){mutable.update{it.copy(game=it.game?.next(SystemClock.elapsedRealtime()),now=SystemClock.elapsedRealtime())}}
    fun stop(){ticker?.cancel();mutable.value=OfflineUi()}
    companion object {
        fun factory(content:ContentRepository,stats:OfflineStatistics)=object:ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass:Class<T>):T=OfflineViewModel(content,stats) as T
        }
    }
}
