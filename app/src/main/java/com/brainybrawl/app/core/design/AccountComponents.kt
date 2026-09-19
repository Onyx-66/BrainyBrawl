package com.brainybrawl.app.core.design
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R

data class BentoAction(val label:String,val symbol:NavSymbol,val click:()->Unit)
@Composable fun ActionBento(actions:List<BentoAction>){
 actions.chunked(2).forEach{row->Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
  row.forEach{action->Column(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(22.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable(onClick=action.click).padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
   NavigationSymbol(action.symbol);Text(action.label,style=MaterialTheme.typography.titleSmall)
  }}
  if(row.size==1)Spacer(Modifier.weight(1f))
 }}
}
@Composable fun OnlineFeatureGate(deviceAccount:Boolean,connect:()->Unit){
 BrawlPanel(Modifier.fillMaxWidth()){
  Text(stringResource(if(deviceAccount)R.string.device_signed_in else R.string.online_account_required),style=MaterialTheme.typography.titleLarge)
  Text(stringResource(R.string.online_feature_explanation))
  BrawlButton(stringResource(R.string.connect_online),connect,Modifier.fillMaxWidth())
 }
}
@Composable fun LanguageDropdown(current:String,onSelect:(String)->Unit){
 var open by remember{mutableStateOf(false)}
 val languages=listOf(Triple("en","🇬🇧",R.string.language_english),Triple("fr","🇫🇷",R.string.language_french),Triple("ar","🇸🇦",R.string.language_arabic))
 val selected=languages.single{it.first==current}
 Box(Modifier.fillMaxWidth()){
  OutlinedButton({open=true},Modifier.fillMaxWidth().heightIn(min=56.dp),shape=RoundedCornerShape(16.dp)){
   Text(selected.second+"  "+stringResource(selected.third),Modifier.weight(1f));Text("▾")
  }
  DropdownMenu(open,{open=false}){languages.forEach{(code,flag,label)->DropdownMenuItem(text={Text(flag+"  "+stringResource(label))},onClick={open=false;onSelect(code)})}}
 }
}
