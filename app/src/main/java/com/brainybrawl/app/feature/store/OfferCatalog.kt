package com.brainybrawl.app.feature.store
import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
@Serializable data class CatalogOffer(val id:String,val name:Map<String,String>,val description:Map<String,String> = emptyMap(),val asset:String,val quantity:Int=1,val price:Double,val currency:String,val enabled:Boolean=false,val index:Int=0)
object OfferCatalog{
 fun load(context:Context,section:String,kind:String):List<CatalogOffer>{
  require(section in setOf("currencies","cosmetics")&&kind in setOf("coins","gems","flames","avatars","frames"))
  val rows=Json{ignoreUnknownKeys=true}.decodeFromString<List<CatalogOffer>>(context.assets.open("assets/store/$section/$kind/offers.json").bufferedReader().use{it.readText()})
  require(rows.map{it.id}.distinct().size==rows.size&&rows.all{it.price>=0&&it.quantity>0&&it.asset.startsWith("assets/")&&!it.asset.contains("..")})
  return rows
 }
}
