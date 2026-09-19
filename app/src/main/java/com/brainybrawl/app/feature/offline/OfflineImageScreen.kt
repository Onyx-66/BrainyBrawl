package com.brainybrawl.app.feature.offline
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.compose.*
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString

@Composable fun OfflineImageScreen(model:OfflineImageViewModel,exit:()->Unit){
 val state by model.state.collectAsStateWithLifecycle()
 val lifecycle=LocalLifecycleOwner.current.lifecycle
 DisposableEffect(model,lifecycle){
  val observer=LifecycleEventObserver{_,event->if(event==Lifecycle.Event.ON_START)model.setVisible(true)else if(event==Lifecycle.Event.ON_STOP)model.setVisible(false)}
  lifecycle.addObserver(observer);model.setVisible(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
  onDispose{lifecycle.removeObserver(observer);model.setVisible(false)}
 }
 androidx.activity.compose.BackHandler{model.stop();exit()}
 Text(stringResource(R.string.offline_images),style=MaterialTheme.typography.headlineMedium)
 Text(stringResource(R.string.offline_no_flames))
 if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth())
 if(state.failed)Text(stringResource(R.string.content_unavailable))
 state.game?.let{game->
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(namedString(R.string.round_count,"current" to game.index+1,"total" to game.images.size));Text(namedString(R.string.session_score,"score" to game.score))}
  BrawlPanel(Modifier.fillMaxWidth()){
   Text(game.image.theme,style=MaterialTheme.typography.labelLarge)
   Text(game.image.prompt,Modifier.testTag("offline-image"),style=MaterialTheme.typography.titleLarge)
   var expanded by remember(game.index){mutableStateOf(false)}
   ContentImage(game.image.assetRef,game.image.specification,Modifier.clickable{expanded=true})
   if(expanded)ImageViewer(stringResource(R.string.close_image),{expanded=false}){ContentImage(game.image.assetRef,game.image.specification)}
  }
  if(!game.revealed)TimerBar(namedString(R.string.answer_seconds,"seconds" to (game.window.remaining(state.now)+999)/1000),game.window.remaining(state.now)/30_000f)
  game.image.choices.chunked(2).forEach{row->Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
   row.forEach{choice->AnswerOption(if(game.revealed)choice.label+" · "+if(choice.correct)choice.points else 0 else choice.label,choice.id in game.selected,!game.revealed,{model.select(choice.id)},Modifier.weight(1f).fillMaxHeight(),if(game.revealed){if(choice.correct)true else if(choice.id in game.selected)false else null}else null)}
  }}
  if(!game.revealed)BrawlButton(namedString(R.string.confirm_four,"count" to game.selected.size),model::confirm,Modifier.fillMaxWidth(),enabled=game.selected.size==4,tone=ActionTone.POSITIVE)
  else{
   Text(game.image.explanation)
   if(!game.confirmed)Text(stringResource(R.string.time_expired))
   if(!game.finished)BrawlButton(stringResource(R.string.next_question),model::next,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
   else{Text(namedString(R.string.session_ratio,"earned" to game.score,"possible" to game.possible));Text(namedString(R.string.offline_best,"score" to state.best))}
  }
 }
 BrawlButton(stringResource(R.string.leave_offline),{model.stop();exit()},Modifier.fillMaxWidth(),tone=ActionTone.DESTRUCTIVE)
}
