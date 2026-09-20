package com.brainybrawl.app
import com.brainybrawl.app.feature.nearby.*
import com.brainybrawl.app.feature.profile.PlayerId
import com.brainybrawl.app.game.content.*
import org.junit.Assert.*
import org.junit.Test
class NearbyGameTest {
    private val q=QuestionContent(ContentMeta("q","en",Approval.APPROVED),"nature","Question?",listOf(QuestionOption("a","Yes",true),QuestionOption("b","No",false)),"Explanation")
    @Test fun allModesRequireCorrectCapacityAndReadyPlayers(){NearbyMode.entries.forEach{mode->
        val game=NearbyGame(mode,"Host");assertFalse(game.start(listOf(q),0))
        repeat(mode.minimum-1){assertTrue(game.join("p$it","Player$it"))};assertFalse(game.start(listOf(q),0))
        repeat(mode.minimum-1){game.ready("p$it",true)};assertTrue(game.start(listOf(q),0));assertFalse(game.join("late","Late"))
    }}
    @Test fun hostScoresOnceAndRejectsStaleForgedAndLateAnswers(){
        val game=NearbyGame(NearbyMode.DUEL,"Host");game.join("peer","Peer");game.ready("peer",true);game.start(listOf(q,q),100)
        assertNull(game.state.correct)
        game.answer("intruder",1,"a",101);game.answer("peer",0,"a",101);game.answer("peer",1,"fake",101);assertEquals(0,game.state.players[1].score)
        game.answer("peer",1,"a",102);game.answer("peer",1,"a",103);assertEquals(10,game.state.players[1].score)
        game.answer("host",1,"b",104);assertEquals("reveal",game.state.phase);assertEquals("a",game.state.correct)
        game.tick(3104);assertEquals(2,game.state.round);assertNull(game.state.correct)
        game.answer("host",2,"a",33104);assertEquals(0,game.state.players[0].score)
        game.tick(33104);game.tick(36104);assertEquals("finished",game.state.phase)
    }
    @Test fun lobbyDepartureFreesSlotAndMatchDepartureEndsMatch(){
        val game=NearbyGame(NearbyMode.DUEL,"Host");game.join("a","A");assertFalse(game.join("b","B"));game.leave("a");assertTrue(game.join("b","B"));game.ready("b",true);game.start(listOf(q),0);game.leave("b");assertEquals("disconnected",game.state.phase)
    }
    @Test fun teamsAreBalanced(){val game=NearbyGame(NearbyMode.SQUAD,"Host");repeat(7){game.join("$it","P$it")};assertEquals(4,game.state.players.count{it.team==0});assertEquals(4,game.state.players.count{it.team==1})}
    @Test fun twelveDigitIdsRoundTripWithoutChangingServerIdentity(){assertEquals("000010000001",PlayerId.format(10000001));assertEquals("10000001",PlayerId.searchQuery("000010000001"));assertEquals("PlayerOne",PlayerId.searchQuery("PlayerOne"))}
}
