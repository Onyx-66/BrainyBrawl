package com.brainybrawl.app.feature.leaderboard
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString

@Composable fun LeaderboardScreen(model:LeaderboardViewModel,signedIn:Boolean){
    val state by model.state.collectAsStateWithLifecycle()
    Text(stringResource(R.string.leaderboards),style=MaterialTheme.typography.headlineMedium)
    if(!signedIn){Text(stringResource(R.string.sign_in_required));return}
    val filter=state.filter
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())){
        listOf("duel" to R.string.duel,"duo" to R.string.duo,"squad" to R.string.squad,"solo" to R.string.solo).forEach{(mode,label)->
            FilterChip(filter.mode==mode,{model.load(filter.copy(mode=mode,metric=if(mode=="duel")"win_rate" else "highest_score"))},label={Text(stringResource(label))})
        }
    }
    val metrics=when(filter.mode){"duel"->listOf("win_rate");"solo"->listOf("highest_score");else->listOf("highest_score","win_rate","top_rate")}
    metrics.forEach{metric->
        FilterChip(filter.metric==metric,{model.load(filter.copy(metric=metric))},label={Text(stringResource(when(metric){"highest_score"->R.string.highest_score;"win_rate"->R.string.win_rate;else->if(filter.mode=="duo")R.string.top_five_rate else R.string.top_three_rate}))})
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
        FilterChip(filter.period=="weekly",{model.load(filter.copy(period="weekly"))},label={Text(stringResource(R.string.weekly))})
        FilterChip(filter.period=="all_time",{model.load(filter.copy(period="all_time"))},label={Text(stringResource(R.string.all_time))})
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
        Text(stringResource(R.string.friends_only));Switch(filter.friends,{model.load(filter.copy(friends=it))})
    }
    if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(state.failed)FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.network_error),stringResource(R.string.retry),{model.load(filter)})
    state.board?.let{board->
        BrawlPanel(Modifier.fillMaxWidth()){
            Text(stringResource(R.string.your_rank),style=MaterialTheme.typography.titleLarge)
            board.own?.let{RankRow(it,filter.metric!="highest_score")}?:Text(stringResource(R.string.unranked))
        }
        if(board.rows.isEmpty()){
            androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(R.drawable.bb_state_no_rankings),null,Modifier.fillMaxWidth().height(120.dp))
            Text(stringResource(R.string.no_rankings))
        }
        board.rows.forEach{player->BrawlPanel(Modifier.fillMaxWidth()){RankRow(player,filter.metric!="highest_score")}}
    }
}
@Composable private fun RankRow(player:RankedPlayer,percentage:Boolean){
    Text(namedString(R.string.rank_name,"rank" to player.rank,"name" to player.username),style=MaterialTheme.typography.titleMedium)
    Text(namedString(if(percentage)R.string.metric_percent else R.string.session_score,"score" to player.value))
    Text(namedString(R.string.games_played,"count" to player.played))
}
