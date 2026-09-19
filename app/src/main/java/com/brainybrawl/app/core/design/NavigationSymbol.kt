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

enum class NavSymbol { HOME,GAMES,STORE,PROFILE,TROPHY }
@Composable fun NavigationSymbol(symbol:NavSymbol){
    val color=LocalContentColor.current
    Canvas(Modifier.size(24.dp)){
        val scale=size.width/24
        fun point(x:Float,y:Float)=Offset(x*scale,y*scale)
        val stroke=Stroke(2*scale)
        when(symbol){
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
