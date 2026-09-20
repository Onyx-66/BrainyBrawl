package com.brainybrawl.app.feature.profile

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.*

@Serializable data class PlayerProfile(val id: String,@SerialName("player_number") val number: Long,val username: String)
@Serializable data class Balance(val currency: String,val balance: Long)
@Serializable data class OwnedCosmetic(val id: String,val kind: String,@SerialName("label_key") val labelKey: String,val labels:Map<String,String> = emptyMap())
@Serializable data class ProfileSnapshot(val profile: PlayerProfile,val currencies: List<Balance>,val inventory: List<OwnedCosmetic>,val played: Int,val wins: Int,val email:String?=null,val providers:List<String> = emptyList(),@SerialName("mode_stats")val modeStats:List<ModeStatistics> = emptyList(),@SerialName("lifetime_flames")val lifetimeFlames:Long=0,val level:Long=1,@SerialName("is_admin")val isAdmin:Boolean=false)
@Serializable data class ModeStatistics(val mode:String,val played:Int,val wins:Int,val best:Int)
@Serializable data class PlayerPreview(val profile:PlayerProfile,val inventory:List<OwnedCosmetic>,@SerialName("mode_stats")val modeStats:List<ModeStatistics>)
@Serializable data class FriendEntry(@SerialName("user_id") val userId: String,@SerialName("player_number") val number: Long,val username: String,val status: String,val incoming: Boolean)
@Serializable data class BlockEntry(@SerialName("user_id") val userId: String,@SerialName("player_number") val number: Long)
@Serializable data class SocialSnapshot(val friends: List<FriendEntry>,val blocks: List<BlockEntry>)
interface PlayerRepository {
    suspend fun profile(): ProfileSnapshot
    suspend fun preview(userId:String):PlayerPreview
    suspend fun social(): SocialSnapshot
    suspend fun search(query: String): List<PlayerProfile>
    suspend fun rename(username: String)
    suspend fun friend(userId: String,action: String)
    suspend fun block(userId: String,blocked: Boolean)
    suspend fun report(userId: String,category: String,details: String)
    suspend fun equip(item: String)
}
class SupabasePlayerRepository(private val client: SupabaseClient?) : PlayerRepository {
    private val json=Json { ignoreUnknownKeys=true }
    private suspend fun rpc(name: String,args: JsonObject=buildJsonObject {}): String =
        requireNotNull(client) { "Backend unavailable" }.postgrest.rpc(name,args).data
    override suspend fun profile()=json.decodeFromString<ProfileSnapshot>(rpc("profile_snapshot"))
    override suspend fun preview(userId:String)=json.decodeFromString<PlayerPreview>(rpc("player_preview",buildJsonObject{put("p_target",userId)}))
    override suspend fun social()=json.decodeFromString<SocialSnapshot>(rpc("social_snapshot"))
    override suspend fun search(query: String)=json.decodeFromString<List<PlayerProfile>>(rpc("search_players",buildJsonObject { put("p_query",PlayerId.searchQuery(query)) }))
    override suspend fun rename(username: String) { rpc("update_profile",buildJsonObject { put("p_username",username) }) }
    override suspend fun friend(userId: String,action: String) { rpc("friend_action",buildJsonObject { put("p_target",userId);put("p_action",action) }) }
    override suspend fun block(userId: String,blocked: Boolean) { rpc("set_block",buildJsonObject { put("p_target",userId);put("p_blocked",blocked) }) }
    override suspend fun report(userId: String,category: String,details: String) { rpc("report_player",buildJsonObject { put("p_target",userId);put("p_category",category);put("p_details",details) }) }
    override suspend fun equip(item: String) { rpc("equip_cosmetic",buildJsonObject { put("p_item",item) }) }
}
