package com.brainybrawl.app.feature.store

import android.content.Context
import com.brainybrawl.app.feature.profile.OwnedCosmetic
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.*
import java.util.UUID

@Serializable data class StoreItem(val id: String,@SerialName("cosmetic_id") val cosmeticId: String,val kind: String,
    @SerialName("label_key") val labelKey: String,val currency: String,val price: Long,val vault: Boolean,val owned: Boolean,val labels:Map<String,String> = emptyMap())
@Serializable data class StoreSnapshot(val items: List<StoreItem>,val boosts: List<OwnedCosmetic>,val loadout: List<String>)
interface StoreRepository {
    suspend fun snapshot(): StoreSnapshot
    suspend fun purchase(item: String)
    suspend fun loadout(items: List<String>)
}
/** Persistent request IDs make retry-after-network-loss safe across process death. */
class SupabaseStoreRepository(private val client: SupabaseClient?,context: Context) : StoreRepository {
    private val pending=context.getSharedPreferences("purchase_requests",Context.MODE_PRIVATE)
    private val json=Json { ignoreUnknownKeys=true }
    override suspend fun snapshot():StoreSnapshot=json.decodeFromString(requireNotNull(client).postgrest.rpc("store_snapshot").data)
    override suspend fun purchase(item:String)=withContext(Dispatchers.IO) {
        val backend=requireNotNull(client)
        val user=requireNotNull(backend.auth.currentUserOrNull()).id
        val storageKey="$user:$item"
        val key=synchronized(pending) {
            pending.getString(storageKey,null) ?: UUID.randomUUID().toString().also {
                check(pending.edit().putString(storageKey,it).commit())
            }
        }
        backend.postgrest.rpc("purchase_item",buildJsonObject { put("p_item",item);put("p_key",key) })
        // A known success ends this request. A thrown network error retains the ID.
        check(pending.edit().remove(storageKey).commit())
    }
    override suspend fun loadout(items:List<String>) {
        require(items.size==2 && items.toSet().size==2)
        requireNotNull(client).postgrest.rpc("set_loadout",buildJsonObject { put("p_items",JsonArray(items.map(::JsonPrimitive))) })
    }
}
