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

/** Original scalable artwork; no illustrative player or economy data from the mockup. */
@Composable fun BrainMark(modifier:Modifier=Modifier){
    Canvas(modifier){
        val u=size.minDimension/100f
        translate((size.width-100*u)/2,(size.height-100*u)/2){
            drawCircle(Cyan.copy(alpha=.12f),49*u,Offset(50*u,50*u))
            val outline=Path().apply{
                moveTo(49*u,20*u);cubicTo(30*u,7*u,15*u,24*u,20*u,36*u)
                cubicTo(3*u,44*u,10*u,67*u,23*u,69*u)
                cubicTo(18*u,86*u,43*u,94*u,50*u,78*u)
                cubicTo(65*u,94*u,85*u,83*u,80*u,69*u)
                cubicTo(96*u,62*u,96*u,43*u,81*u,36*u)
                cubicTo(84*u,16*u,62*u,10*u,49*u,20*u);close()
            }
            drawPath(outline,Ink,style=Stroke(10*u,cap=StrokeCap.Round))
            drawPath(outline,Cyan,style=Stroke(6*u,cap=StrokeCap.Round))
            drawPath(outline,Brush.verticalGradient(listOf(Color(0xFFFF99E7),Color(0xFFBC55E8)),0f,90*u))
            val folds=Path().apply{
                moveTo(50*u,25*u);lineTo(50*u,68*u)
                moveTo(30*u,29*u);quadraticTo(24*u,44*u,38*u,44*u)
                moveTo(24*u,58*u);quadraticTo(34*u,49*u,39*u,59*u)
                moveTo(70*u,27*u);quadraticTo(77*u,44*u,64*u,43*u)
                moveTo(76*u,57*u);quadraticTo(62*u,51*u,63*u,63*u)
            }
            drawPath(folds,Color(0xFF71338F),style=Stroke(3.5f*u,cap=StrokeCap.Round))
            drawCircle(Ink,3*u,Offset(37*u,67*u));drawCircle(Ink,3*u,Offset(63*u,67*u))
            drawArc(Ink,10f,160f,false,Offset(43*u,67*u),Size(14*u,11*u),style=Stroke(2.5f*u))
        }
    }
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
        CompositionLocalProvider(LocalContentColor provides Color.White){NavigationSymbol(symbol)}
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
