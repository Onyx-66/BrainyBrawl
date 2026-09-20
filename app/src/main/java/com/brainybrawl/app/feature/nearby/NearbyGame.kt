package com.brainybrawl.app.feature.nearby

import com.brainybrawl.app.game.content.QuestionContent
import kotlinx.serialization.Serializable

@Serializable enum class NearbyMode(val capacity:Int,val minimum:Int){ DUEL(2,2), DUO(4,4), SQUAD(8,8), SOLO(8,2) }
@Serializable data class NearbyPlayer(val id:String,val name:String,val team:Int,val ready:Boolean=false,val score:Int=0,val answered:Boolean=false)
@Serializable data class NearbyOption(val id:String,val label:String)
@Serializable data class NearbyState(val mode:NearbyMode=NearbyMode.DUEL,val phase:String="lobby",val players:List<NearbyPlayer> = emptyList(),val round:Int=0,val total:Int=0,val prompt:String="",val options:List<NearbyOption> = emptyList(),val seconds:Int=0,val correct:String?=null)
@Serializable data class NearbyMessage(val version:Int=1,val type:String,val name:String="",val playerId:String="",val ready:Boolean=false,val round:Int=0,val answer:String="",val state:NearbyState?=null)

/** Host authority: remote clients send intent only, never scores, player identities or clocks. */
class NearbyGame(mode:NearbyMode,hostName:String){
    var state=NearbyState(mode=mode,players=listOf(NearbyPlayer("host",cleanName(hostName),0,true)));private set
    private var questions:List<QuestionContent> = emptyList()
    private var deadline=0L
    fun join(id:String,name:String):Boolean {
        if(state.phase!="lobby"||state.players.size>=state.mode.capacity||state.players.any{it.id==id})return false
        state=state.copy(players=state.players+NearbyPlayer(id,cleanName(name),state.players.size%2));return true
    }
    fun leave(id:String){state=if(state.phase=="lobby")state.copy(players=state.players.filterNot{it.id==id}.mapIndexed{index,player->player.copy(team=index%2)})else state.copy(phase="disconnected")}
    fun ready(id:String,value:Boolean){if(state.phase=="lobby")state=state.copy(players=state.players.map{if(it.id==id)it.copy(ready=value)else it})}
    val canStart get()=state.phase=="lobby"&&state.players.size>=state.mode.minimum&&state.players.all{it.ready}
    fun start(content:List<QuestionContent>,now:Long):Boolean{
        if(!canStart||content.isEmpty())return false
        require(content.size<=15&&content.all{it.options.size in 2..10&&it.options.count{option->option.correct}==1})
        questions=content;state=state.copy(total=content.size);next(now);return true
    }
    private fun next(now:Long){
        if(state.round>=questions.size){state=state.copy(phase="finished",seconds=0);return}
        val question=questions[state.round];deadline=now+30_000
        state=state.copy(phase="question",round=state.round+1,prompt=question.prompt,options=question.options.map{NearbyOption(it.id,it.label)},seconds=30,correct=null,players=state.players.map{it.copy(answered=false)})
    }
    fun answer(id:String,round:Int,answer:String,now:Long){
        if(state.phase!="question"||round!=state.round||now>=deadline||state.players.none{it.id==id&&!it.answered}||state.options.none{it.id==answer})return
        val correct=questions[state.round-1].options.single{it.correct}.id==answer
        state=state.copy(players=state.players.map{if(it.id==id)it.copy(answered=true,score=it.score+if(correct)10 else 0)else it})
        if(state.players.all{it.answered})reveal(now)
    }
    private fun reveal(now:Long){deadline=now+3_000;state=state.copy(phase="reveal",seconds=3,correct=questions[state.round-1].options.single{it.correct}.id)}
    fun tick(now:Long){
        if(state.phase !in setOf("question","reveal"))return
        if(now>=deadline){if(state.phase=="question")reveal(now)else next(now)}
        else state=state.copy(seconds=((deadline-now+999)/1000).toInt())
    }
    companion object {fun cleanName(value:String)=value.filter{!it.isISOControl()}.trim().take(24).ifBlank{"Player"}}
}
