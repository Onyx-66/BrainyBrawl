package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.feature.auth.LocalAccounts
import com.brainybrawl.app.feature.offline.OfflineStatistics

@Composable fun LocalProfileScreen(accounts:LocalAccounts,stats:OfflineStatistics,connect:()->Unit,friends:()->Unit,password:()->Unit,logout:()->Unit){
    val state by accounts.state.collectAsStateWithLifecycle()
    val user=state.current?:return
    Text(stringResource(R.string.profile),style=MaterialTheme.typography.headlineMedium)
    PlayerCard(user.username,stringResource(R.string.device_account),user.username.take(1),Modifier.fillMaxWidth())
    BrawlPanel(Modifier.fillMaxWidth()){
        Text(user.email)
        Text(stringResource(R.string.local_profile_description))
        Text(namedString(R.string.player_level,"level" to 1))
        Text(namedString(R.string.offline_best,"score" to stats.read().best))
    }
    BrawlButton(stringResource(R.string.friends),friends,Modifier.fillMaxWidth())
    BrawlButton(stringResource(R.string.connect_online),connect,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
    BrawlButton(stringResource(R.string.change_password),password,Modifier.fillMaxWidth())
    BrawlButton(stringResource(R.string.sign_out),logout,Modifier.fillMaxWidth(),tone=ActionTone.DESTRUCTIVE)
}
