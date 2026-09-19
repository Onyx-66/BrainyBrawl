package com.brainybrawl.app.feature.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.feature.profile.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(model: PlayerViewModel) {
    val ui by model.social.collectAsStateWithLifecycle()
    val profile by model.profile.collectAsStateWithLifecycle()
    val preview by model.preview.collectAsStateWithLifecycle()
    DisposableEffect(model){onDispose{model.closePreview()}}
    if(preview!=null)ModalBottomSheet(onDismissRequest=model::closePreview){
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            when(val current=preview){
                PlayerDataState.Loading->LinearProgressIndicator(Modifier.fillMaxWidth())
                PlayerDataState.Failed->Text(stringResource(R.string.request_failed))
                is PlayerDataState.Ready->{
                    Text(current.data.profile.username,style=MaterialTheme.typography.headlineMedium)
                    Text(namedString(R.string.player_number,"number" to current.data.profile.number.toString()))
                    Text(namedString(R.string.owned_cosmetics,"count" to current.data.inventory.size))
                    current.data.modeStats.forEach{stats->BrawlPanel(Modifier.fillMaxWidth()){
                        Text(stringResource(when(stats.mode){"duo"->R.string.duo;"squad"->R.string.squad;"solo"->R.string.solo;else->R.string.duel}),style=MaterialTheme.typography.titleMedium)
                        Text(namedString(R.string.games_played,"count" to stats.played))
                        Text(namedString(R.string.games_won,"count" to stats.wins))
                        Text(namedString(R.string.mode_best,"score" to stats.best))
                    }}
                }
                else->Unit
            }
        }
    }
    var query by rememberSaveable { mutableStateOf("") }
    var reportTarget by remember { mutableStateOf<FriendEntry?>(null) }
    var blockTarget by remember { mutableStateOf<FriendEntry?>(null) }
    Text(stringResource(R.string.friends),style=MaterialTheme.typography.headlineMedium)
    if(profile is PlayerDataState.SignedOut) { Text(stringResource(R.string.sign_in_required));return }
    OutlinedTextField(query,{query=it},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.find_player))},singleLine=true)
    BrawlButton(stringResource(R.string.search),{model.search(query)},Modifier.fillMaxWidth(),enabled=query.trim().length>=3 && !ui.busy)
    if(ui.busy || profile is PlayerDataState.Loading) LinearProgressIndicator(Modifier.fillMaxWidth())
    if(ui.failed || profile is PlayerDataState.Failed) FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.network_error),stringResource(R.string.retry),model::refresh)
    if(ui.reportSent) Text(stringResource(R.string.report_sent))
    if(ui.searched && ui.search.isEmpty()) Text(stringResource(R.string.no_players_found))
    ui.search.forEach { found ->
        BrawlPanel(Modifier.fillMaxWidth()) {
            Text(found.username,style=MaterialTheme.typography.titleLarge)
            Text(namedString(R.string.player_number,"number" to found.number.toString()))
            BrawlButton(stringResource(R.string.add_friend),{model.friend(found.id,"request")},Modifier.fillMaxWidth(),enabled=!ui.busy)
            TextButton(onClick={reportTarget=FriendEntry(found.id,found.number,found.username,"",false)}) { Text(stringResource(R.string.report)) }
        }
    }
    if(ui.snapshot.friends.isEmpty() && profile is PlayerDataState.Ready) Text(stringResource(R.string.empty_friends))
    ui.snapshot.friends.forEach { friend ->
        BrawlPanel(Modifier.fillMaxWidth()) {
            Text(friend.username,style=MaterialTheme.typography.titleLarge)
            Text(namedString(R.string.player_number,"number" to friend.number.toString()))
            if(friend.status=="pending") {
                Text(stringResource(if(friend.incoming) R.string.incoming_request else R.string.request_pending))
                if(friend.incoming) BrawlButton(stringResource(R.string.accept),{model.friend(friend.userId,"accept")},Modifier.fillMaxWidth(),enabled=!ui.busy,tone=ActionTone.POSITIVE)
                BrawlButton(stringResource(if(friend.incoming) R.string.decline else R.string.cancel),{model.friend(friend.userId,"decline")},Modifier.fillMaxWidth(),enabled=!ui.busy,tone=ActionTone.DESTRUCTIVE)
            } else {
                BrawlButton(stringResource(R.string.profile),{model.preview(friend.userId)},enabled=!ui.busy)
                TextButton(onClick={model.friend(friend.userId,"remove")},enabled=!ui.busy) { Text(stringResource(R.string.remove_friend)) }
            }
            Row {
                TextButton(onClick={blockTarget=friend},enabled=!ui.busy) { Text(stringResource(R.string.block)) }
                TextButton(onClick={reportTarget=friend},enabled=!ui.busy) { Text(stringResource(R.string.report)) }
            }
        }
    }
    if(ui.snapshot.blocks.isNotEmpty()) Text(stringResource(R.string.blocked_players),style=MaterialTheme.typography.titleLarge)
    ui.snapshot.blocks.forEach { blocked ->
        BrawlPanel(Modifier.fillMaxWidth()) {
            Text(namedString(R.string.player_number,"number" to blocked.number.toString()))
            BrawlButton(stringResource(R.string.unblock),{model.block(blocked.userId,false)},enabled=!ui.busy)
        }
    }
    blockTarget?.let { target ->
        AlertDialog(onDismissRequest={blockTarget=null},title={Text(stringResource(R.string.block))},
            text={Text(stringResource(R.string.block_description))},
            confirmButton={TextButton(onClick={model.block(target.userId,true);blockTarget=null}) { Text(stringResource(R.string.block)) }},
            dismissButton={TextButton(onClick={blockTarget=null}) { Text(stringResource(R.string.cancel)) }})
    }
    reportTarget?.let { target -> ReportDialog({reportTarget=null}) { category,details,block ->
        model.report(target.userId,category,details,block);reportTarget=null
    } }
}
@Composable
private fun ReportDialog(dismiss: ()->Unit,send: (String,String,Boolean)->Unit) {
    var category by remember { mutableStateOf("harassment") }
    var details by remember { mutableStateOf("") }
    var block by remember { mutableStateOf(false) }
    val categories=listOf("harassment" to R.string.report_harassment,"cheating" to R.string.report_cheating,
        "profile" to R.string.report_profile,"spam" to R.string.report_spam,"other" to R.string.report_other)
    AlertDialog(onDismissRequest=dismiss,title={Text(stringResource(R.string.report))},text={
        Column(Modifier.heightIn(max=480.dp).verticalScroll(rememberScrollState())) {
            categories.forEach { (key,label) ->
                Row { RadioButton(category==key,{category=key});TextButton(onClick={category=key}) { Text(stringResource(label)) } }
            }
            OutlinedTextField(details,{details=it.take(2000)},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.report_details))})
            Row { Checkbox(block,{block=it});Text(stringResource(R.string.also_block)) }
        }
    },confirmButton={TextButton(onClick={send(category,details,block)}) { Text(stringResource(R.string.send_report)) }},
        dismissButton={TextButton(onClick=dismiss) { Text(stringResource(R.string.cancel)) }})
}
