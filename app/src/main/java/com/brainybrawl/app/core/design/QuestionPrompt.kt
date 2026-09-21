package com.brainybrawl.app.core.design
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable fun QuestionPrompt(topic:String,prompt:String,detail:String="",modifier:Modifier=Modifier){
 Column(modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF233C70),Color(0xFF392667))),RoundedCornerShape(24.dp)).border(1.dp,Color(0xFF6E8BC8),RoundedCornerShape(24.dp)).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
   GameArtwork("start_game_icon",Modifier.size(42.dp))
   Text(topic,style=MaterialTheme.typography.labelLarge,color=Color(0xFF9BEAFF))
  }
  Text(prompt,style=MaterialTheme.typography.headlineSmall,color=Color.White)
  if(detail.isNotBlank())Text(detail,style=MaterialTheme.typography.bodyMedium,color=Color(0xFFD4DEFF))
 }
}
