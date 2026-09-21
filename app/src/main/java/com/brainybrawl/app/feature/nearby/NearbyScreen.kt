package com.brainybrawl.app.feature.nearby

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.game.content.ContentRepository

@Composable fun NearbyScreen(content:ContentRepository,name:String,initial:NearbyMode,onExit:()->Unit){
    val context=LocalContext.current;val scope=rememberCoroutineScope()
    val session=remember{NearbySession(context.applicationContext,scope,content,name)}
    val state by session.state.collectAsState();val status by session.status.collectAsState()
    val locale=LocalConfiguration.current.locales[0].language.let{if(it in setOf("en","fr","ar"))it else "en"}
    var mode by remember{mutableStateOf(initial)}
    var devices by remember{mutableStateOf<List<PairedDevice>>(emptyList())}
    var accessError by remember{mutableStateOf(false)}
    var available by remember{mutableStateOf(false)}
    val required=remember{if(Build.VERSION.SDK_INT>=31)arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADVERTISE)else emptyArray()}
    fun refresh(){try{available=required.all{ContextCompat.checkSelfPermission(context,it)==PackageManager.PERMISSION_GRANTED}&&session.transport.adapter?.isEnabled==true;devices=if(available)session.transport.paired()else emptyList()}catch(_:SecurityException){available=false;accessError=true}}
    val external=rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){refresh()}
    val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){granted->accessError=granted.values.any{!it};refresh()}
    LaunchedEffect(Unit){refresh()}
    val view=LocalView.current
    DisposableEffect(session){onDispose{session.leave()}}
    BackHandler{session.leave();onExit()}
    Text(stringResource(R.string.bluetooth_play),Modifier.fillMaxWidth(),style=MaterialTheme.typography.headlineMedium)
    Text(stringResource(R.string.bluetooth_description),style=MaterialTheme.typography.bodyMedium)
    if(session.transport.adapter==null){FeedbackPanel(stringResource(R.string.bluetooth_unavailable),stringResource(R.string.bluetooth_hardware));BrawlButton(stringResource(R.string.back_to_modes),onExit);return}
    if(accessError){
        Text(stringResource(R.string.bluetooth_permission),color=MaterialTheme.colorScheme.error)
        TextButton({external.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+context.packageName)))}){Text(stringResource(R.string.settings))}
    }
    if(!available){
        BrawlButton(stringResource(R.string.bluetooth_enable),{
            if(required.any{ContextCompat.checkSelfPermission(context,it)!=PackageManager.PERMISSION_GRANTED})permission.launch(required)
            else try{external.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))}catch(_:Exception){accessError=true}
        },Modifier.fillMaxWidth())
    }
    if(status in setOf("failed","disconnected","content_failed"))Text(stringResource(if(status=="content_failed")R.string.content_unavailable_detail else R.string.bluetooth_connection_failed),color=MaterialTheme.colorScheme.error)
    if(state==null&&status!="connecting"){
        Text(stringResource(modeLabel(mode)),style=MaterialTheme.typography.titleLarge)
        BrawlButton(stringResource(R.string.bluetooth_host),{try{session.host(mode);external.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300))}catch(_:Exception){session.leave();accessError=true}},Modifier.fillMaxWidth(),enabled=available)
        Text(stringResource(R.string.bluetooth_pair_help),style=MaterialTheme.typography.bodySmall)
        BrawlButton(stringResource(R.string.bluetooth_pair),{try{external.launch(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))}catch(_:Exception){accessError=true}},Modifier.fillMaxWidth())
        TextButton({refresh()}){Text(stringResource(R.string.bluetooth_refresh))}
        devices.forEach{device->OutlinedButton({session.join(device.address)},Modifier.fillMaxWidth(),enabled=available){Text(device.name,maxLines=2)}}
        if(available&&devices.isEmpty())Text(stringResource(R.string.bluetooth_no_devices))
    }
    if(status=="connecting"){LinearProgressIndicator(Modifier.fillMaxWidth());Text(stringResource(R.string.bluetooth_connecting))}
    state?.let{room->
        Text(stringResource(modeLabel(room.mode)),style=MaterialTheme.typography.titleLarge)
        if(room.phase=="lobby"){
            Text(stringResource(R.string.bluetooth_lobby,room.players.size,room.mode.capacity,room.mode.minimum))
            room.players.forEach{player->Text(player.name+if(room.mode in setOf(NearbyMode.DUO,NearbyMode.SQUAD))" · "+stringResource(R.string.bluetooth_team,player.team+1)else "")}
            if(session.isHost)BrawlButton(stringResource(R.string.start_game),{session.start(locale)},Modifier.fillMaxWidth(),enabled=room.players.size>=room.mode.minimum&&room.players.all{it.ready})
            else{val me=room.players.find{it.id==session.myId};BrawlButton(stringResource(if(me?.ready==true)R.string.bluetooth_not_ready else R.string.ready),{session.ready(me?.ready!=true)},Modifier.fillMaxWidth())}
            Text(stringResource(R.string.bluetooth_ready_count,room.players.count{it.ready},room.players.size))
        }
        if(room.phase in setOf("question","reveal")){
            Text(stringResource(R.string.bluetooth_round,room.round,room.total,room.seconds),style=MaterialTheme.typography.labelLarge)
            BrawlPanel(Modifier.fillMaxWidth()){
                Text(room.prompt,style=MaterialTheme.typography.titleLarge)
                room.options.forEach{option->OutlinedButton({session.answer(option.id)},Modifier.fillMaxWidth(),enabled=room.phase=="question"&&room.players.find{it.id==session.myId}?.answered==false){Text(option.label)}}
                if(room.phase=="reveal")Text(stringResource(R.string.bluetooth_answer,room.options.find{it.id==room.correct}?.label.orEmpty()))
                else if(room.players.find{it.id==session.myId}?.answered==true)Text(stringResource(R.string.bluetooth_answer_saved))
            }
        }
        if(room.phase=="finished")Text(stringResource(R.string.bluetooth_results),style=MaterialTheme.typography.headlineSmall)
        if(room.phase!="lobby"){
            if(room.mode in setOf(NearbyMode.DUO,NearbyMode.SQUAD))room.players.groupBy{it.team}.entries.sortedByDescending{it.value.sumOf{p->p.score}}.forEach{(team,players)->Text(stringResource(R.string.bluetooth_team,team+1)+" · "+players.sumOf{it.score})}
            room.players.sortedByDescending{it.score}.forEach{Text(it.name+" · "+it.score)}
        }
    }
    if(state!=null||status=="connecting")BrawlButton(stringResource(R.string.bluetooth_leave),{session.leave()},Modifier.fillMaxWidth(),tone=ActionTone.DESTRUCTIVE)
    BrawlButton(stringResource(R.string.back_to_modes),{session.leave();onExit()},Modifier.fillMaxWidth())
}
private fun modeLabel(mode:NearbyMode)=when(mode){NearbyMode.DUEL->R.string.duel;NearbyMode.DUO->R.string.duo;NearbyMode.SQUAD->R.string.squad;NearbyMode.SOLO->R.string.solo}
