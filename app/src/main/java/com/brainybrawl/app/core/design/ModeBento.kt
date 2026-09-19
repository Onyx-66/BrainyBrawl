package com.brainybrawl.app.core.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.feature.lobby.OnlineMode

@Composable fun ModeBento(selected:OnlineMode?,onSelect:(OnlineMode)->Unit){
    val modes=OnlineMode.entries.toList()
    modes.chunked(2).forEachIndexed{row,pair->
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
            pair.forEachIndexed{column,mode->
                val index=row*2+column
                val accent=listOf(Color(0xFF9D83FF),Color(0xFFEE8BFA),Color(0xFF55D6FF),Color(0xFFFFCB68))[index]
                val shape=RoundedCornerShape(24.dp)
                Column(Modifier.weight(if(row==0&&column==0)1.15f else 1f).fillMaxHeight().clip(shape)
                    .background(Brush.linearGradient(listOf(accent.copy(alpha=.24f),Color(0xFF10213E))))
                    .border(if(selected==mode)2.dp else 1.dp,accent.copy(alpha=if(selected==mode)1f else .3f),shape)
                    .clickable{onSelect(mode)}.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                    ModeGlyph(index)
                    Text(stringResource(when(mode){OnlineMode.DUEL->R.string.duel;OnlineMode.DUO->R.string.duo;OnlineMode.SQUAD->R.string.squad;OnlineMode.SOLO->R.string.solo}),style=MaterialTheme.typography.titleLarge,color=Color.White)
                    Text(stringResource(when(mode){OnlineMode.DUEL->R.string.duel_description;OnlineMode.DUO->R.string.duo_description;OnlineMode.SQUAD->R.string.squad_description;OnlineMode.SOLO->R.string.solo_description}),style=MaterialTheme.typography.labelMedium,color=Color(0xFFBBD0EA))
                }
            }
        }
    }
}
@Composable private fun ModeGlyph(index:Int){
    val resource=listOf(R.drawable.bb_badge_duel,R.drawable.bb_badge_duo,R.drawable.bb_badge_squad,R.drawable.bb_badge_solo)[index]
    androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(resource),null,Modifier.size(68.dp))
}
