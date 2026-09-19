package com.brainybrawl.app
import com.brainybrawl.app.game.engine.*
import com.brainybrawl.app.game.content.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class OfflinePuzzleTest{
    private fun puzzle()=XmlContentParser().parse(File(System.getProperty("brainybrawl.contentDir"),"collaborative_puzzle.xml").inputStream(),ContentKind.PUZZLE)
        .filterIsInstance<PuzzleContent>().first{it.pieces.all{p->p.assetRef!=null}}
    @Test fun allNinetySixPiecesCanBePlacedOnceByOnePlayer(){
        var game=OfflinePuzzle(puzzle(),1000)
        game.puzzle.pieces.forEach{game=game.place(it.id,it.slot,it.rotation,1001)}
        assertEquals(96,game.score);assertTrue(game.finished);assertFalse(game.expired)
        val piece=game.puzzle.pieces.last()
        assertEquals(game,game.place(piece.id,piece.slot,piece.rotation,1002))
        assertEquals(0,ModeRules.flames(GameMode.OFFLINE,true))
    }
    @Test fun incorrectRotatedLateAndUnknownInputsCannotIncreaseScore(){
        val game=OfflinePuzzle(puzzle(),1000);val a=game.puzzle.pieces.first();val b=game.puzzle.pieces.last()
        assertEquals(0,game.place(a.id,b.slot,0,1001).score)
        assertEquals(0,game.place(a.id,a.slot,90,1001).score)
        assertEquals(game,game.place("unknown",a.slot,0,1001))
        assertEquals(180_000L,game.window.deadline-game.startedAt)
        assertEquals(1,game.place(a.id,a.slot,0,180_999).score)
        val expired=game.place(a.id,a.slot,0,181_000)
        assertTrue(expired.expired);assertEquals(0,expired.score)
        assertEquals(expired,expired.place(b.id,b.slot,0,181_001))
    }
}
