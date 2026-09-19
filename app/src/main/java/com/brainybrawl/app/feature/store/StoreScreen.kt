package com.brainybrawl.app.feature.store

import androidx.compose.foundation.layout.*
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
import com.brainybrawl.app.core.localization.catalogLabel
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun StoreScreen(model:StoreViewModel,signedIn:Boolean,onPurchased:()->Unit) {
    val ui by model.ui.collectAsStateWithLifecycle()
    val locale=LocalConfiguration.current.locales[0].language
    var vault by rememberSaveable { mutableStateOf(false) }
    var confirmation by remember { mutableStateOf<StoreItem?>(null) }
    Text(stringResource(R.string.store),style=MaterialTheme.typography.headlineMedium)
    if(!signedIn){Text(stringResource(R.string.sign_in_required));return}
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        FilterChip(!vault,{vault=false},label={Text(stringResource(R.string.catalog))})
        FilterChip(vault,{vault=true},label={Text(stringResource(R.string.flame_vault))})
    }
    if(ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
    if(ui.failed) FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.purchase_retry),stringResource(R.string.retry),model::refresh)
    val items=ui.snapshot?.items?.filter { it.vault==vault }
    if(items!=null && items.isEmpty()) Text(stringResource(R.string.empty_store))
    items.orEmpty().forEach { item ->
        val label=catalogLabel(item.labels,locale)
        BrawlPanel(Modifier.fillMaxWidth()) {
            Text(label?:stringResource(R.string.content_unavailable),style=MaterialTheme.typography.titleLarge)
            Text(stringResource(when(item.kind){"avatar"->R.string.avatar;"frame"->R.string.frame;"banner"->R.string.banner;else->R.string.boost}),style=MaterialTheme.typography.titleLarge)
            Text(namedString(R.string.item_price,"amount" to item.price,"currency" to stringResource(when(item.currency){"gold"->R.string.gold;"gems"->R.string.gems;else->R.string.flames})))
            BrawlButton(stringResource(if(item.owned) R.string.owned else R.string.purchase),{confirmation=item},Modifier.fillMaxWidth(),enabled=label!=null&&!item.owned&&!ui.busy,tone=if(item.vault)ActionTone.REWARD else ActionTone.PRIMARY)
        }
    }
    Text(stringResource(R.string.flames_skill_only),style=MaterialTheme.typography.bodyMedium)
    confirmation?.let { item ->
        AlertDialog(onDismissRequest={confirmation=null},title={Text(catalogLabel(item.labels,locale)?:stringResource(R.string.confirm_purchase))},
            text={Text(namedString(R.string.item_price,"amount" to item.price,"currency" to stringResource(when(item.currency){"gold"->R.string.gold;"gems"->R.string.gems;else->R.string.flames})))},
            confirmButton={TextButton(onClick={model.purchase(item.id);confirmation=null}){Text(stringResource(R.string.purchase))}},
            dismissButton={TextButton(onClick={confirmation=null}){Text(stringResource(R.string.cancel))}})
    }
    LaunchedEffect(ui.snapshot){if(ui.snapshot!=null)onPurchased()}
}

@Composable
fun LoadoutScreen(model:StoreViewModel,continueAction:()->Unit) {
    val ui by model.ui.collectAsStateWithLifecycle()
    val locale=LocalConfiguration.current.locales[0].language
    var selected by rememberSaveable { mutableStateOf(listOf<String>()) }
    LaunchedEffect(ui.snapshot?.loadout){ui.snapshot?.let{selected=it.loadout}}
    LaunchedEffect(ui.saved,ui.busy){if(ui.saved&&!ui.busy&&!ui.failed){model.consumeSaved();continueAction()}}
    Text(stringResource(R.string.loadout),style=MaterialTheme.typography.headlineMedium)
    Text(stringResource(R.string.choose_two_boosts))
    if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(ui.failed)Text(stringResource(R.string.request_failed),color=MaterialTheme.colorScheme.error)
    if(ui.snapshot?.boosts?.isEmpty()==true)Text(stringResource(R.string.no_boosts))
    ui.snapshot?.boosts.orEmpty().forEach { boost ->
        AnswerOption(catalogLabel(boost.labels,locale)?:stringResource(R.string.content_unavailable),boost.id in selected,!ui.busy&&catalogLabel(boost.labels,locale)!=null,{
            selected=if(boost.id in selected)selected-boost.id else if(selected.size<2)selected+boost.id else selected
        })
    }
    BrawlButton(stringResource(R.string.continue_action),{model.saveLoadout(selected)},Modifier.fillMaxWidth(),enabled=selected.size in setOf(0,2)&&ui.snapshot!=null&&!ui.busy,tone=ActionTone.POSITIVE)
}
