package com.brainybrawl.app.feature.offline

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.feature.match.*
import com.brainybrawl.app.game.engine.ModeRules
import kotlinx.serialization.json.*

@Composable fun OfflinePuzzleScreen(model:OfflinePuzzleViewModel,onExit:()->Unit){
    val lifecycle=androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(model,lifecycle){
        val observer=androidx.lifecycle.LifecycleEventObserver{_,event->
            if(event==androidx.lifecycle.Lifecycle.Event.ON_START)model.setVisible(true)
            if(event==androidx.lifecycle.Lifecycle.Event.ON_STOP)model.setVisible(false)
        }
        lifecycle.addObserver(observer);model.setVisible(lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED))
        onDispose{lifecycle.removeObserver(observer);model.setVisible(false)}
    }
    androidx.activity.compose.BackHandler{model.stop();onExit()}
    val state by model.state.collectAsStateWithLifecycle()
    Text(stringResource(R.string.offline_no_flames))
    if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(state.failed)FeedbackPanel(stringResource(R.string.content_unavailable),stringResource(R.string.content_unavailable_detail))
    state.game?.let{game->
        val remaining=game.window.remaining(state.now)
        TimerBar(namedString(R.string.time_remaining,"seconds" to (remaining+999)/1000),remaining.toFloat()/ModeRules.PUZZLE_MILLIS)
        fun polygon(piece:com.brainybrawl.app.game.content.PuzzlePiece)=piece.polygon.joinToString(" "){"${it.x},${it.y}"}
        val board=PuzzleBoardView(
            game.puzzle.pieces.map{PuzzleTile(it.id,it.side,it.rotation,polygon(it),it.assetRef)},
            game.puzzle.pieces.map{PuzzleSlot(it.slot,polygon(it))},
            game.puzzle.pieces.filter{it.id in game.placed}.map{PuzzlePlacement(it.id,it.slot,"practice")},emptyList(),
            game.wrongSlot?.let{listOf(PuzzleAttempt("practice",buildJsonObject{put("slot_id",it)},false))}?:emptyList())
        PuzzleGame(board,game.puzzle.assetRef,0,"practice",!game.finished,model::place,{_,_->},solo=true)
        if(game.finished){
            FeedbackPanel(stringResource(if(game.score==96)R.string.puzzle_complete else R.string.time_expired),namedString(R.string.puzzle_progress,"count" to game.score,"total" to 96))
            Text(namedString(R.string.offline_best,"score" to state.best))
        }
    }
    BrawlButton(stringResource(R.string.leave_offline),{model.stop();onExit()},Modifier.fillMaxWidth(),tone=ActionTone.DESTRUCTIVE)
}
