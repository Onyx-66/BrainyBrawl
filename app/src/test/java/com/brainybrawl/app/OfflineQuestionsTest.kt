package com.brainybrawl.app
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.OfflineQuestions
import org.junit.Assert.*
import org.junit.Test
class OfflineQuestionsTest {
    private val question=QuestionContent(ContentMeta("q","en",Approval.APPROVED),"theme","prompt",
        (1..5).map{QuestionOption("o$it","Option $it",it==1)},"explanation")
    @Test fun answersAvailableImmediatelyAndDuplicateDoesNotScoreTwice(){
        val game=OfflineQuestions(listOf(question),startedAt=100)
        assertNull(game.answer("o1",99).selected)
        val answered=game.answer("o1",100)
        assertEquals(1,answered.score)
        assertEquals(answered,answered.answer("o2",11_000))
        assertTrue(answered.finished)
    }
    @Test fun deadlineExpiresIncludingBackgroundTime(){
        val game=OfflineQuestions(listOf(question),startedAt=100)
        assertEquals(0,game.answer("o1",45_100).score)
        assertTrue(game.tick(300_000).finished)
    }
    @Test fun acceptsExplicitTranslationsAndArabicDigits(){
        val multilingual=question.copy(acceptedAnswers=setOf("heart","cœur","قلب","96"))
        for(answer in listOf("HEART","cœur","قَلْب","٩٦","۹۶"))assertEquals(1,OfflineQuestions(listOf(multilingual),startedAt=0).answerText(answer,1).score)
        assertEquals(0,OfflineQuestions(listOf(multilingual),startedAt=0).answerText("liver",1).score)
    }
    @Test fun nextQuestionHasFreshWindowAndKeepsScore(){
        val game=OfflineQuestions(listOf(question,question),startedAt=0).answer("o1",10_000).next(90_000)
        assertEquals(1,game.index);assertEquals(1,game.score)
        assertEquals(90_000L,game.window.opensAt)
        assertEquals(135_000L,game.window.deadline)
        assertNull(game.selected);assertFalse(game.revealed)
    }
}
