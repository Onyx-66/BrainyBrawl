package com.brainybrawl.app.game.engine

import com.brainybrawl.app.game.content.PuzzleContent

/** Local practice owns all 96 pieces; no competitive writes or rewards. */
data class OfflinePuzzle(val puzzle:PuzzleContent,val startedAt:Long,val placed:Set<String> = emptySet(),val expired:Boolean=false,val wrongSlot:String?=null){
    init{require(puzzle.pieces.size==96&&puzzle.pieces.map{it.id}.toSet().size==96)}
    val window get()=RoundWindow(startedAt,startedAt,startedAt+ModeRules.PUZZLE_MILLIS)
    val finished get()=expired||placed.size==puzzle.pieces.size
    val score get()=placed.size
    fun tick(now:Long)=if(now>=window.deadline&&!finished)copy(expired=true)else this
    fun place(piece:String,slot:String,rotation:Int,now:Long):OfflinePuzzle{
        if(finished||!window.accepting(now))return tick(now)
        val candidate=puzzle.pieces.find{it.id==piece}?:return this
        if(piece in placed||puzzle.pieces.none{it.slot==slot}||rotation !in setOf(0,90,180,270))return this
        return if(candidate.slot==slot&&rotation==candidate.rotation)copy(placed=placed+piece,wrongSlot=null)else copy(wrongSlot=slot)
    }
}
