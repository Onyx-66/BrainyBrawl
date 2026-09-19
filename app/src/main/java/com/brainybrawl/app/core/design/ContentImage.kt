package com.brainybrawl.app.core.design

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.ui.theme.Cyan
import com.caverock.androidsvg.SVG
import kotlinx.coroutines.*

sealed interface ArtworkState {
    data object Loading:ArtworkState
    data object Failed:ArtworkState
    data class Ready(val bitmap:Bitmap):ArtworkState
}
private val artworkCache=android.util.LruCache<String,Bitmap>(4)
/** Bounded packaged artwork cache. Bitmap lifetime follows active UI references. */
@Composable fun rememberArtwork(reference:String):State<ArtworkState>{
    val context=LocalContext.current.applicationContext
    return produceState<ArtworkState>(ArtworkState.Loading,reference){
        // Publish UI state on the Android looper even with an unconfined caller.
        withContext(Dispatchers.Main.immediate){
            value=ArtworkState.Loading
            try{value=ArtworkState.Ready(withContext(Dispatchers.IO){
                artworkCache.get(reference)?:run{
                    val text=com.brainybrawl.app.core.security.PackagedSvg.read(reference,context.assets.open(reference))
                    val svg=SVG.getFromString(text).apply{setDocumentWidth(1080f);setDocumentHeight(720f)}
                    Bitmap.createBitmap(1080,720,Bitmap.Config.ARGB_8888).also{svg.renderToCanvas(android.graphics.Canvas(it));artworkCache.put(reference,it)}
                }
            })}catch(e:CancellationException){throw e}catch(_:Exception){value=ArtworkState.Failed}
        }
    }
}
@Composable fun ContentImage(reference:String,description:String,modifier:Modifier=Modifier){
    val state by rememberArtwork(reference)
    when(val current=state){
        is ArtworkState.Ready->Image(current.bitmap.asImageBitmap(),description,modifier.fillMaxWidth().aspectRatio(1.5f).border(2.dp,Cyan,RoundedCornerShape(12.dp)))
        ArtworkState.Failed->Text(stringResource(R.string.image_unavailable),modifier)
        ArtworkState.Loading->LinearProgressIndicator(modifier.fillMaxWidth())
    }
}
