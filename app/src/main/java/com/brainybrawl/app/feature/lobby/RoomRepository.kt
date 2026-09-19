package com.brainybrawl.app.feature.lobby

import android.os.SystemClock
import com.brainybrawl.app.core.network.ServerClock
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import java.time.Instant
import java.util.UUID

interface RoomRepository {
    suspend fun reactions(locale:String):List<ReactionPreset>
    suspend fun react(room:String,reaction:String)
    suspend fun reportReaction(event:ReactionEvent)
    suspend fun currentRoom():String?
    suspend fun invites():List<RoomInvite>
    suspend fun create(mode:OnlineMode,quick:Boolean):String
    suspend fun joinPublic(mode:OnlineMode):String?
    suspend fun join(room:String):String
    suspend fun ready(room:String,ready:Boolean)
    suspend fun matchmaking(room:String,enabled:Boolean)
    suspend fun invite(room:String,friend:String)
    suspend fun renameTeam(room:String,name:String)
    suspend fun leave(room:String)
    suspend fun start(room:String,locale:String):String
    fun observe(room:String):Flow<RoomConnection>
}
class SupabaseRoomRepository(private val client:SupabaseClient?):RoomRepository {
    private val json=Json { ignoreUnknownKeys=true }
    val clock=ServerClock(SystemClock::elapsedRealtime)
    private suspend fun rpc(name:String,args:JsonObject=buildJsonObject {}):String=requireNotNull(client).postgrest.rpc(name,args).data
    override suspend fun reactions(locale:String):List<ReactionPreset> =json.decodeFromString(rpc("reaction_catalog",buildJsonObject{put("p_locale",locale)}))
    override suspend fun react(room:String,reaction:String){rpc("send_reaction",buildJsonObject{put("p_room",room);put("p_reaction",reaction)})}
    override suspend fun reportReaction(event:ReactionEvent){rpc("report_player",buildJsonObject{put("p_target",event.userId);put("p_category","harassment");put("p_details","");put("p_event",event.id)})}
    override suspend fun currentRoom():String?=json.decodeFromString(rpc("current_room"))
    override suspend fun invites():List<RoomInvite> =json.decodeFromString(rpc("pending_invites"))
    override suspend fun create(mode:OnlineMode,quick:Boolean):String {
        require(!quick||mode==OnlineMode.DUEL)
        return json.decodeFromString(if(quick)rpc("quick_match") else rpc("create_room",buildJsonObject{put("p_mode",mode.name.lowercase())}))
    }
    override suspend fun joinPublic(mode:OnlineMode):String?=json.decodeFromString(rpc("join_public_room",buildJsonObject{put("p_mode",mode.name.lowercase())}))
    override suspend fun join(room:String):String=json.decodeFromString(rpc("join_room",buildJsonObject{put("p_room",room)}))
    override suspend fun ready(room:String,ready:Boolean){rpc("set_ready",buildJsonObject{put("p_room",room);put("p_ready",ready)})}
    override suspend fun matchmaking(room:String,enabled:Boolean){rpc("set_matchmaking",buildJsonObject{put("p_room",room);put("p_enabled",enabled)})}
    override suspend fun invite(room:String,friend:String){rpc("invite_friend",buildJsonObject{put("p_room",room);put("p_friend",friend)})}
    override suspend fun renameTeam(room:String,name:String){rpc("rename_team",buildJsonObject{put("p_room",room);put("p_name",name)})}
    override suspend fun leave(room:String){rpc("leave_room",buildJsonObject{put("p_room",room)})}
    override suspend fun start(room:String,locale:String):String=json.decodeFromString(rpc("start_match",buildJsonObject{put("p_room",room);put("p_locale",locale)}))

    override fun observe(room:String):Flow<RoomConnection> = channelFlow {
        require(UUID.fromString(room).toString()==room)
        val backend=requireNotNull(client)
        val gate=RoomSnapshotGate(room)
        val signal=Channel<Unit>(Channel.CONFLATED)
        val channel=backend.channel("room:$room")
        val changes=channel.postgresChangeFlow<PostgresAction.Update>(schema="public") {
            table="rooms";filter("id",FilterOperator.EQ,room)
        }
        var latest:RoomSnapshot?=null
        send(RoomConnection.Connecting)
        val collector=launch(start=CoroutineStart.UNDISPATCHED) { changes.collect { signal.trySend(Unit) } }
        val subscribe=launch {
            try { withTimeout(10_000){channel.subscribe(blockUntilSubscribed=true)};signal.trySend(Unit) }
            catch(e:CancellationException){if(!isActive)throw e}
            catch(_:Exception){ /* Authoritative polling covers unavailable/missed Realtime messages. */ }
        }
        val poll=launch { while(isActive){signal.trySend(Unit);delay(5_000)} }
        val heartbeat=launch {
            while(isActive) {
                try { rpc("heartbeat",buildJsonObject{put("p_room",room)}) }
                catch(e:CancellationException){throw e}
                catch(_:Exception){ /* Snapshot fetch below surfaces connection loss. */ }
                delay(10_000)
            }
        }
        try {
            for(ignored in signal) {
                try {
                    val sent=SystemClock.elapsedRealtime()
                    val snapshot=json.decodeFromString<RoomSnapshot>(rpc("room_snapshot",buildJsonObject{put("p_room",room)}))
                    val received=SystemClock.elapsedRealtime()
                    gate.accept(snapshot)?.let {
                        clock.sample(Instant.parse(it.serverTime).toEpochMilli(),sent,received)
                        latest=it
                    }
                    latest?.let{send(RoomConnection.Live(it))}
                } catch(e:CancellationException){throw e}
                catch(_:Exception){send(RoomConnection.Recovering(latest))}
                // Coalescing bounds repeated room-change notifications under 40-player load.
                delay(200)
            }
        } finally {
            collector.cancel();subscribe.cancel();poll.cancel();heartbeat.cancel();signal.close()
            withContext(NonCancellable){withTimeoutOrNull(3_000){backend.realtime.removeChannel(channel)}}
        }
    }
}
