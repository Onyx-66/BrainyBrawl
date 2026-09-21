package com.brainybrawl.app.core.design

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

// Visible alpha bounds measured from the supplied originals; files remain untouched.
private val artworkBounds=mapOf(
    "currency_count" to Rect(0.022099f,0.116022f,0.977901f,0.864641f),
    "squad_bluetooth_icon" to Rect(0.098884f,0.141946f,0.891547f,0.837321f),
    "squad_bluetooth_card" to Rect(0.018341f,0.038278f,0.979266f,0.962520f),
    "solo_bluetooth_icon" to Rect(0.122931f,0.052009f,0.905437f,0.950355f),
    "solo_bluetooth_card" to Rect(0.018341f,0.033493f,0.982456f,0.957735f),
    "duo_bluetooth_icon" to Rect(0.058000f,0.118000f,0.934000f,0.848000f),
    "duo_bluetooth_card" to Rect(0.015949f,0.031100f,0.984051f,0.961722f),
    "bluetooth_icon" to Rect(0.201754f,0.059011f,0.801435f,0.946571f),
    "bluetooth_card" to Rect(0.021563f,0.272642f,0.977763f,0.740566f),
    "1v1_bluetooth_icon" to Rect(0.116000f,0.102000f,0.882000f,0.868000f),
    "1v1_bluetooth_card" to Rect(0.019936f,0.034290f,0.980064f,0.948963f),
    "1v1_card" to Rect(0.019936f,0.030303f,0.980861f,0.945774f),
    "1v1_icon" to Rect(0.133333f,0.142105f,0.875439f,0.871930f),
    "coin_count" to Rect(0.023020f,0.131215f,0.976980f,0.853591f),
    "coin_icon" to Rect(0.045455f,0.046252f,0.953748f,0.949761f),
    "coin_more" to Rect(0.067449f,0.087977f,0.909091f,0.912023f),
    "duo_card" to Rect(0.033493f,0.051834f,0.966507f,0.922648f),
    "duo_icon" to Rect(0.101351f,0.143581f,0.942568f,0.846284f),
    "flame_count" to Rect(0.021921f,0.174178f,0.979123f,0.721072f),
    "flame_icon" to Rect(0.141946f,0.057416f,0.853270f,0.899522f),
    "flame_more" to Rect(0.066066f,0.060060f,0.927928f,0.912913f),
    "games_icon" to Rect(0.030545f,0.152098f,0.969455f,0.897727f),
    "gem_count" to Rect(0.000000f,0.000000f,1.000000f,1.000000f),
    "gem_icon" to Rect(0.048644f,0.157895f,0.950558f,0.891547f),
    "gem_more" to Rect(0.063063f,0.072072f,0.924925f,0.921922f),
    "home_icon" to Rect(0.123636f,0.104021f,0.873455f,0.902098f),
    "offline_card" to Rect(0.029696f,0.254144f,0.970994f,0.748619f),
    "offline_icon" to Rect(0.171224f,0.141602f,0.824219f,0.916992f),
    "profile_icon" to Rect(0.183273f,0.082168f,0.815273f,0.923077f),
    "solo_card" to Rect(0.011164f,0.037480f,0.986443f,0.951356f),
    "solo_icon" to Rect(0.133065f,0.082661f,0.893145f,0.949597f),
    "squad_card" to Rect(0.016746f,0.032695f,0.983254f,0.966507f),
    "squad_icon" to Rect(0.064062f,0.125000f,0.917188f,0.884375f),
    "start_game_icon" to Rect(0.047085f,0.056170f,0.983558f,0.978723f),
    "store_icon" to Rect(0.123636f,0.099650f,0.874909f,0.913462f)
)
@Composable fun GameArtwork(name:String,modifier:Modifier=Modifier,contentScale:ContentScale=ContentScale.Fit,reference:String="assets/${if(name.endsWith("_icon"))"icons" else "ui"}/$name.png"){
    val state by rememberArtwork(reference)
    (state as? ArtworkState.Ready)?.let{ready->
        val painter=remember(ready.bitmap,name){
            val b=artworkBounds[name]?:Rect(0f,0f,1f,1f)
            val w=ready.bitmap.width;val h=ready.bitmap.height
            val left=(b.left*w).toInt();val top=(b.top*h).toInt()
            BitmapPainter(ready.bitmap.asImageBitmap(),IntOffset(left,top),IntSize(((b.right*w).toInt()-left).coerceAtLeast(1),((b.bottom*h).toInt()-top).coerceAtLeast(1)))
        }
        Image(painter,null,modifier,contentScale=contentScale)
    }
}
