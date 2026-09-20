package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.*

@Composable fun ProfileAvatar(repository:AppearanceRepository,user:String,label:String,modifier:Modifier=Modifier){
    val version by repository.revision.collectAsStateWithLifecycle()
    val selection=remember(user,version){repository.read(user)}
    AppearancePreview(selection,modifier)
}
@Composable fun AppearancePreview(selection:AppearanceSelection,modifier:Modifier=Modifier){
    val context=LocalContext.current.applicationContext
    val avatar by produceState<android.graphics.Bitmap?>(null,selection.avatar){value=withContext(Dispatchers.IO){AppearanceArt.avatar(context,selection.avatar)}}
    val frame by produceState<android.graphics.Bitmap?>(null,selection.frame){value=withContext(Dispatchers.IO){AppearanceArt.frame(context,selection.frame)}}
    val opening=remember(frame){frame?.let(AppearanceArt::opening)}
    BoxWithConstraints(modifier.aspectRatio(1f)){
        val aperture=opening?:android.graphics.RectF(.085f,.085f,.915f,.915f)
        avatar?.let{Image(it.asImageBitmap(),null,Modifier.offset(maxWidth*aperture.left,maxWidth*aperture.top)
            .size(maxWidth*aperture.width(),maxWidth*aperture.height()).clip(RoundedCornerShape(12)),contentScale=ContentScale.Crop)}
        frame?.let{Image(it.asImageBitmap(),null,Modifier.fillMaxSize())}
    }
}
