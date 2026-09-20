package com.brainybrawl.app.core.design

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

/** Owner-supplied artwork, rendered without tint and decoded off the UI thread. */
@Composable fun GameArtwork(name:String,modifier:Modifier=Modifier){
    val state by rememberArtwork("assets/ui/$name.png")
    (state as? ArtworkState.Ready)?.let{Image(it.bitmap.asImageBitmap(),null,modifier)}
}
