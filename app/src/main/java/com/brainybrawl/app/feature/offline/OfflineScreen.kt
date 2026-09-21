package com.brainybrawl.app.feature.offline

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString

@Composable fun OfflineScreen(model:OfflineViewModel,onExit:()->Unit) {
    val lifecycle=androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(model,lifecycle){
        val observer=androidx.lifecycle.LifecycleEventObserver{_,event->
            if(event==androidx.lifecycle.Lifecycle.Event.ON_START)model.setVisible(true)
            if(event==androidx.lifecycle.Lifecycle.Event.ON_STOP)model.setVisible(false)
        }
        lifecycle.addObserver(observer)
        model.setVisible(lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED))
        onDispose{lifecycle.removeObserver(observer);model.setVisible(false)}
    }
    androidx.activity.compose.BackHandler { model.stop();onExit() }
    val state by model.state.collectAsStateWithLifecycle()
    Text(stringResource(R.string.offline_questions),style=MaterialTheme.typography.headlineMedium)
    Text(stringResource(R.string.offline_no_flames))
    if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(state.failed)FeedbackPanel(stringResource(R.string.content_unavailable),stringResource(R.string.content_unavailable_detail),stringResource(R.string.play_english_questions),{model.start("en")})
    state.game?.let { game ->
        GameplayFeedback(if(game.revealed)"${game.startedAt}:${game.index}" else null,game.question.options.any{it.id==game.selected&&it.correct})
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Text(namedString(R.string.round_count,"current" to game.index+1,"total" to game.questions.size))
            Text(namedString(R.string.session_score,"score" to game.score))
        }
        val contentDirection=if(game.question.meta.locale=="ar")LayoutDirection.Rtl else LayoutDirection.Ltr
        CompositionLocalProvider(LocalLayoutDirection provides contentDirection){
        QuestionPrompt(game.question.theme,game.question.prompt,modifier=Modifier.testTag("offline-question"))
        }
        if(!game.revealed) {
            val reading=state.now<game.window.opensAt
            val remaining=(if(reading)game.window.opensAt-state.now else game.window.remaining(state.now)).coerceAtLeast(0)
            TimerBar(namedString(if(reading)R.string.reading_seconds else R.string.answer_seconds,"seconds" to (remaining+999)/1000),
                remaining.toFloat()/45_000)
        }
        CompositionLocalProvider(LocalLayoutDirection provides contentDirection){
        game.question.options.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                row.forEach { option ->
                    AnswerOption(option.label,game.selected==option.id,!game.revealed,{model.answer(option.id)},modifier=Modifier.weight(1f).fillMaxHeight(),result=if(game.revealed){if(option.correct)true else if(game.selected==option.id)false else null}else null)
                }
            }
        }
        }
        if(!game.revealed){
            var reply by remember(game.index){mutableStateOf("")}
            Text(stringResource(R.string.learning_tip),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.secondary)
            OutlinedTextField(reply,{reply=it.take(200)},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.answer_any_language))},singleLine=true)
            BrawlButton(stringResource(R.string.submit_answer),{model.answerText(reply)},Modifier.fillMaxWidth(),enabled=reply.isNotBlank())
        }
        if(game.revealed) {
            FeedbackPanel(stringResource(if(game.selected==null)R.string.time_expired else if(game.question.options.any{it.id==game.selected&&it.correct})R.string.correct_answer else R.string.wrong_answer),
                game.question.options.single{it.correct}.label+"\n"+game.question.explanation)
            if(!game.finished)BrawlButton(stringResource(R.string.next_question),model::next,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
            else {
                Text(namedString(R.string.session_ratio,"earned" to game.score,"possible" to game.questions.size))
                Text(namedString(R.string.offline_best,"score" to state.stats.best))
                Text(namedString(R.string.all_time_ratio,"earned" to state.stats.earned,"possible" to state.stats.possible))
            }
        }
    }
    BrawlButton(stringResource(R.string.leave_offline),{model.stop();onExit()},Modifier.fillMaxWidth(),tone=ActionTone.DESTRUCTIVE)
}
