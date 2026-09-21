package com.brainybrawl.app.feature.store
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.core.localization.catalogLabel
import com.brainybrawl.app.feature.profile.*
import kotlinx.coroutines.*
import androidx.compose.ui.graphics.asImageBitmap

@Composable fun StoreScreen(model:StoreViewModel,signedIn:Boolean,onPurchased:()->Unit,initialCurrency:OfferCurrency=OfferCurrency.GEMS,sectionRequest:Int=0,appearance:AppearanceRepository?=null,userId:String?=null){
 val ui by model.ui.collectAsStateWithLifecycle();val locale=LocalConfiguration.current.locales[0].language
 var cosmetics by rememberSaveable{mutableStateOf(false)};var kind by rememberSaveable{mutableStateOf("avatars")}
 var confirmation by remember{mutableStateOf<StoreItem?>(null)}
 LaunchedEffect(sectionRequest){if(sectionRequest>0)cosmetics=false}
 Text(stringResource(R.string.store_stock_up),style=MaterialTheme.typography.headlineMedium)
 BrawlPanel(Modifier.fillMaxWidth()){
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
   FilterChip(!cosmetics,{cosmetics=false},label={Text(stringResource(R.string.store_currency))},modifier=Modifier.weight(1f))
   FilterChip(cosmetics,{cosmetics=true},label={Text(stringResource(R.string.store_cosmetics))},modifier=Modifier.weight(1f))
  }
  if(!cosmetics)CurrencyOffers(initialCurrency,sectionRequest)
  else {
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    FilterChip(kind=="avatars",{kind="avatars"},label={Text(stringResource(R.string.avatars))},modifier=Modifier.weight(1f))
    FilterChip(kind=="frames",{kind="frames"},label={Text(stringResource(R.string.frames))},modifier=Modifier.weight(1f))
   }
   CosmeticOffers(kind,appearance,userId)
   if(signedIn){
    if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth())
    if(ui.failed)FeedbackPanel(stringResource(R.string.request_failed),stringResource(R.string.purchase_retry),stringResource(R.string.retry),model::refresh)
    ui.snapshot?.items.orEmpty().filter{it.kind==kind.removeSuffix("s")}.forEach{item->
     HorizontalDivider()
     Text(catalogLabel(item.labels,locale)?:stringResource(R.string.content_unavailable),style=MaterialTheme.typography.titleMedium)
     Text(namedString(R.string.item_price,"amount" to item.price,"currency" to stringResource(when(item.currency){"gold"->R.string.gold;"gems"->R.string.gems;else->R.string.flames})))
     BrawlButton(stringResource(if(item.owned)R.string.owned else R.string.purchase),{confirmation=item},Modifier.fillMaxWidth(),enabled=!item.owned&&!ui.busy)
    }
   }
  }
 }
 confirmation?.let{item->AlertDialog(onDismissRequest={confirmation=null},title={Text(catalogLabel(item.labels,locale)?:stringResource(R.string.confirm_purchase))},text={Text(namedString(R.string.item_price,"amount" to item.price,"currency" to item.currency))},confirmButton={TextButton({model.purchase(item.id);confirmation=null}){Text(stringResource(R.string.purchase))}},dismissButton={TextButton({confirmation=null}){Text(stringResource(R.string.cancel))}})}
 LaunchedEffect(ui.snapshot){if(ui.snapshot!=null)onPurchased()}
}
@Composable private fun CosmeticOffers(kind:String,appearance:AppearanceRepository?,userId:String?){
 val context=LocalContext.current;val locale=LocalConfiguration.current.locales[0].language
 val offers=remember(kind){OfferCatalog.load(context,"cosmetics",kind)};val scope=rememberCoroutineScope()
 var busy by remember{mutableStateOf(false)};var saved by remember{mutableStateOf(false)};var failed by remember{mutableStateOf(false)}
 if(saved)Text(stringResource(R.string.cosmetic_saved),color=MaterialTheme.colorScheme.secondary)
 if(failed)Text(stringResource(R.string.request_failed),color=MaterialTheme.colorScheme.error)
 offers.chunked(2).forEach{row->Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(10.dp)){
  row.forEach{offer->Column(Modifier.weight(1f).fillMaxHeight(),horizontalAlignment=androidx.compose.ui.Alignment.CenterHorizontally){
   val state by rememberArtwork(offer.asset)
   (state as? ArtworkState.Ready)?.let{androidx.compose.foundation.Image(androidx.compose.ui.graphics.painter.BitmapPainter(it.bitmap.asImageBitmap()),null,Modifier.size(88.dp))}
   Text(offer.name[locale]?:offer.name.getValue("en"),style=MaterialTheme.typography.labelLarge)
   offer.description[locale]?.takeIf{it.isNotBlank()}?.let{Text(it,style=MaterialTheme.typography.bodySmall)}
   Text(if(offer.price==0.0)stringResource(R.string.free_offer)else "${offer.price} ${offer.currency}",style=MaterialTheme.typography.labelMedium)
   TextButton({scope.launch{busy=true;failed=false;try{val old=appearance!!.read(userId!!);appearance.select(userId,if(kind=="avatars")old.copy(avatar=offer.index)else old.copy(frame=offer.index));saved=true}catch(e:CancellationException){throw e}catch(_:Exception){failed=true}finally{busy=false}}},enabled=!busy&&offer.enabled&&offer.price==0.0&&appearance!=null&&userId!=null){Text(stringResource(R.string.use_cosmetic))}
  }}
 }}
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
