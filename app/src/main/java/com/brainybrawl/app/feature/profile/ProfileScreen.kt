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
fun ProfileScreen(model: PlayerViewModel, avatars:AvatarRepository,password: () -> Unit, logout: () -> Unit) {
    val state by model.profile.collectAsStateWithLifecycle()
    val ui by model.social.collectAsStateWithLifecycle()
    Text(stringResource(R.string.profile),style=MaterialTheme.typography.headlineMedium)
    when(val current=state) {
        PlayerDataState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
        PlayerDataState.Failed -> FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.network_error),stringResource(R.string.retry),model::refresh)
        PlayerDataState.SignedOut -> Text(stringResource(R.string.sign_in_required))
        is PlayerDataState.Ready -> {
            val profile=current.data.profile
            var username by rememberSaveable(profile.username) { mutableStateOf(profile.username) }
            BrawlPanel(Modifier.fillMaxWidth()){
                Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)){
                    ProfileAvatar(avatars,profile.id,profile.username,Modifier.size(76.dp))
                    Column{Text(profile.username,style=MaterialTheme.typography.headlineSmall);Text(namedString(R.string.player_level,"level" to current.data.level),color=Gold);Text(namedString(R.string.player_number,"number" to profile.number.toString()),style=MaterialTheme.typography.labelMedium)}
                }
                PhotoPicker(avatars,false)
            }
            CurrencyBar(current.data)
            BrawlPanel(Modifier.fillMaxWidth()) {
                current.data.email?.let{Text(namedString(R.string.account_email,"email" to it))}
                Text(stringResource(R.string.connected_accounts),style=MaterialTheme.typography.titleMedium)
                current.data.providers.forEach{provider->Text(stringResource(when(provider){"google"->R.string.provider_google;"discord"->R.string.provider_discord;else->R.string.email}))}
            }
            current.data.modeStats.forEach{stats->BrawlPanel(Modifier.fillMaxWidth()){
                Text(stringResource(when(stats.mode){"duo"->R.string.duo;"squad"->R.string.squad;"solo"->R.string.solo;else->R.string.duel}),style=MaterialTheme.typography.titleMedium)
                Text(namedString(R.string.games_played,"count" to stats.played))
                Text(namedString(R.string.games_won,"count" to stats.wins))
                Text(namedString(R.string.mode_best,"score" to stats.best))
            }}
            BrawlPanel(Modifier.fillMaxWidth()) {
                Text(namedString(R.string.games_played,"count" to current.data.played.toString()))
                Text(namedString(R.string.games_won,"count" to current.data.wins.toString()))
            }
            OutlinedTextField(username,{username=it},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.username))},singleLine=true)
            BrawlButton(stringResource(R.string.save),{model.rename(username)},Modifier.fillMaxWidth(),enabled=!ui.busy && AuthValidation.username(username))
            Text(stringResource(R.string.inventory),style=MaterialTheme.typography.titleLarge)
            if(current.data.inventory.isEmpty()) Text(stringResource(R.string.empty_inventory))
            current.data.inventory.forEach { cosmetic ->
                val label=com.brainybrawl.app.core.localization.catalogLabel(cosmetic.labels,androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language)
                Text(label?:stringResource(R.string.content_unavailable))
                if(cosmetic.kind!="boost") BrawlButton(stringResource(when(cosmetic.kind) { "frame"->R.string.equip_frame;"banner"->R.string.equip_banner;else->R.string.equip_avatar }),{model.equip(cosmetic.id)},Modifier.fillMaxWidth(),enabled=!ui.busy)
            }
            if(ui.failed) Text(stringResource(R.string.request_failed),color=MaterialTheme.colorScheme.error)
        }
    }
    ActionBento(listOf(BentoAction(stringResource(R.string.change_password),NavSymbol.SETTINGS,password),BentoAction(stringResource(R.string.sign_out),NavSymbol.HOME,logout)))
}
