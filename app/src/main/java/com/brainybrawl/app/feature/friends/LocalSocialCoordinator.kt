package com.brainybrawl.app.feature.friends

import com.brainybrawl.app.feature.auth.*
import com.brainybrawl.app.feature.profile.PlayerRepository
import com.brainybrawl.app.feature.lobby.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/** Explicit user-triggered delivery; a device account cannot impersonate an online sender. */
class LocalSocialCoordinator(private val local:LocalAccounts,private val auth:AuthRepository,
    private val players:PlayerRepository,private val rooms:RoomRepository){
    suspend fun sync():Int{
        val identity=requireNotNull(local.state.value.current)
        val sender=auth.state.value as? AuthState.SignedIn?:error("online_required")
        require(!sender.local&&sender.email.equals(identity.email,true))
        fun authorized(){require(auth.state.value==sender&&local.state.value.current?.id==identity.id)}
        var sent=0
        for(item in local.state.value.queue.filter{!it.sent}){
            authorized()
            try{
                val target=players.search(item.target).singleOrNull()?:continue
                authorized()
                if(item.kind=="friend")players.friend(target.id,"request") else{
                    val mode=OnlineMode.valueOf(item.mode.uppercase(java.util.Locale.ROOT))
                    val room=item.room?:rooms.currentRoom()?:rooms.create(mode,false)
                    authorized()
                    val snapshot=withTimeout(10_000){rooms.observe(room).filterIsInstance<RoomConnection.Live>().first()}.snapshot
                    require(snapshot.room.mode==mode&&snapshot.room.status=="lobby")
                    authorized();local.update(item.copy(room=room))
                    rooms.invite(room,target.id)
                }
                authorized();local.update(item.copy(sent=true));sent++
            }catch(e:CancellationException){throw e}catch(_:Exception){/* Retain unsent requests; UI explains that acceptance/connectivity is required. */}
        }
        return sent
    }
}
