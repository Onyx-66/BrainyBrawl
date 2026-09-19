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
    PlayerCard(avatars,user.id,user.username,1,local=true)
    AccountDetailsCard(user.email,listOf("email"))
    ProfileStatPair(stringResource(R.string.player_level_label) to "1",stringResource(R.string.practice_best) to stats.read().best.toString())
    BrawlPanel(Modifier.fillMaxWidth()){Text(stringResource(R.string.account_sync_info))}
    ActionBento(listOf(
        BentoAction(stringResource(R.string.friends),NavSymbol.PROFILE,friends),
        BentoAction(stringResource(R.string.connect_online),NavSymbol.GAMES,connect),
        BentoAction(stringResource(R.string.change_password),NavSymbol.SETTINGS,password),
        BentoAction(stringResource(R.string.sign_out),NavSymbol.HOME,logout)))
}
