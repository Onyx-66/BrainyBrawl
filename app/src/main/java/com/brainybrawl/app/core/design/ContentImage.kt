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
private val artworkCache=object:android.util.LruCache<String,Bitmap>(24*1024*1024){
    override fun sizeOf(key:String,value:Bitmap)=value.allocationByteCount
}
/** Packaged files only; bounded decode/cache. Never fetches arbitrary URLs. */
fun loadPackagedArtwork(assets:android.content.res.AssetManager,reference:String):Bitmap {
    require(reference.matches(Regex("assets/[A-Za-z0-9_/.-]+"))&&reference.split('/').none{it==".."})
    artworkCache.get(reference)?.let{return it}
    val bitmap=if(reference.endsWith(".svg")){
        val text=com.brainybrawl.app.core.security.PackagedSvg.read(reference,assets.open(reference))
        val svg=SVG.getFromString(text).apply{setDocumentWidth(1080f);setDocumentHeight(720f)}
        Bitmap.createBitmap(1080,720,Bitmap.Config.ARGB_8888).also{svg.renderToCanvas(android.graphics.Canvas(it))}
    }else{
        require(reference.substringAfterLast('.').lowercase(java.util.Locale.ROOT) in setOf("png","jpg","jpeg","webp"))
        val bytes=assets.open(reference).use{input->
            val out=java.io.ByteArrayOutputStream();val buffer=ByteArray(8192)
            while(true){val count=input.read(buffer);if(count<0)break;require(out.size()+count<=16*1024*1024);out.write(buffer,0,count)}
            out.toByteArray()
        }
        val options=android.graphics.BitmapFactory.Options().apply{inJustDecodeBounds=true}
        android.graphics.BitmapFactory.decodeByteArray(bytes,0,bytes.size,options)
        require(options.outWidth>0&&options.outHeight>0&&options.outWidth.toLong()*options.outHeight<=120_000_000)
        options.inJustDecodeBounds=false;options.inScaled=false
        while(options.outWidth.toLong()*options.outHeight/(options.inSampleSize.coerceAtLeast(1).toLong()*options.inSampleSize.coerceAtLeast(1))>3_000_000){options.inSampleSize=options.inSampleSize.coerceAtLeast(1)*2}
        requireNotNull(android.graphics.BitmapFactory.decodeByteArray(bytes,0,bytes.size,options))
    }
    artworkCache.put(reference,bitmap);return bitmap
}
@Composable fun rememberArtwork(reference:String):State<ArtworkState>{
    val context=LocalContext.current.applicationContext
    return produceState<ArtworkState>(ArtworkState.Loading,reference){
        withContext(Dispatchers.Main.immediate){
            value=ArtworkState.Loading
            try{value=ArtworkState.Ready(withContext(Dispatchers.IO){loadPackagedArtwork(context.assets,reference)})}
            catch(e:CancellationException){throw e}catch(_:Exception){value=ArtworkState.Failed}
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
