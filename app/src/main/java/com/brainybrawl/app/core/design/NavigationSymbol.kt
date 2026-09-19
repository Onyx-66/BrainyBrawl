package com.brainybrawl.app.core.design
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

enum class NavSymbol { HOME,GAMES,IMAGE,STORE,PROFILE,TROPHY,SETTINGS,PUZZLE,OFFLINE }
@Composable fun NavigationSymbol(symbol:NavSymbol){
    val color=LocalContentColor.current
    Canvas(Modifier.size(24.dp)){
        val scale=size.width/24
        fun point(x:Float,y:Float)=Offset(x*scale,y*scale)
        val stroke=Stroke(2*scale)
        when(symbol){
            NavSymbol.PUZZLE->{
                drawRoundRect(color,point(3f,3f),Size(18*scale,18*scale),androidx.compose.ui.geometry.CornerRadius(2*scale),style=stroke)
                drawLine(color,point(12f,3f),point(12f,21f),2*scale);drawLine(color,point(3f,12f),point(21f,12f),2*scale)
                drawCircle(color,2.5f*scale,point(12f,7f),style=stroke)
            }
            NavSymbol.OFFLINE->{
                drawRoundRect(color,point(6f,2f),Size(12*scale,20*scale),androidx.compose.ui.geometry.CornerRadius(2*scale),style=stroke)
                drawLine(color,point(3f,5f),point(21f,19f),2*scale);drawLine(color,point(10f,19f),point(14f,19f),2*scale)
            }
            NavSymbol.IMAGE->{
                drawRoundRect(color,point(2f,3f),Size(20*scale,18*scale),androidx.compose.ui.geometry.CornerRadius(2*scale),style=stroke)
                drawCircle(color,2*scale,point(8f,8f))
                val mountains=Path().apply{moveTo(3*scale,19*scale);lineTo(10*scale,12*scale);lineTo(14*scale,16*scale);lineTo(18*scale,10*scale);lineTo(22*scale,16*scale)}
                drawPath(mountains,color,style=stroke)
            }
            NavSymbol.SETTINGS->{
                drawCircle(color,7*scale,point(12f,12f),style=stroke)
                drawCircle(color,2.5f*scale,point(12f,12f),style=stroke)
                repeat(8){i->val angle=i*Math.PI/4;drawLine(color,point(12f+7*kotlin.math.cos(angle).toFloat(),12f+7*kotlin.math.sin(angle).toFloat()),point(12f+10*kotlin.math.cos(angle).toFloat(),12f+10*kotlin.math.sin(angle).toFloat()),3*scale)}
            }
            NavSymbol.HOME->{
                val path=Path().apply{moveTo(3*scale,11*scale);lineTo(12*scale,3*scale);lineTo(21*scale,11*scale);moveTo(5*scale,10*scale);lineTo(5*scale,21*scale);lineTo(19*scale,21*scale);lineTo(19*scale,10*scale)}
                drawPath(path,color,style=stroke);drawRect(color,point(10f,14f),Size(4*scale,7*scale),style=stroke)
            }
            NavSymbol.GAMES->{
                val path=Path().apply{moveTo(7*scale,6*scale);lineTo(17*scale,6*scale);cubicTo(22*scale,6*scale,24*scale,20*scale,20*scale,20*scale);lineTo(15*scale,16*scale);lineTo(9*scale,16*scale);cubicTo(0f,26*scale,0f,6*scale,7*scale,6*scale);close()}
                drawPath(path,color,style=stroke);drawLine(color,point(6f,11f),point(10f,11f),2*scale);drawLine(color,point(8f,9f),point(8f,13f),2*scale)
                drawCircle(color,scale,point(17f,10f));drawCircle(color,scale,point(19f,13f))
            }
            NavSymbol.TROPHY->{
                val path=Path().apply{moveTo(6*scale,3*scale);lineTo(18*scale,3*scale);lineTo(16*scale,13*scale);quadraticTo(12*scale,18*scale,8*scale,13*scale);close();moveTo(12*scale,15*scale);lineTo(12*scale,21*scale);moveTo(7*scale,21*scale);lineTo(17*scale,21*scale)}
                drawPath(path,color,style=stroke);drawArc(color,0f,180f,false,point(1f,4f),Size(8*scale,7*scale),style=stroke);drawArc(color,0f,180f,false,point(15f,4f),Size(8*scale,7*scale),style=stroke)
            }
            NavSymbol.STORE->{drawRoundRect(color,point(4f,8f),Size(16*scale,13*scale),androidx.compose.ui.geometry.CornerRadius(2*scale),style=stroke);drawArc(color,180f,180f,false,point(8f,2f),Size(8*scale,12*scale),style=stroke)}
            NavSymbol.PROFILE->{drawCircle(color,4*scale,point(12f,7f),style=stroke);drawArc(color,180f,180f,false,point(4f,14f),Size(16*scale,14*scale),style=stroke)}
        }
    }
}
