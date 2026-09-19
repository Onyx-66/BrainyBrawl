package com.brainybrawl.app.feature.lobby

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant

@Serializable enum class OnlineMode { @SerialName("duel") DUEL,@SerialName("duo") DUO,@SerialName("squad") SQUAD,@SerialName("solo") SOLO }
@Serializable data class RoomData(val id:String,@SerialName("host_id") val hostId:String,val mode:OnlineMode,
    val matchmaking:Boolean,val status:String,val version:Long)
@Serializable data class RoomMember(@SerialName("user_id") val userId:String,@SerialName("team_id") val teamId:String?=null,
    val seat:Int,val ready:Boolean,@SerialName("last_seen") val lastSeen:String,val username:String?=null)
@Serializable data class RoomTeam(val id:String,val name:String,val position:Int)
@Serializable data class ReactionEvent(val id:String,@SerialName("user_id") val userId:String,@SerialName("reaction_id") val reactionId:String,@SerialName("created_at") val createdAt:String)
@Serializable data class RoomSnapshot(@SerialName("schema_version") val schemaVersion:Int,@SerialName("server_time") val serverTime:String,
    val room:RoomData,val members:List<RoomMember>,val teams:List<RoomTeam>,@SerialName("match_id") val matchId:String?=null,val reactions:List<ReactionEvent> = emptyList())
@Serializable data class RoomInvite(@SerialName("room_id") val roomId:String,val sender:String,val mode:OnlineMode)
sealed interface RoomConnection {
    data object Empty:RoomConnection
    data object Connecting:RoomConnection
    data class Live(val snapshot:RoomSnapshot):RoomConnection
    data class Recovering(val snapshot:RoomSnapshot?):RoomConnection
}

/** Prevent a delayed fetch or duplicate event from rewinding the authoritative state. */
class RoomSnapshotGate(private val roomId:String) {
    private var latest:RoomSnapshot?=null
    fun accept(candidate:RoomSnapshot):RoomSnapshot? {
        require(candidate.schemaVersion==1 && candidate.room.id==roomId)
        require(candidate.members.map{it.userId}.toSet().size==candidate.members.size)
        require(candidate.room.status in setOf("lobby","playing","closed"))
        val old=latest
        if(old!=null && (candidate.room.version<old.room.version ||
            (candidate.room.version==old.room.version && !Instant.parse(candidate.serverTime).isAfter(Instant.parse(old.serverTime))))) return null
        latest=candidate;return candidate
    }
}

@Serializable data class ReactionPreset(val id:String,val text:String,@SerialName("duration_ms")val durationMillis:Long=1500)
