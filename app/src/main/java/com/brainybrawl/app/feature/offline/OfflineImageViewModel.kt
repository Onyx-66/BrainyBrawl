package com.brainybrawl.app.feature.offline
import android.os.SystemClock
import androidx.lifecycle.*
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.OfflineImages
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

data class OfflineImageUi(val game:OfflineImages?=null,val now:Long=0,val loading:Boolean=false,val failed:Boolean=false,val best:Int=0)
class OfflineImageViewModel(private val content:ContentRepository,private val stats:OfflineStatistics):ViewModel(){
 private val mutable=MutableStateFlow(OfflineImageUi());val state=mutable.asStateFlow()
 private var task:Job?=null;private val visible=MutableStateFlow(false)
 fun setVisible(value:Boolean){visible.value=value}
 fun start(locale:String){
  task?.cancel();mutable.value=OfflineImageUi(loading=true)
  val owner="images:"+stats.currentOwner();val session=UUID.randomUUID().toString()
  task=viewModelScope.launch{
   try{
    val images=content.load(ContentKind.IMAGE,locale).filterIsInstance<ImageContent>().filter{it.scoringPolicy!=null}.shuffled().take(5)
    val time=SystemClock.elapsedRealtime();mutable.value=OfflineImageUi(game=OfflineImages(images,time),now=time)
    while(isActive){
     visible.first{it};val now=SystemClock.elapsedRealtime();mutable.update{it.copy(now=now,game=it.game?.tick(now))}
     val game=mutable.value.game!!
     if(game.finished){val saved=withContext(Dispatchers.IO){stats.record(session,game.score,game.possible,owner)};mutable.update{it.copy(best=saved.best)};break}
     delay(100)
    }
   }catch(e:CancellationException){throw e}catch(_:Exception){mutable.update{it.copy(loading=false,failed=true)}}
  }
 }
 fun select(id:String){mutable.update{it.copy(game=it.game?.select(id,SystemClock.elapsedRealtime()))}}
 fun confirm(){mutable.update{it.copy(game=it.game?.confirm(SystemClock.elapsedRealtime()))}}
 fun next(){val now=SystemClock.elapsedRealtime();mutable.update{it.copy(game=it.game?.next(now),now=now)}}
 fun stop(){task?.cancel();mutable.value=OfflineImageUi()}
 companion object{fun factory(content:ContentRepository,stats:OfflineStatistics)=object:ViewModelProvider.Factory{
  @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass:Class<T>):T=OfflineImageViewModel(content,stats) as T
 }}
}
