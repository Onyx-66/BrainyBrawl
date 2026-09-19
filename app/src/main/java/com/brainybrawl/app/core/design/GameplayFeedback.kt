package com.brainybrawl.app.core.design

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.BrainyBrawlApplication

/** Feedback follows accepted gameplay events; it never drives scoring. */
@Composable fun GameplayFeedback(event:String?,correct:Boolean){
    val app=LocalContext.current.applicationContext as BrainyBrawlApplication
    val settings by app.container.settings.state.collectAsStateWithLifecycle()
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val haptic=LocalHapticFeedback.current
    var previous by rememberSaveable{mutableStateOf<String?>(null)}
    val tone=remember{runCatching{ToneGenerator(AudioManager.STREAM_MUSIC,35)}.getOrNull()}
    DisposableEffect(tone){onDispose{tone?.release()}}
    LaunchedEffect(event){
        if(event!=null&&event!=previous){
            previous=event
            if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
                if(settings.audio)tone?.startTone(if(correct)ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK,100)
                if(settings.vibration)haptic.performHapticFeedback(if(correct)HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove)
            }
        }
    }
}
