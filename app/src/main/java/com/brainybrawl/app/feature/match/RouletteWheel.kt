package com.brainybrawl.app.feature.match

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.ui.theme.*
import kotlin.math.*

/** Displays an already-authoritative twenty-slot roll; it does not generate outcomes. */
@Composable fun RouletteWheel(value:Int){
    require(value in 1..20)
    val description=namedString(R.string.roulette_value,"value" to value)
    val ink=MaterialTheme.colorScheme.onSurface
    val paint=remember{Paint(Paint.ANTI_ALIAS_FLAG).apply{textAlign=Paint.Align.CENTER;isFakeBoldText=true}}
    Text(description,style=MaterialTheme.typography.titleLarge)
    Canvas(Modifier.fillMaxWidth().height(260.dp).semantics{contentDescription=description}){
        val diameter=min(size.width,size.height)*.92f
        val radius=diameter/2
        val origin=center-Offset(radius,radius)
        for(slot in 1..20){
            val start=-90f+(slot-1)*18f
            drawArc(if(slot==value)Gold else if(slot%2==0)Purple else PanelRaised,start+.6f,16.8f,true,origin,Size(diameter,diameter))
            val angle=(start+9)*PI/180
            paint.color=(if(slot==value)Ink else ink).toArgb();paint.textSize=12.dp.toPx()
            val point=center+Offset(cos(angle).toFloat()*radius*.8f,sin(angle).toFloat()*radius*.8f)
            drawContext.canvas.nativeCanvas.drawText(slot.toString(),point.x,point.y-(paint.ascent()+paint.descent())/2,paint)
        }
        drawCircle(Navy,radius*.35f)
        paint.color=Gold.toArgb();paint.textSize=32.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(value.toString(),center.x,center.y-(paint.ascent()+paint.descent())/2,paint)
    }
}
