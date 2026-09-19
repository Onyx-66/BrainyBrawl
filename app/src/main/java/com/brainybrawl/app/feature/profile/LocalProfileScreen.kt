package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.feature.auth.LocalAccounts
import com.brainybrawl.app.feature.offline.OfflineStatistics

@Composable fun LocalProfileScreen(accounts:LocalAccounts,stats:OfflineStatistics,avatars:AvatarRepository,connect:()->Unit,friends:()->Unit,password:()->Unit,logout:()->Unit){
    val state by accounts.state.collectAsStateWithLifecycle()
    val user=state.current?:return
    Text(stringResource(R.string.profile),style=MaterialTheme.typography.headlineMedium)
    BrawlPanel(Modifier.fillMaxWidth()){
        Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)){
            ProfileAvatar(avatars,user.id,user.username,Modifier.size(76.dp))
            Column{Text(user.username,style=MaterialTheme.typography.headlineSmall);Text(stringResource(R.string.device_account),style=MaterialTheme.typography.labelMedium)}
        }
        PhotoPicker(avatars,true)
    }
    BrawlPanel(Modifier.fillMaxWidth()){
        Text(user.email)
        Text(stringResource(R.string.local_profile_description))
        Text(namedString(R.string.player_level,"level" to 1))
        Text(namedString(R.string.offline_best,"score" to stats.read().best))
    }
    ActionBento(listOf(
        BentoAction(stringResource(R.string.friends),NavSymbol.PROFILE,friends),
        BentoAction(stringResource(R.string.connect_online),NavSymbol.GAMES,connect),
        BentoAction(stringResource(R.string.change_password),NavSymbol.SETTINGS,password),
        BentoAction(stringResource(R.string.sign_out),NavSymbol.HOME,logout)))
}
