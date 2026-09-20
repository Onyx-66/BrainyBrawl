package com.brainybrawl.app.core.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.ui.theme.*

/** Owner-supplied transparent branding; decorative when accompanied by a screen title. */
@Composable fun BrainMark(modifier:Modifier=Modifier){
    androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(R.drawable.brand_logo),
        contentDescription=null,modifier=modifier,contentScale=androidx.compose.ui.layout.ContentScale.Fit)
}

@Composable fun GameHero(onPlay:()->Unit){
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(
        Brush.linearGradient(listOf(Color(0xFF234EB8),Purple,Color(0xFF9A4BF1))))){
        Canvas(Modifier.matchParentSize()){
            drawCircle(Color.White.copy(alpha=.08f),size.width*.45f,Offset(size.width,size.height*.15f))
            drawCircle(Cyan.copy(alpha=.15f),size.width*.25f,Offset(size.width*.8f,size.height))
        }
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            Row(verticalAlignment=Alignment.CenterVertically){
                Column(Modifier.weight(1f)){
                    Text(stringResource(R.string.play_title),style=MaterialTheme.typography.headlineMedium,color=Color.White)
                    Text(stringResource(R.string.play_description),style=MaterialTheme.typography.bodyMedium,color=Color.White.copy(alpha=.9f))
                }
                BrainMark(Modifier.size(106.dp))
            }
            BrawlButton(stringResource(R.string.start_game),onPlay,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
        }
    }
}

@Composable fun GameTile(title:String,subtitle:String,symbol:NavSymbol,colors:List<Color>,modifier:Modifier=Modifier,onClick:()->Unit){
    Column(modifier.clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(colors)).clickable(onClick=onClick)
        .padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
        CompositionLocalProvider(LocalContentColor provides Color.White){NavigationSymbol(symbol,Modifier.align(Alignment.CenterHorizontally).size(72.dp))}
        Text(title,style=MaterialTheme.typography.titleMedium,color=Color.White,fontWeight=FontWeight.ExtraBold)
        Text(subtitle,style=MaterialTheme.typography.labelMedium,color=Color.White.copy(alpha=.9f))
    }
}

@Composable fun ModeBanner(title:String,subtitle:String,index:Int,modifier:Modifier=Modifier){
    val colors=when(index){0->listOf(Color(0xFF5341CE),Purple);1->listOf(Color(0xFF8C39CA),Purple);2->listOf(Color(0xFF067BBF),Cyan);else->listOf(Color(0xFF176181),Color(0xFF237CBA))}
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Brush.horizontalGradient(colors)).padding(14.dp),
        verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)){
        BrainMark(Modifier.size(64.dp))
        Column(Modifier.weight(1f)){
            Text(title,style=MaterialTheme.typography.titleLarge,color=Color.White)
            Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=Color.White.copy(alpha=.9f))
        }
        CompositionLocalProvider(LocalContentColor provides Color.White){NavigationSymbol(NavSymbol.GAMES)}
    }
}
