package com.brainybrawl.app.core.design

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.ui.theme.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

/** Random only when a new application session prepares its artwork, never during recomposition. */
fun chooseScreenAsset(folder:String,names:List<String>,random:Random=Random.Default):String? {
    require(folder in setOf("home","splash"))
    val candidates=names.filter{it.matches(Regex("[A-Za-z0-9_.-]+\\.(png|jpg|jpeg|webp)",RegexOption.IGNORE_CASE))}.sorted()
    return candidates.takeIf{it.isNotEmpty()}?.random(random)?.let{"assets/screen/$folder/$it"}
}
data class ScreenArtworkState(val progress:Float=0f,val splash:Bitmap?=null,val home:Bitmap?=null,val ready:Boolean=false)
class ScreenArtRepository(context:Context){
    private val assets=context.applicationContext.assets
    private val mutex=Mutex()
    private val mutable=MutableStateFlow(ScreenArtworkState())
    val state=mutable.asStateFlow()
    suspend fun prepare()=mutex.withLock {
        if(mutable.value.ready)return@withLock
        withContext(Dispatchers.IO){
            fun select(folder:String)=try{chooseScreenAsset(folder,assets.list("assets/screen/$folder").orEmpty().toList())}catch(_:java.io.IOException){null}
            val splash=select("splash")
            val home=select("home")
            mutable.value=ScreenArtworkState(progress=.15f)
            fun decode(path:String?)=try{path?.let{loadPackagedArtwork(assets,it)}}catch(e:CancellationException){throw e}catch(_:Exception){null}
            val splashBitmap=decode(splash)
            mutable.value=ScreenArtworkState(.55f,splashBitmap)
            val homeBitmap=decode(home)
            mutable.value=ScreenArtworkState(1f,splashBitmap,homeBitmap,true)
        }
    }
}
@Composable fun ScreenBackdrop(bitmap:Bitmap?,opacity:Float,modifier:Modifier=Modifier){
    if(bitmap!=null)Image(bitmap.asImageBitmap(),null,modifier.fillMaxSize(),contentScale=ContentScale.Crop,alpha=opacity)
}
@Composable fun StartupSplash(art:ScreenArtworkState,waitingForAccount:Boolean){
    Box(Modifier.fillMaxSize().background(Navy).testTag("startup-splash")){
        ScreenBackdrop(art.splash,1f)
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal=42.dp),horizontalAlignment=Alignment.CenterHorizontally){
            Spacer(Modifier.weight(.7f))
            BrainMark(Modifier.fillMaxWidth(.85f).heightIn(max=300.dp).aspectRatio(1f))
            Spacer(Modifier.weight(1f))
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Navy.copy(alpha=.88f)).padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){
                Text(stringResource(R.string.loading),style=MaterialTheme.typography.titleMedium,color=Color.White)
                val track=Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(8.dp)).border(1.dp,Color.White.copy(alpha=.3f),RoundedCornerShape(8.dp))
                if(waitingForAccount&&art.ready)LinearProgressIndicator(track,color=Positive,trackColor=PanelRaised)
                else LinearProgressIndicator(progress={art.progress.coerceIn(0f,1f)},modifier=track,color=Positive,trackColor=PanelRaised)
            }
            Spacer(Modifier.weight(.45f))
        }
    }
}
