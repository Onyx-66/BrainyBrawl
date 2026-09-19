package com.brainybrawl.app.feature.match

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
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

interface MatchRepository {
    val clock:ServerClock
    fun observe(id:String):Flow<MatchConnection>
    suspend fun chooseTheme(match:String,question:Int,theme:String)
    suspend fun miniAction(match:String,round:String,key:String,action:JsonObject,relay:Boolean):MiniReceipt
    suspend fun cursor(match:String,round:String,x:Float,y:Float)
    suspend fun submit(match:String,round:String,key:String,action:JsonObject)
}
class SupabaseMatchRepository(private val client:SupabaseClient?):MatchRepository {
    override val clock=ServerClock(SystemClock::elapsedRealtime)
    private val json=Json{ignoreUnknownKeys=true;classDiscriminator="kind"}
    override suspend fun chooseTheme(match:String,question:Int,theme:String){requireNotNull(client).postgrest.rpc("choose_draft_theme",buildJsonObject{put("p_match",match);put("p_question",question);put("p_theme",theme)})}
    override suspend fun miniAction(match:String,round:String,key:String,action:JsonObject,relay:Boolean):MiniReceipt =json.decodeFromString(requireNotNull(client).postgrest.rpc(if(relay)"submit_relay_action" else "submit_team_action",buildJsonObject{put("p_match",match);put("p_round",round);put("p_key",key);put("p_action",action)}).data)
    override suspend fun cursor(match:String,round:String,x:Float,y:Float){requireNotNull(client).postgrest.rpc("move_puzzle_cursor",buildJsonObject{put("p_match",match);put("p_round",round);put("p_x",x);put("p_y",y)})}
    override suspend fun submit(match:String,round:String,key:String,action:JsonObject) {
        requireNotNull(client).postgrest.rpc("submit_answer",buildJsonObject{
            put("p_match",match);put("p_round",round);put("p_key",key);put("p_action",action)
        })
    }
    override fun observe(id:String):Flow<MatchConnection> = channelFlow {
        val backend=requireNotNull(client)
        val gate=MatchSnapshotGate(id)
        val signal=Channel<Unit>(Channel.CONFLATED)
        val channel=backend.channel("match:$id")
        val changes=channel.postgresChangeFlow<PostgresAction.Update>(schema="public"){
            table="matches";filter("id",FilterOperator.EQ,id)
        }
        var latest:MatchSnapshot?=null
        var recovering=false
        var started=false
        val collect=launch(start=CoroutineStart.UNDISPATCHED){changes.collect{signal.trySend(Unit)}}
        val subscribe=launch{
            try{withTimeout(10_000){channel.subscribe(blockUntilSubscribed=true)}}
            catch(e:CancellationException){if(!isActive)throw e}
            catch(_:Exception){ /* Polling recovers dropped events. */ }
        }
        val poll=launch{while(isActive){signal.trySend(Unit);delay(1_000)}}
        try {
            send(MatchConnection.Loading)
            for(ignored in signal) {
                try {
                    val sent=SystemClock.elapsedRealtime()
                    val snapshot=json.decodeFromString<MatchSnapshot>(backend.postgrest.rpc("match_snapshot",buildJsonObject{put("p_match",id)}).data)
                    val received=SystemClock.elapsedRealtime()
                    if(gate.accept(snapshot)){
                        clock.sample(Instant.parse(snapshot.serverTime).toEpochMilli(),sent,received);latest=snapshot
                    }
                    if(recovering){Diagnostics.record(ProductEvent.CONNECTION_RESTORED);recovering=false}
                    if(!started){Diagnostics.record(ProductEvent.MATCH_START);started=true}
                    latest?.let{send(MatchConnection.Live(it))}
                    if(latest?.match?.status in setOf("results","closed")){Diagnostics.record(ProductEvent.MATCH_END);break}
                }catch(e:CancellationException){throw e}
                catch(_:Exception){if(!recovering)Diagnostics.record(ProductEvent.CONNECTION_LOST);recovering=true;send(MatchConnection.Recovering(latest))}
                delay(150)
            }
        }finally{
            collect.cancel();subscribe.cancel();poll.cancel();signal.close()
            withContext(NonCancellable){withTimeoutOrNull(3_000){backend.realtime.removeChannel(channel)}}
        }
    }
}
