package com.brainybrawl.app.feature.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.time.Instant

@Serializable data class MatchData(val id:String,val mode:String,val status:String,val version:Long,
    @SerialName("active_round")val activeRound:Int)
@Serializable data class MatchPlayer(@SerialName("user_id")val userId:String,val name:String?=null,val score:Int,val eligible:Boolean,@SerialName("team_id")val teamId:String?=null,val seat:Int=0)
@Serializable data class RoundOption(val id:String,val label:String)
@Serializable data class RoundDisplay(val theme:String="",val prompt:String="",val specification:String="",
    @SerialName("asset_ref")val assetRef:String?=null,val options:List<RoundOption> = emptyList(),val letters:String="")
@Serializable data class SubmissionReceipt(val action:JsonObject,val receipt:String)
@Serializable data class MatchRound(val id:String,val ordinal:Int,val status:String,val kind:String,
    @SerialName("starts_at")val startsAt:String,@SerialName("answer_opens_at")val opensAt:String,
    val deadline:String,val content:RoundDisplay,val reveal:JsonObject?=null,val submission:SubmissionReceipt?=null)
@Serializable data class MatchResult(@SerialName("user_id")val userId:String,val rank:Int,val score:Int,val winner:Boolean)
@Serializable data class TieRoll(@SerialName("user_id")val userId:String,val attempt:Int,val roll:Int)
@Serializable data class MatchSnapshot(@SerialName("schema_version")val schemaVersion:Int,
    @SerialName("server_time")val serverTime:String,val match:MatchData,val participants:List<MatchPlayer>,
    val rounds:List<MatchRound>,val results:List<MatchResult>,@SerialName("tie_rolls")val tieRolls:List<TieRoll>,
    @SerialName("starts_at")val startsAt:String,val draft:TeamDraft?=null,val board:BoardState?=null,
    val teams:List<MatchTeam> = emptyList(),@SerialName("team_rankings")val teamRankings:List<TeamRanking> = emptyList())
class MatchSnapshotGate(private val id:String) {
    private var version=-1L
    private var time=Long.MIN_VALUE
    fun accept(snapshot:MatchSnapshot):Boolean {
        require(snapshot.schemaVersion==1 && snapshot.match.id==id)
        require(snapshot.rounds.map{it.id}.toSet().size==snapshot.rounds.size)
        require(snapshot.participants.map{it.userId}.toSet().size==snapshot.participants.size)
        val next=Instant.parse(snapshot.serverTime).toEpochMilli()
        if(snapshot.match.version<version || (snapshot.match.version==version && next<=time))return false
        version=snapshot.match.version;time=next;return true
    }
}
sealed interface MatchConnection {
    data object Loading:MatchConnection
    data class Live(val snapshot:MatchSnapshot):MatchConnection
    data class Recovering(val snapshot:MatchSnapshot?):MatchConnection
}

@Serializable data class MatchTeam(val id:String,val name:String)
@Serializable data class TeamDraft(val question:Int,@SerialName("choosing_team")val choosingTeam:String,
    val answerers:Map<String,String>,@SerialName("round_id")val roundId:String?=null,val theme:String?=null,val themes:List<String> = emptyList())
@Serializable data class TeamRanking(val stage:Int,@SerialName("team_id")val teamId:String,val total:Int,val rolls:List<Int>,val rank:Int)
@Serializable data class MiniReceipt(val accepted:Boolean,val correct:Boolean,val points:Int,val receipt:String)
