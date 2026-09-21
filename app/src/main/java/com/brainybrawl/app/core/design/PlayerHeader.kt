package com.brainybrawl.app.core.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.feature.profile.*
import com.brainybrawl.app.feature.store.OfferCurrency
import com.brainybrawl.app.core.localization.namedString

@Composable fun PlayerHeader(appearance:AppearanceRepository,user:String?,name:String,level:Long,balances:List<Balance>?,onProfile:()->Unit,onSettings:()->Unit,onCurrency:(OfferCurrency)->Unit){
    val profileLabel=stringResource(R.string.profile)
    val settingsLabel=stringResource(R.string.settings)
    Box(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=12.dp,vertical=6.dp)){
        Column(verticalArrangement=Arrangement.spacedBy(5.dp)){
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                Row(Modifier.testTag("header-profile").weight(1f).heightIn(min=56.dp).clip(RoundedCornerShape(16.dp)).background(Color(0x88102042)).clickable(onClick=onProfile).semantics{contentDescription=profileLabel}.padding(4.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    if(user!=null)ProfileAvatar(appearance,user,name,Modifier.size(44.dp))else GameArtwork("profile_icon",Modifier.size(44.dp))
                    Column(Modifier.weight(1f)){
                        Text(name,style=MaterialTheme.typography.titleSmall,color=Color.White,maxLines=1,overflow=TextOverflow.Ellipsis)
                        if(user!=null)Text(namedString(R.string.player_level,"level" to level),style=MaterialTheme.typography.labelSmall,color=Color(0xFFFFE25F),maxLines=1)
                    }
                }

                HeaderSettings(settingsLabel,onSettings)
            }
            if(user!=null)HeaderWallet(balances,onCurrency,Modifier.fillMaxWidth())

        }
    }
}
@Composable private fun HeaderSettings(label:String,action:()->Unit){
    Box(Modifier.testTag("header-settings").width(56.dp).fillMaxHeight().heightIn(min=56.dp).clip(RoundedCornerShape(13.dp)).background(Color(0x88102042)).clickable(onClick=action).semantics{contentDescription=label},contentAlignment=Alignment.Center){
        CompositionLocalProvider(LocalContentColor provides Color.White){NavigationSymbol(NavSymbol.SETTINGS,Modifier.size(28.dp))}
    }
}
@Composable private fun HeaderWallet(balances:List<Balance>?,onCurrency:(OfferCurrency)->Unit,modifier:Modifier){
    val locale=LocalConfiguration.current.locales[0]
    Row(modifier,horizontalArrangement=Arrangement.spacedBy(4.dp)){
        listOf(OfferCurrency.COINS,OfferCurrency.GEMS,OfferCurrency.FLAMES).forEach{currency->
            val key=when(currency){OfferCurrency.COINS->"gold";OfferCurrency.GEMS->"gems";OfferCurrency.FLAMES->"flames"}
            val prefix=when(currency){OfferCurrency.COINS->"coin";OfferCurrency.GEMS->"gem";OfferCurrency.FLAMES->"flame"}
            val value=balances?.firstOrNull{it.currency==key}?.balance
            val exact=value?.let{java.text.NumberFormat.getIntegerInstance(locale).format(it)}?:"—"
            val display=if(value!=null&&value>=1000)android.icu.text.CompactDecimalFormat.getInstance(locale,android.icu.text.CompactDecimalFormat.CompactStyle.SHORT).format(value)else exact
            val label=stringResource(currency.label)
            Box(Modifier.weight(1f).heightIn(min=48.dp).testTag("header-${currency.name}").clip(RoundedCornerShape(12.dp)).clickable{onCurrency(currency)}.semantics(mergeDescendants=true){contentDescription="$label $exact";role=Role.Button},contentAlignment=Alignment.Center){
                GameArtwork("currency_count",Modifier.fillMaxWidth().height(38.dp).background(Color(0xFFF2F3F7),RoundedCornerShape(14.dp)),androidx.compose.ui.layout.ContentScale.FillBounds)
                Row(Modifier.fillMaxWidth().padding(horizontal=2.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(2.dp)){
                    GameArtwork("${prefix}_icon",Modifier.size(28.dp))
                    Text(display,Modifier.weight(1f),style=MaterialTheme.typography.labelMedium,color=Color(0xFF17243A),maxLines=1)
                    GameArtwork("${prefix}_more",Modifier.size(30.dp))
                }
            }
        }
    }
}
