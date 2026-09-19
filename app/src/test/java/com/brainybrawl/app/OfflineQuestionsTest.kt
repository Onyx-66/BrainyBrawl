package com.brainybrawl.app
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.OfflineQuestions
import org.junit.Assert.*
import org.junit.Test
class OfflineQuestionsTest {
    private val question=QuestionContent(ContentMeta("q","en",Approval.APPROVED),"theme","prompt",
        (1..5).map{QuestionOption("o$it","Option $it",it==1)},"explanation")
    @Test fun readingRejectsAndDuplicateDoesNotScoreTwice(){
        val game=OfflineQuestions(listOf(question),startedAt=100)
        assertNull(game.answer("o1",10_099).selected)
        val answered=game.answer("o1",10_100)
        assertEquals(1,answered.score)
        assertEquals(answered,answered.answer("o2",11_000))
        assertTrue(answered.finished)
    }
    @Test fun deadlineExpiresIncludingBackgroundTime(){
        val game=OfflineQuestions(listOf(question),startedAt=100)
        assertEquals(0,game.answer("o1",30_100).score)
        assertTrue(game.tick(300_000).finished)
    }
    @Test fun nextQuestionHasFreshWindowAndKeepsScore(){
        val game=OfflineQuestions(listOf(question,question),startedAt=0).answer("o1",10_000).next(90_000)
        assertEquals(1,game.index);assertEquals(1,game.score)
        assertEquals(100_000L,game.window.opensAt)
        assertNull(game.selected);assertFalse(game.revealed)
    }
}
