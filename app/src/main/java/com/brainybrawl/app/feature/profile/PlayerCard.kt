package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.ui.theme.Cyan
import com.brainybrawl.app.ui.theme.Gold
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import kotlinx.coroutines.*

@Composable fun PlayerCard(appearance:AppearanceRepository,id:String,username:String,level:Long,number:Long?=null,
    currencies:List<Balance> = emptyList(),friends:Int=0,openFriends:()->Unit){
    val shape=RoundedCornerShape(28.dp)
    var picker by remember{mutableStateOf<String?>(null)}
    var notice by remember{mutableStateOf<Int?>(null)}
    var noticeVersion by remember{mutableIntStateOf(0)}
    LaunchedEffect(noticeVersion){if(notice!=null){delay(3_000);notice=null}}
    Column(Modifier.fillMaxWidth().clip(shape).background(Brush.linearGradient(listOf(Color(0xFF51339C),Color(0xFF142D50))))
        .border(1.dp,Cyan.copy(alpha=.55f),shape).padding(18.dp).testTag("player-card"),verticalArrangement=Arrangement.spacedBy(14.dp)){
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)){
            ProfileAvatar(appearance,id,username,Modifier.size(92.dp).testTag("player-avatar"))
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)){
                Text(username,style=MaterialTheme.typography.headlineSmall,color=Color.White)
                PlayerIdRow(number)
            }
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(6.dp)){
            val locale=androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
            listOf("level","gold","gems","flames").forEach{key->
                val value=if(key=="level")level else currencies.find{it.currency==key}?.balance?:0
                val label=stringResource(when(key){"level"->R.string.player_level_label;"gold"->R.string.gold;"gems"->R.string.gems;else->R.string.flames})
                val formatted=java.text.NumberFormat.getIntegerInstance(locale).format(value)
                Row(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(12.dp)).background(Color(0x55101935)).padding(horizontal=5.dp,vertical=10.dp)
                    .semantics(mergeDescendants=true){contentDescription="$label $formatted"}.testTag("card-$key"),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(5.dp)){
                    if(key=="level")CompositionLocalProvider(LocalContentColor provides Gold){NavigationSymbol(NavSymbol.LEVEL,Modifier.size(20.dp))}
                    else GameArtwork(when(key){"gold"->"coin_icon";"gems"->"gem_icon";else->"flame_icon"},Modifier.size(24.dp))
                    Text(if(value>=10_000)android.icu.text.CompactDecimalFormat.getInstance(locale,android.icu.text.CompactDecimalFormat.CompactStyle.SHORT).format(value)else formatted,style=MaterialTheme.typography.labelLarge,color=Color.White,maxLines=1,modifier=Modifier.weight(1f))
                }
            }
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            listOf("avatar" to R.string.avatars,"frame" to R.string.frames).forEach{(kind,label)->
                FilledTonalButton({picker=kind},Modifier.weight(1f).fillMaxHeight().heightIn(min=48.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.filledTonalButtonColors(containerColor=Color(0xFF6650DC),contentColor=Color.White)){
                    NavigationSymbol(if(kind=="avatar")NavSymbol.AVATARS else NavSymbol.FRAMES);Spacer(Modifier.width(8.dp));Text(stringResource(label),maxLines=1,softWrap=false,style=MaterialTheme.typography.labelLarge)
                }
            }
        }
        FilledTonalButton(openFriends,Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.filledTonalButtonColors(containerColor=Color(0xFF163E62),contentColor=Color.White)){
            NavigationSymbol(NavSymbol.FRIENDS);Spacer(Modifier.width(10.dp));Text(stringResource(R.string.friends),Modifier.weight(1f));Text(friends.toString(),color=Cyan)
        }
        notice?.let{Text(stringResource(it),color=Color.White,style=MaterialTheme.typography.bodySmall,modifier=Modifier.testTag("appearance-notice"))}
    }
    picker?.let{kind->AppearancePicker(appearance,id,kind,{picker=null}){synced->picker=null;notice=if(synced)R.string.appearance_synced else R.string.appearance_saved;noticeVersion++}}
}

@Composable private fun AppearancePicker(repository:AppearanceRepository,user:String,kind:String,dismiss:()->Unit,saved:(Boolean)->Unit){
    val context=androidx.compose.ui.platform.LocalContext.current
    val slots=remember(kind){context.assets.list("assets/${kind}s").orEmpty().mapNotNull{Regex("${kind}_([0-9]{2})\\.png").matchEntire(it)?.groupValues?.get(1)?.toIntOrNull()}.filter{it in 1..if(kind=="avatar")12 else 20}.sorted()}
    val version by repository.revision.collectAsStateWithLifecycle()
    val original=remember(user,version){repository.read(user)}
    var selection by remember(kind){mutableStateOf(original)}
    var busy by remember{mutableStateOf(false)};var failed by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    AlertDialog(containerColor=MaterialTheme.colorScheme.surface,onDismissRequest={if(!busy)dismiss()},title={Text(stringResource(if(kind=="avatar")R.string.avatars else R.string.frames))},
        text={Column(Modifier.heightIn(max=460.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
            slots.chunked(3).forEach{row->Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                row.forEach{index->
                    val candidate=if(kind=="avatar")selection.copy(avatar=index)else selection.copy(frame=index)
                    val active=if(kind=="avatar")selection.avatar==index else selection.frame==index
                    val label=namedString(if(kind=="avatar")R.string.avatar_number else R.string.frame_number,"number" to index)
                    Column(Modifier.weight(1f).border(if(active)2.dp else 1.dp,if(active)Gold else MaterialTheme.colorScheme.outline,RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp)).clickable(enabled=!busy){selection=candidate}.padding(5.dp).testTag("$kind-$index").semantics{selected=active;contentDescription=label},horizontalAlignment=Alignment.CenterHorizontally){
                        AppearancePreview(candidate,Modifier.fillMaxWidth());Text(index.toString(),style=MaterialTheme.typography.labelSmall)
                    }
                }
                repeat(3-row.size){Spacer(Modifier.weight(1f))}
            }}
            if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
            if(failed)Text(stringResource(R.string.request_failed),color=MaterialTheme.colorScheme.error)
        }},
        confirmButton={TextButton({busy=true;scope.launch{try{saved(repository.select(user,selection))}catch(e:CancellationException){throw e}catch(_:Exception){failed=true}finally{busy=false}}},enabled=!busy){Text(stringResource(R.string.save))}},
        dismissButton={TextButton(dismiss,enabled=!busy){Text(stringResource(R.string.cancel))}})
}

@Composable fun ProfileStatPair(first:Pair<String,String>,second:Pair<String,String>){
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
        listOf(first,second).forEach{(label,value)->BrawlPanel(Modifier.weight(1f).fillMaxHeight()){
            Text(value,style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.secondary)
            Text(label,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }}
    }
}
