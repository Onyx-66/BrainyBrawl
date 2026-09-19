package com.brainybrawl.app

import com.brainybrawl.app.game.content.*
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class ContentTest {
    private val parser = XmlContentParser()
    private val directory = File(requireNotNull(System.getProperty("brainybrawl.contentDir")))
    @Test fun allEightFilesMeetTheRuntimeContract() {
        val records = ContentKind.entries.flatMap { parser.parse(File(directory, "${it.fileName}.xml").inputStream(), it) }
        for(locale in listOf("en","fr","ar")){
            val questions=records.filterIsInstance<QuestionContent>().filter{it.meta.locale==locale}
            assertEquals(1225,questions.size)
            assertEquals(questions.size,questions.map{it.prompt}.toSet().size)
            assertTrue(questions.all{it.acceptedAnswers.isNotEmpty()})
            assertEquals(32,records.filterIsInstance<ReactionContent>().count{it.meta.locale==locale})
        }
        assertEquals(records.size, records.map { it.meta.id }.toSet().size)
        assertTrue(records.filterIsInstance<PuzzleContent>().all { it.pieces.size == 96 })
        assertTrue(records.filterIsInstance<ImageContent>().all { it.choices.size == 10 && it.scoringPolicy == ImageScoringPolicy.CORRECT_ONLY })
    }
    @Test(expected = IllegalArgumentException::class)
    fun externalEntityIsRejectedBeforeParsing() {
        parser.parse("<!DOCTYPE content SYSTEM 'file:///private'><content/>".byteInputStream(), ContentKind.QUESTION)
    }
    @Test(expected = IllegalArgumentException::class)
    fun duplicateAnswersAreRejected() {
        val text = File(directory,"question_round.xml").readText().replace("Q_EN_0001_O2", "Q_EN_0001_O1")
        parser.parse(text.byteInputStream(), ContentKind.QUESTION)
    }
    @Test(expected = IllegalArgumentException::class)
    fun multipleCorrectAnswersAreRejected() {
        val text = File(directory,"question_round.xml").readText().replaceFirst("correct=\"false\"", "correct=\"true\"")
        parser.parse(text.byteInputStream(), ContentKind.QUESTION)
    }
}
