package com.brainybrawl.app

import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.*
import org.junit.Assert.*
import org.junit.Test

class GameRulesTest {
    private val meta=ContentMeta("test","en",Approval.DEV_SAMPLE)
    private val question=QuestionContent(meta,"theme","question",(0..4).map{QuestionOption("o$it","option $it",it==2)},"explanation")
    @Test fun duelHasFifteenQuestionWindowsAndFiveImageWindows() {
        val windows=ModeRules.duelWindows(1000)
        assertEquals(20,windows.size)
        assertTrue(windows.take(15).all{it.opensAt-it.startsAt==10000L&&it.deadline-it.opensAt==20000L})
        assertTrue(windows.drop(15).all{it.opensAt==it.startsAt&&it.deadline-it.startsAt==30000L})
    }
    @Test fun firstCorrectOnlyAndNoDuplicateSubmissions() {
        val round=QuestionContest(question,RoundWindow(0,10000,30000),setOf("a","b"))
            .submit("a","o1",10000).submit("b","o2",10001)
        assertEquals(0,round.points["a"]);assertEquals(1,round.points["b"])
        assertThrows(IllegalArgumentException::class.java){round.submit("b","o2",10002)}
        assertThrows(IllegalArgumentException::class.java){QuestionContest(question,RoundWindow(0,10000,30000),setOf("a")).submit("a","o2",30000)}
    }
    @Test fun questionOnlyWindowAndWrongPlayerCannotScore() {
        val round=QuestionContest(question,RoundWindow(0,10000,30000),setOf("a"))
        assertThrows(IllegalArgumentException::class.java){round.submit("a","o2",9999)}
        assertThrows(IllegalArgumentException::class.java){round.submit("b","o2",10000)}
    }
    @Test fun allTwentyDuosContinueAndDraftRolesFollowConfirmedSchedule() {
        val teams=(1..20).map{"team$it"};assertEquals(teams,ModeRules.duoFinalists(teams))
        assertEquals(DuoDraft(1,AnswererRule.PLAYER_ONE),ModeRules.duoDraft(1))
        assertEquals(DuoDraft(10,AnswererRule.HIGHER_SCORING),ModeRules.duoDraft(10))
        assertEquals(DuoDraft(5,AnswererRule.PLAYER_TWO),ModeRules.duoDraft(15))
    }
    @Test fun squadRandomDrawSelectsOnlyExistingTeamsAndMembers() {
        val teams=(1..10).associate{"t$it" to (1..4).map{seat->"p${it}_$seat"}}
        val draw=drawSquadDraft(teams){size->size-1}
        assertTrue(draw.choosingTeam in teams)
        assertEquals(10,draw.answerers.size)
        draw.answerers.forEach{(team,player)->assertTrue(player in teams.getValue(team))}
    }
    @Test fun flamesOnlyForConfirmedCompetitiveWinners() {
        GameMode.entries.forEach{mode->assertEquals(0,ModeRules.flames(mode,false))}
        assertEquals(0,ModeRules.flames(GameMode.OFFLINE,true))
        listOf(GameMode.DUEL,GameMode.DUO,GameMode.SQUAD,GameMode.SOLO).forEach{assertEquals(1,ModeRules.flames(it,true))}
    }
    @Test fun precisionRewardsStreakAndDoesNotRewardRepeatedCrossing() {
        val config=PrecisionContent(meta,360.0,30.0,0.0,2.0,90.0)
        var round=PrecisionRound(config,RoundWindow(0,0,20000),0.0)
        round=round.tap(1);assertEquals(1,round.score)
        assertEquals(round,round.tap(2))
        round=round.tap(1001);assertEquals(3,round.score)
        round=round.tap(2001);assertEquals(6,round.score)
        round=round.tap(2500);assertEquals(0,round.streak);assertEquals(6,round.score)
    }
    @Test fun scrambleNormalizesWhitespaceAndCaseAndReducesLaterCorrectScore() {
        val content=ScrambleContent(meta,"theme","A B",setOf("A B"),10,6)
        val round=ScrambleRound(content,RoundWindow(0,0,15000),setOf("a","b"))
            .submit("a","  a  b  ",1).submit("b","A B",2)
        assertEquals(10,round.points["a"]);assertEquals(6,round.points["b"])
    }
    @Test fun sortEnforcesRelayAndRejectsStaleItemIndex() {
        val items=listOf(SortContent(meta,"theme","one","bucket"))
        val round=SpeedSortRound(items,listOf("a","b"),RoundWindow(0,0,90000)).submit("a",0,"bucket",1)
        assertEquals(1,round.score);assertEquals("b",round.activePlayer)
        assertThrows(IllegalArgumentException::class.java){round.submit("a",1,"bucket",2)}
        assertThrows(IllegalArgumentException::class.java){round.submit("b",0,"bucket",2)}
        assertEquals(0,round.advance(90000).streak)
    }
    @Test fun rouletteRerollsOnlyTiedEntities() {
        val values=ArrayDeque(listOf(10,10,3,5,8))
        val result=resolveRoulette(listOf("a","b","c")){values.removeFirst()}
        assertEquals(listOf("b","a","c"),result.ranks)
        assertEquals(listOf(10,5),result.history["a"]);assertEquals(listOf(3),result.history["c"])
    }
    @Test fun illegalMatchTransitionsAreRejected() {
        assertThrows(IllegalArgumentException::class.java){MatchProgress().transition(MatchStage.MATCH_RESULTS)}
        val state=MatchProgress().transition(MatchStage.COUNTDOWN).transition(MatchStage.PHASE_ACTIVE)
            .transition(MatchStage.PHASE_RESULTS).transition(MatchStage.NEXT_PHASE)
        assertEquals(1,state.phase);assertEquals(4,state.revision)
    }
    @org.junit.Test fun teamDraftAwardsEveryCorrectDesignatedPlayer(){
        val content=com.brainybrawl.app.game.content.QuestionContent(
            com.brainybrawl.app.game.content.ContentMeta("q","en",com.brainybrawl.app.game.content.Approval.APPROVED),"theme","question",
            (1..5).map{com.brainybrawl.app.game.content.QuestionOption("o$it","$it",it==1)},"answer")
        val game=com.brainybrawl.app.game.engine.QuestionContest(content,
            com.brainybrawl.app.game.engine.RoundWindow(0,15_000,35_000),setOf("a","b"),firstCorrectOnly=false)
            .submit("a","o1",16_000).submit("b","o1",34_999)
        org.junit.Assert.assertEquals(mapOf("a" to 1,"b" to 1),game.points)
    }
    @org.junit.Test fun smallerDuoDraftsKeepAllQuestionsAndCycleChoosers(){
        for(count in 1..20){
            val draws=(1..15).map{com.brainybrawl.app.game.engine.ModeRules.duoDraft(it,count)}
            org.junit.Assert.assertEquals(15,draws.size)
            org.junit.Assert.assertTrue(draws.all{it.chooserRank in 1..count})
            org.junit.Assert.assertEquals(com.brainybrawl.app.game.engine.AnswererRule.PLAYER_TWO,draws.last().answerer)
        }
        org.junit.Assert.assertEquals(1,com.brainybrawl.app.game.engine.ModeRules.duoDraft(7,3).chooserRank)
    }
    @Test fun rouletteSurvivesLongTieSequencesWithoutRecursiveOverflow(){
        var draws=0
        val result=resolveRoulette(listOf("a","b")){draws++;if(draws<=20_000)10 else if(draws==20_001)20 else 1}
        assertEquals(listOf("a","b"),result.ranks)
        assertEquals(10_001,result.history.getValue("a").size)
    }
}
