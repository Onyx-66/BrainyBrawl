package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.feature.auth.AuthValidation
import com.brainybrawl.app.ui.theme.*

@Composable
fun CurrencyBar(snapshot: ProfileSnapshot) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        listOf("gold" to R.string.gold,"gems" to R.string.gems,"flames" to R.string.flames).forEach { (key,label) ->
            snapshot.currencies.find { it.currency==key }?.let {
                ResourceChip(stringResource(label),java.text.NumberFormat.getIntegerInstance(androidx.compose.ui.platform.LocalConfiguration.current.locales[0]).format(it.balance),
                    when(key) { "gold" -> Gold; "gems" -> Cyan; else -> Gold },icon=when(key){"gold"->R.drawable.bb_currency_gold;"gems"->R.drawable.bb_currency_gems;else->R.drawable.bb_currency_flames})
            }
        }
    }
}
@Composable
fun ProfileScreen(model: PlayerViewModel, container:com.brainybrawl.app.core.AppContainer,authModel:com.brainybrawl.app.feature.auth.AuthViewModel,friends:()->Unit,password: () -> Unit, logout: () -> Unit) {
    val state by model.profile.collectAsStateWithLifecycle()
    val ui by model.social.collectAsStateWithLifecycle()
    val identity by container.auth.state.collectAsStateWithLifecycle()
    Text(stringResource(R.string.profile),modifier=androidx.compose.ui.Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Start,style=MaterialTheme.typography.headlineMedium)
    when(val current=state) {
        PlayerDataState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
        PlayerDataState.Failed -> FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.network_error),stringResource(R.string.retry),model::refresh)
        PlayerDataState.SignedOut -> Text(stringResource(R.string.sign_in_required))
        is PlayerDataState.Ready -> {
            val profile=current.data.profile
            var username by rememberSaveable(profile.username) { mutableStateOf(profile.username) }
            PlayerCard(container.appearance,profile.id,profile.username,current.data.level,profile.number,current.data.currencies,ui.snapshot.friends.count{it.status=="accepted"},friends)
            (identity as? com.brainybrawl.app.feature.auth.AuthState.SignedIn)?.let{AccountDetailsCard(container,it,authModel,current.data.email,current.data.providers,password,logout)}
            current.data.modeStats.forEach{stats->BrawlPanel(Modifier.fillMaxWidth()){
                Text(stringResource(when(stats.mode){"duo"->R.string.duo;"squad"->R.string.squad;"solo"->R.string.solo;else->R.string.duel}),style=MaterialTheme.typography.titleMedium)
                Text(namedString(R.string.games_played,"count" to stats.played))
                Text(namedString(R.string.games_won,"count" to stats.wins))
                Text(namedString(R.string.mode_best,"score" to stats.best))
            }}
            ProfileStatPair(stringResource(R.string.played_label) to current.data.played.toString(),stringResource(R.string.wins_label) to current.data.wins.toString())
            var editing by rememberSaveable{mutableStateOf(false)}
            BrawlPanel(Modifier.fillMaxWidth()){
                TextButton({editing=!editing}){Text(stringResource(R.string.edit_profile))}
                if(editing){
                    OutlinedTextField(username,{username=it},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.username))},singleLine=true)
                    BrawlButton(stringResource(R.string.save),{model.rename(username);editing=false},Modifier.fillMaxWidth(),enabled=!ui.busy&&AuthValidation.username(username))
                }
            }
            current.data.inventory.filter{it.kind=="boost"}.takeIf{it.isNotEmpty()}?.let{items->
                Text(stringResource(R.string.inventory),style=MaterialTheme.typography.titleLarge)
                items.forEach{Text(com.brainybrawl.app.core.localization.catalogLabel(it.labels,androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language)?:stringResource(R.string.content_unavailable))}
            }
            if(ui.failed) Text(stringResource(R.string.request_failed),color=MaterialTheme.colorScheme.error)
        }
    }

}
