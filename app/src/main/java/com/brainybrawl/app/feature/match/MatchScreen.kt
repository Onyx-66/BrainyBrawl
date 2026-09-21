package com.brainybrawl.app.feature.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import kotlinx.coroutines.delay
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.serialization.json.*
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun MatchScreen(model:MatchViewModel,matchId:String?,onExit:()->Unit){
    LaunchedEffect(matchId){matchId?.let(model::open)}
    val connection by model.connection.collectAsStateWithLifecycle()
    val action by model.action.collectAsStateWithLifecycle()
    GameplayFeedback(action.feedback?.receipt,action.feedback?.correct==true)
    val snapshot=when(val current=connection){is MatchConnection.Live->current.snapshot;is MatchConnection.Recovering->current.snapshot;else->null}
    var now by remember{mutableLongStateOf(0)}
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    val running=snapshot?.match?.status in setOf("active","countdown")
    val tickMillis=if(snapshot?.board is PrecisionBoard)33L else 100L
    LaunchedEffect(snapshot?.match?.id,running,tickMillis,lifecycle){
        if(running)lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){while(true){now=model.now();delay(tickMillis)}}
    }
    if(connection is MatchConnection.Recovering)Text(stringResource(R.string.match_recovering),color=MaterialTheme.colorScheme.error)
    if(snapshot==null){Text(stringResource(R.string.match_loading));LinearProgressIndicator(Modifier.fillMaxWidth());return}
    val self=snapshot.participants.find{it.userId==model.userId}
    val teammates=if(snapshot.match.mode=="duel")snapshot.participants else if(snapshot.match.mode=="solo")snapshot.participants.filter{it.userId==model.userId} else snapshot.participants.filter{it.teamId==self?.teamId}
    teammates.chunked(2).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
        row.forEach{player->BrawlPanel(Modifier.weight(1f)){
            Text(player.name?:stringResource(R.string.player),style=MaterialTheme.typography.titleSmall)
            Text(namedString(R.string.session_score,"score" to player.score))
        }}
    }}
    var standings by remember{mutableStateOf(false)}
    if(snapshot.teams.isNotEmpty()){
        TextButton({standings=true}){Text(stringResource(R.string.team_standings))}
        if(standings)ModalBottomSheet(onDismissRequest={standings=false}){
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
                snapshot.teams.sortedByDescending{team->snapshot.participants.filter{it.teamId==team.id}.sumOf{it.score}}.forEach{team->
                    Text(namedString(R.string.team_total,"name" to team.name,"score" to snapshot.participants.filter{it.teamId==team.id}.sumOf{it.score}))
                }
                snapshot.teamRankings.filter{it.rolls.isNotEmpty()}.forEach{rank->
                    Text(namedString(R.string.team_rolls,"name" to (snapshot.teams.find{it.id==rank.teamId}?.name?:stringResource(R.string.player)),"rolls" to rank.rolls.joinToString(" / ")))
                }
            }
        }
    }
    if(snapshot.match.status in setOf("results","closed")){
        Text(stringResource(R.string.match_results),style=MaterialTheme.typography.headlineLarge)
        val ownRoll=snapshot.tieRolls.lastOrNull{it.userId==model.userId}?.roll
            ?:snapshot.teamRankings.lastOrNull{it.teamId==self?.teamId}?.rolls?.lastOrNull()
        ownRoll?.let{RouletteWheel(it)}
        snapshot.results.forEach{result->
            val name=snapshot.participants.find{it.userId==result.userId}?.name?:stringResource(R.string.player)
            BrawlPanel(Modifier.fillMaxWidth()){
                Text(namedString(R.string.result_rank,"rank" to result.rank,"name" to name,"score" to result.score))
                if(result.winner)Text(stringResource(R.string.winner_flame),color=com.brainybrawl.app.ui.theme.Gold)
            }
        }
        if(snapshot.tieRolls.isNotEmpty()){
            Text(stringResource(R.string.roulette_results),style=MaterialTheme.typography.titleLarge)
            snapshot.tieRolls.forEach{roll->Text(namedString(R.string.roulette_roll,"name" to (snapshot.participants.find{it.userId==roll.userId}?.name?:stringResource(R.string.player)),"attempt" to roll.attempt,"roll" to roll.roll))}
        }
        var reviewing by remember{mutableStateOf(false)}
        TextButton({reviewing=true}){Text(stringResource(R.string.review_answers))}
        if(reviewing)ModalBottomSheet(onDismissRequest={reviewing=false}){
            LazyColumn(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){
                items(snapshot.rounds.filter{it.kind in setOf("question_round","image_guess")},key={it.id}){RoundReveal(it)}
            }
        }
        BrawlButton(stringResource(R.string.return_modes),onExit,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
        return
    }
    if(action.failed)Text(stringResource(R.string.answer_retry),color=MaterialTheme.colorScheme.error)
    val round=snapshot.rounds.lastOrNull{it.status=="active"}
    val draft=snapshot.draft
    if(draft!=null){
        val answering=snapshot.participants.find{it.userId==draft.answerers[self?.teamId]}
        Text(namedString(R.string.designated_answerer,"name" to (answering?.name?:stringResource(R.string.player))))
        if(draft.roundId==null){
            Text(stringResource(R.string.choose_theme),style=MaterialTheme.typography.headlineSmall)
            val choosing=self?.teamId==draft.choosingTeam&&connection is MatchConnection.Live&&!action.busy
            if(!choosing)Text(namedString(R.string.waiting_for_team,"name" to (snapshot.teams.find{it.id==draft.choosingTeam}?.name?:stringResource(R.string.player))))
            draft.themes.forEach{theme->BrawlButton(theme,{model.chooseTheme(draft.question,theme)},Modifier.fillMaxWidth(),enabled=choosing)}
            return
        }
    }
    if(round==null){
        val remaining=(Instant.parse(snapshot.startsAt).toEpochMilli()-now).coerceAtLeast(0)
        Text(namedString(R.string.match_countdown,"seconds" to (remaining+999)/1000));return
    }
    val deadline=Instant.parse(round.deadline).toEpochMilli()
    val opens=Instant.parse(round.opensAt).toEpochMilli()
    val starts=Instant.parse(round.startsAt).toEpochMilli()
    val designated=round.kind!="question_round"||snapshot.match.mode in setOf("duel","solo")||draft?.answerers?.get(self?.teamId)==model.userId
    val accepting=connection is MatchConnection.Live&&now>=opens&&now<deadline&&round.submission==null&&!action.busy&&designated&&self?.eligible==true&&!(action.round==round.id&&action.accepted)
    val selected=if(action.round==round.id)action.selected else emptySet()
    Text(namedString(R.string.round_count,"current" to round.ordinal+1,"total" to when(snapshot.match.mode){"duo"->21;"squad"->22;"solo"->17;else->20}),style=MaterialTheme.typography.titleLarge)
    val reading=now<opens
    val remaining=(if(reading)opens-now else deadline-now).coerceAtLeast(0)
    val duration=if(reading)opens-starts else deadline-opens
    val timerLabel=when {
        now<starts->R.string.match_countdown
        reading->R.string.reading_seconds
        snapshot.board!=null->R.string.time_remaining
        else->R.string.answer_seconds
    }
    TimerBar(namedString(timerLabel,"seconds" to (remaining+999)/1000),remaining.toFloat()/duration.coerceAtLeast(1))
    when(val board=snapshot.board){
        is PrecisionBoard->PrecisionGame(board,round,teammates,model.userId,now,accepting){model.mini(round,buildJsonObject{})}
        is SortBoard->SpeedSortGame(board,accepting&&board.activeUser==model.userId){index,bucket->model.mini(round,buildJsonObject{put("index",index);put("bucket",bucket)})}
        is PuzzleBoardView->PuzzleGame(board,round.content.assetRef,self?.seat?:0,model.userId,accepting,
            {piece,slot,rotation->model.mini(round,buildJsonObject{put("piece_id",piece);put("slot_id",slot);put("rotation",rotation)})},
            {x,y->model.cursor(round.id,x,y)})
        is ScrambleBoard->ScrambleGame(round,accepting&&!board.answered){answer->model.mini(round,buildJsonObject{put("answer",answer)})}
        null->{
            QuestionPrompt(round.content.theme,round.content.prompt,round.content.specification)
            var expanded by remember(round.id){mutableStateOf(false)}
            round.content.assetRef?.let{asset->
                ContentImage(asset,round.content.specification,Modifier.clickable{expanded=true})
                if(expanded)ImageViewer(stringResource(R.string.close_image),{expanded=false}){ContentImage(asset,round.content.specification)}
            }
            round.content.options.forEach{option->AnswerOption(option.label,option.id in selected,accepting,{
                if(round.kind=="question_round")model.submit(round,option.id)else model.select(round,option.id)
            })}
            if(round.kind=="question_round"){
                var reply by remember(round.id){mutableStateOf("")}
                Text(stringResource(R.string.learning_tip),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.secondary)
            OutlinedTextField(reply,{reply=it.take(200)},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.answer_any_language))},enabled=accepting,singleLine=true)
                BrawlButton(stringResource(R.string.submit_answer),{model.submit(round,answer=reply)},Modifier.fillMaxWidth(),enabled=accepting&&reply.isNotBlank())
            }
            if(round.kind=="image_guess")BrawlButton(namedString(R.string.confirm_four,"count" to selected.size),{model.submit(round)},Modifier.fillMaxWidth(),enabled=accepting&&selected.size==4,tone=ActionTone.POSITIVE)
        }
    }
    if(round.submission!=null||action.round==round.id&&action.accepted)Text(stringResource(R.string.answer_received))
    if(action.round==round.id)action.feedback?.let{receipt->Text(namedString(if(receipt.correct)R.string.action_points else R.string.action_wrong,"points" to receipt.points),color=if(receipt.correct)com.brainybrawl.app.ui.theme.Positive else com.brainybrawl.app.ui.theme.Negative)}
    snapshot.rounds.lastOrNull{it.status=="results"}?.takeIf{it.kind in setOf("question_round","image_guess")}?.let{previous->
        var revealOpen by remember(previous.id){mutableStateOf(false)}
        TextButton({revealOpen=true}){Text(stringResource(R.string.previous_answer))}
        if(revealOpen)ModalBottomSheet(onDismissRequest={revealOpen=false}){
            Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)){RoundReveal(previous)}
        }
    }
}
