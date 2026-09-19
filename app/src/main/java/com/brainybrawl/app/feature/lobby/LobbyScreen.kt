package com.brainybrawl.app.feature.lobby

import androidx.compose.foundation.layout.*
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
import com.brainybrawl.app.feature.profile.FriendEntry
import java.time.Instant

@Composable
fun LobbyScreen(model:RoomViewModel,userId:String?,friends:List<FriendEntry>,onMatch:(String)->Unit,onLeave:()->Unit) {
    val locale=androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language

    val connection by model.connection.collectAsStateWithLifecycle()
    val actions by model.actions.collectAsStateWithLifecycle()
    val snapshot=when(val current=connection){is RoomConnection.Live->current.snapshot;is RoomConnection.Recovering->current.snapshot;else->null}
    val live=connection is RoomConnection.Live
    Text(stringResource(R.string.lobby),style=MaterialTheme.typography.headlineMedium)
    if(connection is RoomConnection.Connecting)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(connection is RoomConnection.Recovering)FeedbackPanel(stringResource(R.string.reconnecting),stringResource(R.string.reconnecting_description))
    if(actions.failed)Text(stringResource(R.string.lobby_action_failed),color=MaterialTheme.colorScheme.error)
    if(connection==RoomConnection.Empty&&!actions.busy) {
        Text(stringResource(if(actions.noPublicRoom)R.string.no_public_room else R.string.no_room));BrawlButton(stringResource(R.string.back_to_modes),onLeave)
    }
    snapshot?.let { s ->
        val self=s.members.find{it.userId==userId}
        val host=s.room.hostId==userId
        val enabled=live&&!actions.busy&&s.room.status=="lobby"
        Text(stringResource(when(s.room.mode){OnlineMode.DUEL->R.string.duel;OnlineMode.DUO->R.string.duo;OnlineMode.SQUAD->R.string.squad;OnlineMode.SOLO->R.string.solo}),style=MaterialTheme.typography.titleLarge)
        Text(namedString(R.string.player_count,"count" to s.members.size.toString(),"maximum" to when(s.room.mode){OnlineMode.DUEL->"2";OnlineMode.SOLO->"20";else->"40"}))
        if(host)Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Text(stringResource(R.string.public_matchmaking));Switch(s.room.matchmaking,{model.matchmaking(it)},enabled=enabled)
        } else Text(stringResource(if(s.room.matchmaking)R.string.public_room else R.string.private_room))
        val groups=if(s.teams.isEmpty())listOf<RoomTeam?>(null) else s.teams
        groups.forEach { team ->
            BrawlPanel(Modifier.fillMaxWidth()) {
                if(team!=null)Text(team.name,style=MaterialTheme.typography.titleLarge)
                s.members.filter{it.teamId==team?.id}.forEach { member ->
                    val recent=Instant.parse(member.lastSeen).plusSeconds(45).isAfter(Instant.parse(s.serverTime))
                    val status=stringResource(if(!recent)R.string.away else if(member.ready)R.string.ready else R.string.not_ready)
                    Text(namedString(R.string.member_status,"name" to (member.username?:stringResource(R.string.blocked_player)),"status" to status))
                }
                if(team!=null&&self?.teamId==team.id&&self.seat==0) {
                    var name by rememberSaveable(team.id){mutableStateOf(team.name)}
                    OutlinedTextField(name,{name=it.take(40)},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.team_name))},enabled=enabled)
                    BrawlButton(stringResource(R.string.save),{model.rename(name)},enabled=enabled&&name.isNotBlank())
                }
            }
        }
        if(s.room.status=="lobby") {
            BrawlButton(stringResource(if(self?.ready==true)R.string.not_ready else R.string.ready),{model.ready(self?.ready!=true)},Modifier.fillMaxWidth(),enabled=enabled,tone=ActionTone.POSITIVE)
            if(host&&s.room.mode!=OnlineMode.SOLO)BrawlButton(stringResource(R.string.start_match),{model.start(locale)},Modifier.fillMaxWidth(),enabled=enabled&&s.members.all{it.ready},tone=ActionTone.POSITIVE)
            if(s.room.mode==OnlineMode.SOLO)Text(stringResource(R.string.solo_rules_pending))
            Text(stringResource(R.string.invite_friends),style=MaterialTheme.typography.titleMedium)
            friends.filter{it.status=="accepted"&&s.members.none{m->m.userId==it.userId}}.forEach { friend ->
                BrawlButton(namedString(R.string.invite_named,"name" to friend.username),{model.invite(friend.userId)},Modifier.fillMaxWidth(),enabled=enabled)
            }
        }
        ReactionPanel(model,s,userId)
        BrawlButton(stringResource(R.string.leave_room),{model.leave()},Modifier.fillMaxWidth(),enabled=!actions.busy,tone=ActionTone.DESTRUCTIVE)
        LaunchedEffect(s.matchId){s.matchId?.let(onMatch)}
    }
    LaunchedEffect(actions.matchId){actions.matchId?.let(onMatch)}
}
