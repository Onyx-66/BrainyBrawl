package com.brainybrawl.app.core.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

data class GameNavItem(val key:String,val title:String,val art:String)
@Composable fun GameNavigationBar(items:List<GameNavItem>,selectedKey:String,select:(String)->Unit){
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart=28.dp,topEnd=28.dp))
        .background(MaterialTheme.colorScheme.surface).navigationBarsPadding().padding(horizontal=10.dp,vertical=6.dp),
        verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
        items.forEach{item->
            val active=item.key==selectedKey
            val shape=RoundedCornerShape(22.dp)
            val decoration=if(active)Modifier.background(Brush.verticalGradient(listOf(Color(0xFF22D8FF),Color(0xFF087DF1),Color(0xFF1645B5))),shape)
                .border(2.dp,Color(0xFF68EAFF),shape)else Modifier
            Column(Modifier.weight(1f).height(72.dp).clip(shape).then(decoration)
                .selectable(active,role=Role.Tab){select(item.key)}.testTag("nav-${item.key}")
                .semantics(mergeDescendants=true){if(!active)text=AnnotatedString(item.title)},
                horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
                GameArtwork(item.art,Modifier.size(if(active)42.dp else 38.dp))
                if(active)Text(item.title,Modifier.testTag("nav-label-${item.key}"),style=MaterialTheme.typography.labelLarge,color=Color.White,maxLines=1)
            }
        }
    }
}
