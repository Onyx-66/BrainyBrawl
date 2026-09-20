package com.brainybrawl.app.feature.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.GameArtwork
import java.text.NumberFormat
import androidx.compose.ui.platform.LocalConfiguration

/** Merchandising drafts only. No product is sellable until Play/server verification is configured. */
enum class OfferCurrency(val label:Int,val art:String,val accent:Color,val amounts:List<Int>){
    GEMS(R.string.gems,"gem_icon",Color(0xFF48D8FF),listOf(80,250,550,1200,2600,5500)),
    COINS(R.string.store_coins,"coin_icon",Color(0xFFFFCD42),listOf(500,1500,3500,8000,18000,40000)),
    FLAMES(R.string.flames,"flame_icon",Color(0xFFFF7866),listOf(5,15,35,80,180,400))
}

@Composable fun CurrencyOffers(initialCurrency:OfferCurrency=OfferCurrency.GEMS,sectionRequest:Int=0){
    var selected by rememberSaveable(initialCurrency){mutableStateOf(initialCurrency)}
    LaunchedEffect(initialCurrency,sectionRequest){selected=initialCurrency}
    val locale=LocalConfiguration.current.locales[0]
    val number=remember(locale){NumberFormat.getIntegerInstance(locale)}
    val currency=stringResource(selected.label)
    Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF352877),Color(0xFF123A65))),RoundedCornerShape(24.dp)).padding(20.dp),verticalAlignment=Alignment.CenterVertically){
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)){
            Text(stringResource(R.string.store_stock_up),style=MaterialTheme.typography.headlineSmall,color=Color.White)
            Text(stringResource(R.string.store_subtitle),style=MaterialTheme.typography.bodyMedium,color=Color(0xFFD6E4FF))
        }
        GameArtwork("store_icon",Modifier.size(76.dp))
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
        OfferCurrency.entries.forEach{kind->
            val active=selected==kind
            Surface(onClick={selected=kind},modifier=Modifier.weight(1f).testTag("currency-${kind.name}"),shape=RoundedCornerShape(18.dp),color=if(active)kind.accent else Color(0xFF152945),contentColor=if(active)Color(0xFF071A32) else Color.White){
                Column(Modifier.padding(vertical=10.dp,horizontal=4.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)){
                    GameArtwork(kind.art,Modifier.size(32.dp))
                    Text(stringResource(kind.label),style=MaterialTheme.typography.labelLarge,maxLines=1)
                }
            }
        }
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
        Text(currency,style=MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.store_six_packs),style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
    selected.amounts.chunked(2).forEachIndexed{row,amounts->
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
            amounts.forEachIndexed{column,amount->
                val index=row*2+column
                Column(Modifier.weight(1f).fillMaxHeight().testTag("offer-${selected.name}-$index")
                    .background(Brush.verticalGradient(listOf(selected.accent.copy(alpha=.22f),Color(0xFF10233F))),RoundedCornerShape(22.dp))
                    .border(1.dp,selected.accent.copy(alpha=.55f),RoundedCornerShape(22.dp)).padding(14.dp),
                    horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){
                    Text(stringResource(listOf(R.string.store_pocket,R.string.store_pouch,R.string.store_bundle,R.string.store_chest,R.string.store_vault,R.string.store_treasury)[index]),style=MaterialTheme.typography.labelLarge,color=Color(0xFFE0ECFF))
                    GameArtwork(selected.art,Modifier.size(64.dp))
                    Text(number.format(amount),style=MaterialTheme.typography.headlineMedium,color=Color.White)
                    Text(currency,style=MaterialTheme.typography.labelMedium,color=selected.accent)
                    Button(onClick={},enabled=false,modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(disabledContainerColor=Color(0xFF2A3E58),disabledContentColor=Color(0xFFCDDAEB))){
                        Text(stringResource(R.string.store_coming_soon),style=MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
    Text(stringResource(R.string.store_purchase_pending),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
}
