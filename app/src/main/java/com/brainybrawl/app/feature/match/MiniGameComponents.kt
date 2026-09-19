package com.brainybrawl.app.feature.match

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.ui.theme.*
import java.time.Instant
import kotlin.math.*

@Composable fun PrecisionGame(board:PrecisionBoard,round:MatchRound,players:List<MatchPlayer>,self:String?,now:Long,enabled:Boolean,onTap:()->Unit){
    val starts=Instant.parse(round.startsAt).toEpochMilli()
    val seat=((now-starts).coerceAtLeast(0)/20_000).toInt().coerceAtMost(3)
    val active=players.find{it.seat==seat}
    val state=board.players.find{it.userId==active?.userId}
    Text(stringResource(R.string.precision_tap),style=MaterialTheme.typography.headlineSmall)
    Text(namedString(R.string.active_player,"name" to (active?.name?:stringResource(R.string.player))))
    Text(namedString(R.string.turn_remaining,"seconds" to ((starts+(seat+1)*20_000-now).coerceIn(0,20_000)+999)/1000))
    state?.let{
        val speed=board.config.speed+state.streak*board.config.speedIncrement
        val angle=(state.position+(now-Instant.parse(state.anchorAt).toEpochMilli()).coerceAtLeast(0)*speed/1000)%360
        val width=min(board.config.maxWidth,board.config.width+state.streak*board.config.widthIncrement)
        Canvas(Modifier.fillMaxWidth().height(240.dp)){
            val diameter=min(size.width,size.height)*.78f;val origin=Offset((size.width-diameter)/2,(size.height-diameter)/2)
            drawArc(PanelRaised,0f,360f,false,origin,Size(diameter,diameter),style=Stroke(18.dp.toPx()))
            drawArc(Positive,state.zoneStart.toFloat(),width.toFloat(),false,origin,Size(diameter,diameter),style=Stroke(18.dp.toPx()))
            val radians=angle*PI/180;val radius=diameter/2
            drawCircle(Gold,12.dp.toPx(),center+Offset(cos(radians).toFloat()*radius,sin(radians).toFloat()*radius))
        }
        Text(namedString(R.string.current_streak,"count" to state.streak))
    }
    BrawlButton(stringResource(R.string.tap_now),onTap,Modifier.fillMaxWidth(),enabled=enabled&&active?.userId==self&&now<starts+(seat+1)*20_000,tone=ActionTone.POSITIVE)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun SpeedSortGame(board:SortBoard,enabled:Boolean,onSort:(Int,String)->Unit){
    var dragged by remember(board.index){mutableStateOf(Offset.Zero)}
    var dragOrigin by remember{mutableStateOf(Offset.Zero)}
    var origin by remember{mutableStateOf(Offset.Zero)}
    val targets=remember{mutableStateMapOf<String,Rect>()}
    Text(stringResource(R.string.speed_sort),style=MaterialTheme.typography.headlineSmall)
    Text(stringResource(R.string.sort_instruction))
    BrawlPanel(Modifier.fillMaxWidth().onGloballyPositioned{origin=it.boundsInRoot().center}
        .graphicsLayer{translationX=dragged.x;translationY=dragged.y}
        .pointerInput(board.index,enabled){if(enabled)detectDragGestures(
            onDragStart={dragOrigin=origin},onDragCancel={dragged=Offset.Zero},
            onDragEnd={val drop=dragOrigin+dragged;targets.entries.firstOrNull{it.value.contains(drop)}?.let{onSort(board.index,it.key)};dragged=Offset.Zero},
            onDrag={change,amount->change.consume();dragged+=amount})}){
        Text(board.label,style=MaterialTheme.typography.headlineMedium)
    }
    FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
        board.buckets.forEach{bucket->BrawlButton(bucket,{onSort(board.index,bucket)},Modifier.onGloballyPositioned{targets[bucket]=it.boundsInRoot()},enabled=enabled)}
    }
    Text(namedString(R.string.current_streak,"count" to board.streak))
}
@Composable fun ScrambleGame(round:MatchRound,enabled:Boolean,onAnswer:(String)->Unit){
    var answer by remember(round.id){mutableStateOf("")}
    Text(stringResource(R.string.word_scramble),style=MaterialTheme.typography.headlineSmall)
    BrawlPanel(Modifier.fillMaxWidth()){Text(round.content.letters,style=MaterialTheme.typography.headlineLarge,letterSpacing=3.sp)}
    OutlinedTextField(answer,{answer=it.take(200)},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.your_answer))},enabled=enabled,singleLine=true)
    BrawlButton(stringResource(R.string.submit_answer),{onAnswer(answer)},Modifier.fillMaxWidth(),enabled=enabled&&answer.isNotBlank(),tone=ActionTone.POSITIVE)
}
