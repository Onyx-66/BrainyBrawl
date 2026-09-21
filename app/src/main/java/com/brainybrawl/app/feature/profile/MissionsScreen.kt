package com.brainybrawl.app.feature.profile
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.R
import com.brainybrawl.app.core.AppContainer
import com.brainybrawl.app.core.design.*
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
@Serializable data class Mission(val id:String,val target:Int,val coins:Int,val flames:Int,val progress:Int=0,val claimed:Boolean=false)
@Composable fun MissionsScreen(container:AppContainer,online:Boolean,refreshWallet:()->Unit){
 val scope=rememberCoroutineScope();var rows by remember{mutableStateOf<List<Mission>>(emptyList())};var busy by remember{mutableStateOf(false)};var failed by remember{mutableStateOf(false)}
 val json=remember{Json{ignoreUnknownKeys=true}}
 suspend fun load(){rows=json.decodeFromString(requireNotNull(container.supabase).postgrest.rpc("mission_snapshot").data)}
 fun request(claim:String?=null){scope.launch{busy=true;failed=false;try{if(claim!=null)requireNotNull(container.supabase).postgrest.rpc("claim_mission",buildJsonObject{put("p_mission",claim)});load();refreshWallet()}catch(e:CancellationException){throw e}catch(_:Exception){failed=true}finally{busy=false}}}
 LaunchedEffect(online){if(online)request()}
 Text(stringResource(R.string.achievements),style=MaterialTheme.typography.headlineMedium)
 Text(stringResource(if(online)R.string.mission_intro else R.string.mission_online))
 if(!online)return
 if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
 if(failed)FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.network_error),stringResource(R.string.retry),{request()})
 rows.forEach{mission->BrawlPanel(Modifier.fillMaxWidth()){
  Text(stringResource(when(mission.id){"play_1"->R.string.mission_play_1;"play_5"->R.string.mission_play_5;"play_20"->R.string.mission_play_20;"win_1"->R.string.mission_win_1;"win_5"->R.string.mission_win_5;else->R.string.mission_correct_25}),style=MaterialTheme.typography.titleMedium)
  LinearProgressIndicator(progress={mission.progress.toFloat()/mission.target},modifier=Modifier.fillMaxWidth())
  Text("${mission.progress} / ${mission.target}",style=MaterialTheme.typography.labelLarge)
  Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){GameArtwork("coin_icon",Modifier.size(24.dp));Text("+${mission.coins}");if(mission.flames>0){GameArtwork("flame_icon",Modifier.size(24.dp));Text("+${mission.flames}")}}
  BrawlButton(stringResource(if(mission.claimed)R.string.reward_claimed else if(mission.progress>=mission.target)R.string.claim_reward else R.string.mission_locked),{request(mission.id)},Modifier.fillMaxWidth(),enabled=!busy&&!mission.claimed&&mission.progress>=mission.target,tone=ActionTone.REWARD)
 }}
}
