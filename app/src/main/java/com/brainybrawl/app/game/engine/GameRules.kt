package com.brainybrawl.app.game.engine

import com.brainybrawl.app.game.content.*

enum class GameMode(val capacity:Int,val teamSize:Int) { DUEL(2,1),DUO(40,2),SQUAD(40,4),SOLO(20,1),OFFLINE(1,1) }
enum class MatchStage { LOBBY,COUNTDOWN,PHASE_ACTIVE,PHASE_RESULTS,NEXT_PHASE,MATCH_RESULTS,CLOSED }
data class MatchProgress(val stage:MatchStage=MatchStage.LOBBY,val phase:Int=0,val revision:Long=0) {
    fun transition(next:MatchStage):MatchProgress {
        val allowed=when(stage) {
            MatchStage.LOBBY->setOf(MatchStage.COUNTDOWN,MatchStage.CLOSED)
            MatchStage.COUNTDOWN->setOf(MatchStage.PHASE_ACTIVE)
            MatchStage.PHASE_ACTIVE->setOf(MatchStage.PHASE_RESULTS)
            MatchStage.PHASE_RESULTS->setOf(MatchStage.NEXT_PHASE,MatchStage.MATCH_RESULTS)
            MatchStage.NEXT_PHASE->setOf(MatchStage.PHASE_ACTIVE)
            MatchStage.MATCH_RESULTS->setOf(MatchStage.CLOSED)
            MatchStage.CLOSED->emptySet()
        }
        require(next in allowed)
        return copy(stage=next,phase=phase+if(next==MatchStage.NEXT_PHASE)1 else 0,revision=revision+1)
    }
}
data class RoundWindow(val startsAt:Long,val opensAt:Long,val deadline:Long) {
    init{require(startsAt<=opensAt&&opensAt<deadline)}
    fun accepting(now:Long)=now>=opensAt&&now<deadline
    fun remaining(now:Long)=(deadline-now).coerceAtLeast(0)
}
object ModeRules {
    fun duelWindows(start:Long):List<RoundWindow> = (0 until 20).map { index ->
        val begins=start+index*30_000L
        RoundWindow(begins,begins+if(index<15)10_000 else 0,begins+30_000)
    }
    fun flames(mode:GameMode,winner:Boolean)=if(winner&&mode in setOf(GameMode.DUEL,GameMode.DUO,GameMode.SQUAD,GameMode.SOLO))1 else 0
    fun duoFinalists(rankedTeams:List<String>):List<String> = rankedTeams.toList()
    fun duoDraft(question:Int,teamCount:Int=20):DuoDraft {
        require(question in 1..15 && teamCount in 1..20)
        val scheduled=when(question){in 1..5->DuoDraft(question,AnswererRule.PLAYER_ONE)
            in 6..10->DuoDraft(question,AnswererRule.HIGHER_SCORING)
            else->DuoDraft(question-10,AnswererRule.PLAYER_TWO)}
        return scheduled.copy(chooserRank=(scheduled.chooserRank-1)%teamCount+1)
    }
    val squadPrecisionTurns=List(4){20_000L}
    const val PUZZLE_MILLIS=180_000L
    const val SQUAD_SORT_MILLIS=90_000L
    const val SELECTION_MILLIS=15_000L
    const val DUO_SCRAMBLE_MILLIS=15_000L
}
enum class AnswererRule{PLAYER_ONE,HIGHER_SCORING,PLAYER_TWO}
data class DuoDraft(val chooserRank:Int,val answerer:AnswererRule)
data class SquadDraftDraw(val choosingTeam:String,val answerers:Map<String,String>)
fun drawSquadDraft(teams:Map<String,List<String>>,nextInt:(Int)->Int):SquadDraftDraw {
    require(teams.isNotEmpty()&&teams.values.all{it.size==4&&it.toSet().size==4})
    val ordered=teams.keys.sorted()
    val chooser=ordered[nextInt(ordered.size)]
    return SquadDraftDraw(chooser,ordered.associateWith { team ->teams.getValue(team).let{it[nextInt(it.size)]} })
}

/** Local/reference scoring. Online callers submit actions and use server results. */
data class QuestionContest(val content:QuestionContent,val window:RoundWindow,val eligible:Set<String>,
    val submissions:Map<String,String> = emptyMap(),val points:Map<String,Int> = emptyMap(),val firstCorrect:String?=null,val firstCorrectOnly:Boolean=true) {
    fun submit(player:String,option:String,receivedAt:Long):QuestionContest {
        require(player in eligible&&window.accepting(receivedAt)&&player !in submissions)
        val answer=content.options.single{it.id==option}
        val score=if(answer.correct&&(!firstCorrectOnly||firstCorrect==null))1 else 0
        return copy(submissions=submissions+(player to option),points=points+(player to score),firstCorrect=if(score==1&&firstCorrect==null)player else firstCorrect)
    }
}
fun imageScore(content:ImageContent,selected:Set<String>):Int {
    require(selected.size==4&&selected.all{id->content.choices.any{it.id==id}})
    val policy=requireNotNull(content.scoringPolicy){"Image scoring needs an approved decision"}
    return content.choices.filter{it.id in selected&&(policy==ImageScoringPolicy.ALL_SELECTED||it.correct)}.sumOf{it.points}
}

data class PrecisionRound(val config:PrecisionContent,val window:RoundWindow,val zoneStart:Double,
    val rotation:Double=0.0,val anchorAt:Long=window.startsAt,val streak:Int=0,val score:Int=0,val lastHitPass:Long?=null) {
    fun angle(now:Long):Double=((position(now)%360)+360)%360
    private fun position(now:Long)=rotation+(now-anchorAt).coerceAtLeast(0)*(config.speed+streak*config.speedIncrement)/1000.0
    fun tap(now:Long):PrecisionRound {
        require(window.accepting(now)&&now>=anchorAt)
        val position=position(now)
        val relative=position-zoneStart
        val pass=kotlin.math.floor(relative/360).toLong()
        val within=((relative%360)+360)%360
        val hit=within<=minOf(config.maxZoneWidth,config.zoneWidth+streak*config.widthIncrement)
        if(hit&&lastHitPass==pass)return this // One award per crossing; no rapid-tap farming.
        val nextStreak=if(hit)streak+1 else 0
        return copy(rotation=position,anchorAt=now,streak=nextStreak,score=score+if(hit)nextStreak else 0,lastHitPass=if(hit)pass else lastHitPass)
    }
}

data class PuzzleBoard(val content:PuzzleContent,val window:RoundWindow,val leftPlayer:String,val rightPlayer:String,
    val placements:Set<String> = emptySet(),val playerScores:Map<String,Int> = emptyMap()) {
    fun place(player:String,pieceId:String,slotId:String,rotation:Int,now:Long):PuzzleBoard {
        require(window.accepting(now))
        val piece=content.pieces.single{it.id==pieceId}
        require(player==if(piece.side=="LEFT")leftPlayer else rightPlayer)
        if(pieceId in placements||slotId!=piece.slot||rotation!=piece.rotation)return this
        return copy(placements=placements+pieceId,playerScores=playerScores+(player to ((playerScores[player]?:0)+1)))
    }
}

fun normalizeAnswer(answer:String):String = java.text.Normalizer.normalize(answer,java.text.Normalizer.Form.NFKD)
    .filterNot{Character.getType(it)==Character.NON_SPACING_MARK.toInt()||it=='ـ'}
    .map{when(it){in '٠'..'٩'->'0'+(it-'٠');in '۰'..'۹'->'0'+(it-'۰');else->it}}.joinToString("")
    .trim().lowercase(java.util.Locale.ROOT).replace(Regex("\\s+")," ")

data class ScrambleRound(val content:ScrambleContent,val window:RoundWindow,val eligible:Set<String>,val points:Map<String,Int> = emptyMap()) {
    fun submit(player:String,answer:String,now:Long):ScrambleRound {
        require(window.accepting(now)&&player in eligible&&player !in points)
        if(normalizeAnswer(answer) !in content.acceptedAnswers.map(::normalizeAnswer))return this
        return copy(points=points+(player to if(points.isEmpty())content.fullPoints else content.reducedPoints))
    }
}

data class SpeedSortRound(val items:List<SortContent>,val players:List<String>,val window:RoundWindow,
    val index:Int=0,val score:Int=0,val streak:Int=0) {
    init{require(items.isNotEmpty()&&players.isNotEmpty()&&players.toSet().size==players.size)}
    val current:SortContent get()=items[index%items.size]
    val activePlayer:String get()=players[index%players.size]
    fun submit(player:String,itemIndex:Int,bucket:String,now:Long):SpeedSortRound {
        require(window.accepting(now)&&player==activePlayer&&itemIndex==index)
        val correct=bucket==current.bucket
        return copy(index=index+1,score=score+if(correct)1 else 0,streak=if(correct)streak+1 else 0)
    }
    fun advance(now:Long)=if(now>=window.deadline)copy(streak=0) else this
}

data class RouletteResult(val ranks:List<String>,val history:Map<String,List<Int>>)
fun resolveRoulette(entities:List<String>,roll:()->Int):RouletteResult {
    require(entities.isNotEmpty()&&entities.toSet().size==entities.size)
    val history=entities.associateWith{mutableListOf<Int>()}
    val pending=ArrayDeque<List<String>>()
    val ranked=mutableListOf<String>()
    pending.addLast(entities)
    while(pending.isNotEmpty()){
        val group=pending.removeFirst()
        if(group.size==1){ranked+=group.single();continue}
        val byRoll=group.groupBy{id->roll().also{require(it in 1..20);history.getValue(id).add(it)}}
        byRoll.toSortedMap().values.forEach{pending.addFirst(it)}
    }
    return RouletteResult(ranked,history.mapValues{it.value.toList()})
}
