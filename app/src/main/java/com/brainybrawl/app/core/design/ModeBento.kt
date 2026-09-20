package com.brainybrawl.app.core.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.feature.lobby.OnlineMode

@Composable fun ModeBento(selected:OnlineMode?,onSelect:(OnlineMode)->Unit){
    OnlineMode.entries.toList().chunked(2).forEach{pair->
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            pair.forEach{mode->
                val art=when(mode){OnlineMode.DUEL->"1v1";OnlineMode.DUO->"duo";OnlineMode.SQUAD->"squad";OnlineMode.SOLO->"solo"}
                val title=stringResource(when(mode){OnlineMode.DUEL->R.string.duel;OnlineMode.DUO->R.string.duo;OnlineMode.SQUAD->R.string.squad;OnlineMode.SOLO->R.string.solo})
                val description=stringResource(when(mode){OnlineMode.DUEL->R.string.duel_description;OnlineMode.DUO->R.string.duo_description;OnlineMode.SQUAD->R.string.squad_description;OnlineMode.SOLO->R.string.solo_description})
                Box(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(26.dp))
                    .testTag("mode-${mode.name}").clickable{onSelect(mode)}){
                    GameArtwork("${art}_card",Modifier.matchParentSize(),ContentScale.FillBounds)
                    Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(4.dp)){
                        GameArtwork("${art}_icon",Modifier.fillMaxWidth().height(106.dp).testTag("mode-art-$art"))
                        Row(verticalAlignment=Alignment.CenterVertically){
                            Text(title,Modifier.weight(1f),style=MaterialTheme.typography.titleLarge.copy(shadow=Shadow(Color(0xFF172051),Offset(0f,3f),4f)),color=Color.White)
                            Text("›",style=MaterialTheme.typography.headlineMedium,color=Color.White)
                        }
                        Text(description,style=MaterialTheme.typography.labelMedium.copy(shadow=Shadow(Color(0xFF172051),Offset(0f,1f),5f)),color=Color.White)
                    }
                    if(selected==mode)Box(Modifier.matchParentSize().border(3.dp,Color.White,RoundedCornerShape(26.dp)))
                }
            }
        }
    }
}
@Composable fun OfflineModeCard(onClick:()->Unit){
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).testTag("mode-OFFLINE").clickable(onClick=onClick)){
        GameArtwork("offline_card",Modifier.matchParentSize(),ContentScale.FillBounds)
        Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)){
            GameArtwork("offline_icon",Modifier.size(72.dp))
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)){
                Text(stringResource(R.string.offline),style=MaterialTheme.typography.titleLarge,color=Color.White)
                Text(stringResource(R.string.offline_modes_description),style=MaterialTheme.typography.labelMedium,color=Color.White)
            }
            Text("›",style=MaterialTheme.typography.headlineMedium,color=Color.White)
        }
    }
}
