package com.brainybrawl.app.feature.leaderboard
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable data class RankedPlayer(@SerialName("user_id")val userId:String,val username:String,val played:Int,val value:Double,val rank:Long)
@Serializable data class LeaderboardSnapshot(val mode:String,val metric:String,val period:String,
    @SerialName("friends_only")val friendsOnly:Boolean,val rows:List<RankedPlayer>,val own:RankedPlayer?=null)
data class LeaderboardFilter(val mode:String="duel",val metric:String="win_rate",val period:String="all_time",val friends:Boolean=false)
interface LeaderboardRepository { suspend fun load(filter:LeaderboardFilter):LeaderboardSnapshot }
class SupabaseLeaderboardRepository(private val client:SupabaseClient?):LeaderboardRepository {
    private val json=Json{ignoreUnknownKeys=true}
    override suspend fun load(filter:LeaderboardFilter):LeaderboardSnapshot=json.decodeFromString(requireNotNull(client).postgrest.rpc("leaderboard",buildJsonObject{
        put("p_mode",filter.mode);put("p_metric",filter.metric);put("p_period",filter.period);put("p_friends",filter.friends);put("p_limit",50)
    }).data)
}
