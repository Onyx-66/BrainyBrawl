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
fun ProfileScreen(model: PlayerViewModel, container:com.brainybrawl.app.core.AppContainer,authModel:com.brainybrawl.app.feature.auth.AuthViewModel,achievements:()->Unit,friends:()->Unit,password: () -> Unit, logout: () -> Unit) {
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
            var editing by remember{mutableStateOf(false)}
            PlayerCard(container.appearance,profile.id,profile.username,current.data.level,profile.number,current.data.currencies,ui.snapshot.friends.count{it.status=="accepted"},friends,{editing=true},achievements)
            (identity as? com.brainybrawl.app.feature.auth.AuthState.SignedIn)?.let{AccountDetailsCard(container,it,authModel,current.data.email,current.data.providers,password,logout)}
            if(editing) UsernameDialog(username,{editing=false}){model.rename(it);editing=false}
            current.data.inventory.filter{it.kind=="boost"}.takeIf{it.isNotEmpty()}?.let{items->
                Text(stringResource(R.string.inventory),style=MaterialTheme.typography.titleLarge)
                items.forEach{Text(com.brainybrawl.app.core.localization.catalogLabel(it.labels,androidx.compose.ui.platform.LocalConfiguration.current.locales[0].language)?:stringResource(R.string.content_unavailable))}
            }
            if(ui.failed) Text(stringResource(R.string.request_failed),color=MaterialTheme.colorScheme.error)
        }
    }

}
