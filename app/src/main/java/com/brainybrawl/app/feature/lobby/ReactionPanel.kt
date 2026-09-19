package com.brainybrawl.app.feature.lobby
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import kotlinx.coroutines.delay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.time.Instant
import android.os.SystemClock

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ReactionPanel(model:RoomViewModel,snapshot:RoomSnapshot,userId:String?){
    val state by model.actions.collectAsStateWithLifecycle()
    val locale=LocalConfiguration.current.locales[0].language
    LaunchedEffect(snapshot.room.id,locale){model.loadReactions(locale)}
    var open by remember{mutableStateOf(false)}
    var report by remember{mutableStateOf<ReactionEvent?>(null)}
    var now by remember{mutableLongStateOf(Instant.parse(snapshot.serverTime).toEpochMilli())}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(snapshot.serverTime,lifecycle){
        val anchor=SystemClock.elapsedRealtime();val server=Instant.parse(snapshot.serverTime).toEpochMilli()
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){while(true){now=server+SystemClock.elapsedRealtime()-anchor;delay(100)}}
    }
    val visible=snapshot.reactions.distinctBy{it.id}.filter{it.id !in state.hiddenReactions}
    visible.forEach{event->
        state.reactions.find{it.id==event.reactionId}?.let{preset->
            val age=now-Instant.parse(event.createdAt).toEpochMilli()
            if(age in 0..preset.durationMillis){
                BrawlPanel(Modifier.fillMaxWidth()){
                    Text(namedString(R.string.reaction_from,"name" to (snapshot.members.find{it.userId==event.userId}?.username?:stringResource(R.string.player)),"reaction" to preset.text))
                    if(event.userId!=userId)TextButton({report=event}){Text(stringResource(R.string.report))}
                }
            }
        }
    }
    if(state.reactions.isEmpty()&&state.failed)TextButton({model.loadReactions(locale)}){Text(stringResource(R.string.retry))}
    BrawlButton(stringResource(R.string.quick_replies),{open=true},enabled=state.reactions.isNotEmpty()&&!state.busy)
    if(open)ModalBottomSheet(onDismissRequest={open=false}){
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp)){
            Text(stringResource(R.string.quick_replies),style=MaterialTheme.typography.titleLarge)
            state.reactions.forEach{preset->BrawlButton(preset.text,{model.react(preset.id);open=false},Modifier.fillMaxWidth(),enabled=!state.busy)}
        }
    }
    report?.let{event->AlertDialog(onDismissRequest={report=null},title={Text(stringResource(R.string.report))},text={Text(stringResource(R.string.report_reaction_detail))},
        confirmButton={TextButton({model.reportReaction(event);report=null}){Text(stringResource(R.string.send_report))}},dismissButton={TextButton({report=null}){Text(stringResource(R.string.cancel))}})}
}
