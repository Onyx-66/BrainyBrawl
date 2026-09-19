package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.ui.theme.*

@Composable fun PlayerCard(avatars:AvatarRepository,id:String,username:String,level:Long,number:Long?=null,local:Boolean=false){
    val shape=RoundedCornerShape(28.dp)
    Column(Modifier.fillMaxWidth().clip(shape).background(Brush.linearGradient(listOf(Color(0xFF51339C),Color(0xFF142D50))))
        .border(1.dp,Cyan.copy(alpha=.55f),shape).padding(20.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
        Text(stringResource(R.string.player_card),style=MaterialTheme.typography.labelLarge,color=Color(0xFFD4C7FF))
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)){
            Box(Modifier.clip(RoundedCornerShape(24.dp)).border(3.dp,Gold,RoundedCornerShape(24.dp)).padding(6.dp)){
                ProfileAvatar(avatars,id,username,Modifier.size(74.dp))
            }
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)){
                Text(username,style=MaterialTheme.typography.headlineSmall,color=Color.White)
                Text(namedString(R.string.player_level,"level" to level),style=MaterialTheme.typography.titleSmall,color=Gold)
                number?.let{Text(namedString(R.string.player_number,"number" to it),style=MaterialTheme.typography.labelMedium,color=Color(0xFFC8D9EF))}
            }
        }
        PhotoPicker(avatars,local)
    }
}
@Composable fun AccountDetailsCard(email:String?,providers:List<String>){
    BrawlPanel(Modifier.fillMaxWidth()){
        Text(stringResource(R.string.account_details),style=MaterialTheme.typography.titleLarge)
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
            Text(stringResource(R.string.account_email_label),style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text(email?:stringResource(R.string.no_account_email),style=MaterialTheme.typography.bodyLarge)
        }
        Text(stringResource(R.string.signin_methods),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
        androidx.compose.foundation.layout.FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            providers.distinct().forEach{provider->
                Surface(shape=RoundedCornerShape(12.dp),color=MaterialTheme.colorScheme.surfaceVariant){
                    Row(Modifier.padding(horizontal=12.dp,vertical=9.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        if(provider=="google"||provider=="discord")ProviderMark(if(provider=="google")com.brainybrawl.app.feature.auth.AuthProvider.GOOGLE else com.brainybrawl.app.feature.auth.AuthProvider.DISCORD)
                        else NavigationSymbol(NavSymbol.PROFILE)
                        Text(stringResource(when(provider){"google"->R.string.provider_google;"discord"->R.string.provider_discord;else->R.string.email}),style=MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
@Composable fun ProfileStatPair(first:Pair<String,String>,second:Pair<String,String>){
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
        listOf(first,second).forEach{(label,value)->
            BrawlPanel(Modifier.weight(1f).fillMaxHeight()){
                Text(value,style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.secondary)
                Text(label,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
