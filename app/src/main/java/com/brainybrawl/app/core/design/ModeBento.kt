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
                    ModeGlyph(index,accent)
                    Text(stringResource(when(mode){OnlineMode.DUEL->R.string.duel;OnlineMode.DUO->R.string.duo;OnlineMode.SQUAD->R.string.squad;OnlineMode.SOLO->R.string.solo}),style=MaterialTheme.typography.titleLarge,color=Color.White)
                    Text(stringResource(when(mode){OnlineMode.DUEL->R.string.duel_description;OnlineMode.DUO->R.string.duo_description;OnlineMode.SQUAD->R.string.squad_description;OnlineMode.SOLO->R.string.solo_description}),style=MaterialTheme.typography.labelMedium,color=Color(0xFFBBD0EA))
                }
            }
        }
    }
}
@Composable private fun ModeGlyph(index:Int,color:Color){
    Canvas(Modifier.size(58.dp)){
        val u=size.width/60f
        fun pt(x:Float,y:Float)=Offset(x*u,y*u)
        fun person(x:Float,y:Float){drawCircle(color,5*u,pt(x,y));drawArc(color,180f,180f,false,pt(x-9,y+7),androidx.compose.ui.geometry.Size(18*u,18*u),style=Stroke(4*u))}
        when(index){
            0->{
                fun sword(mirror:Boolean){val p=Path().apply{moveTo(14*u,10*u);lineTo(21*u,12*u);lineTo(44*u,39*u);lineTo(39*u,44*u);lineTo(12*u,21*u);close()};if(mirror){drawContext.canvas.save();drawContext.canvas.scale(-1f,1f,30*u,30*u)};drawPath(p,color);drawLine(color,pt(32f,46f),pt(47f,32f),4*u);drawLine(color,pt(41f,41f),pt(51f,51f),5*u);if(mirror)drawContext.canvas.restore()};sword(false);sword(true)
            }
            1->{person(19f,19f);person(41f,19f)}
            2->{person(17f,10f);person(43f,10f);person(17f,36f);person(43f,36f)}
            else->{drawCircle(color.copy(alpha=.18f),28*u);person(30f,19f);drawCircle(color,27*u,style=Stroke(2*u))}
        }
    }
}
