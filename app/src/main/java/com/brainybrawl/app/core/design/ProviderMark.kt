package com.brainybrawl.app.core.design
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.feature.auth.AuthProvider

@Composable fun ProviderMark(provider:AuthProvider){
    Canvas(Modifier.size(24.dp)){
        val s=size.width/24f
        scale(s,s,pivot=Offset.Zero){
            if(provider==AuthProvider.GOOGLE){
                val stroke=Stroke(4f)
                drawArc(Color(0xFF4285F4),0f,82f,false,Offset(3f,3f),Size(18f,18f),style=stroke)
                drawArc(Color(0xFF34A853),82f,66f,false,Offset(3f,3f),Size(18f,18f),style=stroke)
                drawArc(Color(0xFFFBBC05),148f,66f,false,Offset(3f,3f),Size(18f,18f),style=stroke)
                drawArc(Color(0xFFEA4335),214f,103f,false,Offset(3f,3f),Size(18f,18f),style=stroke)
                drawLine(Color(0xFF4285F4),Offset(12f,12f),Offset(22f,12f),4f)
            }else{
                val face=Path().apply{moveTo(5f,5f);quadraticTo(12f,2f,19f,5f);quadraticTo(22f,10f,23f,18f);lineTo(17f,21f);lineTo(15f,18f);quadraticTo(12f,19f,9f,18f);lineTo(7f,21f);lineTo(1f,18f);quadraticTo(2f,10f,5f,5f);close()}
                drawPath(face,Color(0xFF5865F2))
                drawOval(Color.White,Offset(7f,10f),Size(3f,4f));drawOval(Color.White,Offset(14f,10f),Size(3f,4f))
            }
        }
    }
}
