package com.brainybrawl.app.feature.friends

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.feature.auth.LocalAccounts
import kotlinx.coroutines.*

@Composable fun LocalFriendsScreen(accounts:LocalAccounts,coordinator:LocalSocialCoordinator,online:Boolean,connect:()->Unit){
    val state by accounts.state.collectAsStateWithLifecycle()
    var target by rememberSaveable{mutableStateOf("")}
    var mode by rememberSaveable{mutableStateOf("duel")}
    var busy by remember{mutableStateOf(false)}
    var failed by remember{mutableStateOf(false)}
    var attempted by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    var job by remember{mutableStateOf<Job?>(null)}
    DisposableEffect(state.current?.id,online){onDispose{job?.cancel()}}
    fun action(block:suspend()->Unit){if(!busy){busy=true;failed=false;job=scope.launch{try{block()}catch(e:CancellationException){throw e}catch(_:Exception){failed=true}finally{busy=false}}}}
    Text(stringResource(R.string.offline_social),style=MaterialTheme.typography.headlineSmall)
    Text(stringResource(R.string.offline_social_description),style=MaterialTheme.typography.bodyMedium)
    OutlinedTextField(target,{target=it.take(254)},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.find_player))},singleLine=true)
    BrawlButton(stringResource(R.string.queue_friend),{action{accounts.enqueue(target,"friend");target=""}},Modifier.fillMaxWidth(),enabled=!busy&&target.trim().length>=3)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
        listOf("duel" to R.string.duel,"duo" to R.string.duo,"squad" to R.string.squad).forEach{(key,label)->
            FilterChip(mode==key,{mode=key},label={Text(stringResource(label))})
        }
    }
    BrawlButton(stringResource(R.string.queue_invite),{action{accounts.enqueue(target,"invite",mode);target=""}},Modifier.fillMaxWidth(),enabled=!busy&&target.trim().length>=3,tone=ActionTone.POSITIVE)
    if(online)BrawlButton(stringResource(R.string.send_queued),{action{coordinator.sync();attempted=true}},Modifier.fillMaxWidth(),enabled=!busy)
    else BrawlButton(stringResource(R.string.connect_online),connect,Modifier.fillMaxWidth())
    if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(failed||attempted&&state.queue.any{!it.sent})Text(stringResource(R.string.queue_pending_reason))
    state.queue.forEach{item->BrawlPanel(Modifier.fillMaxWidth()){
        Text(item.target,style=MaterialTheme.typography.titleMedium)
        Text(stringResource(if(item.kind=="friend")R.string.queue_friend else R.string.queue_invite))
        Text(stringResource(if(item.sent)R.string.queue_sent else R.string.queue_waiting))
        TextButton({action{accounts.cancel(item.id)}},enabled=!busy){Text(stringResource(if(item.sent)R.string.dismiss else R.string.cancel))}
    }}
}
