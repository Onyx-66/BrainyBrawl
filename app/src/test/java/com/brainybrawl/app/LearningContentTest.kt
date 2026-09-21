package com.brainybrawl.app
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.*
import java.io.File
import org.junit.Assert.*
import org.junit.Test
class LearningContentTest{
 private val dir=File(requireNotNull(System.getProperty("brainybrawl.contentDir")))
 @Test fun suppliedExamplesAndAllThreeLanguagesAreAccepted(){
  val questions=XmlContentParser().parse(File(dir,"question_round.xml").inputStream(),ContentKind.QUESTION).filterIsInstance<QuestionContent>()
  fun accepts(id:String,input:String):Boolean{val q=questions.single{it.meta.id=="PACK250_${id}_AR"};return AnswerMatcher.matches(input,q.acceptedAnswers,q.options.filterNot{it.correct}.map{it.label})}
  assertTrue(accepts("Q001","westfalia"));assertTrue(accepts("Q002","mali"));assertTrue(accepts("Q004","constantine"))
  assertTrue(accepts("Q087","eight"));assertTrue(accepts("Q087","8"));assertTrue(accepts("Q087","٨"));assertTrue(accepts("Q087","eiyt"));assertTrue(accepts("Q087","huit"));assertTrue(accepts("Q087","ثمانية"))
  assertTrue(accepts("Q127","5"));assertTrue(accepts("Q148","12"));assertFalse(accepts("Q087","ثمانين"))
  assertTrue(accepts("Q151","Marquez"));assertTrue(accepts("Q151","Gabriel Garcia Marqez"))
  assertFalse(accepts("Q087","eighty"));assertFalse(accepts("Q087","eighteen"));assertFalse(accepts("Q087","9"));assertFalse(accepts("Q006","1067"))
  assertFalse(accepts("Q002","Bali"));assertFalse(accepts("Q001","I have no idea"));assertFalse(accepts("Q053","V"))
 }
 @Test fun repeatedImageDrawsNeverMislabelUnselectedCorrectAnswers(){
  val pools=XmlContentParser().parse(File(dir,"image_guess.xml").inputStream(),ContentKind.IMAGE).filterIsInstance<ImageContent>().filter{it.meta.approval==Approval.APPROVED}
  assertEquals(60,pools.size)
  pools.forEach{pool->val positives=pool.choices.filter{it.correct}.map{it.id}.toSet();val draws=mutableSetOf<Set<String>>()
   repeat(100){seed->val round=pool.roundChoices(kotlin.random.Random(seed));assertEquals(10,round.choices.size);assertEquals(4,round.choices.count{it.correct});assertEquals(10,round.choices.map{it.label}.distinct().size)
    assertTrue(round.choices.filterNot{it.correct}.none{it.id in positives});draws.add(round.choices.map{it.id}.toSet())}
   assertTrue(draws.size>1)
  }
 }
 @Test fun adjacentSolvedPiecesLoseTheirSharedBorder(){
  val slots=mapOf("a" to listOf(Point(0f,0f),Point(.5f,0f),Point(.5f,1f),Point(0f,1f)),"b" to listOf(Point(.5f,0f),Point(1f,0f),Point(1f,1f),Point(.5f,1f)))
  assertEquals(7,puzzleEdges(slots,emptySet()).size)
  assertEquals(4,puzzleEdges(slots,setOf("a")).count{it.solved})
  val completed=puzzleEdges(slots,setOf("a","b"));assertEquals(6,completed.size);assertTrue(completed.all{it.solved});assertFalse(completed.any{it.from.x==.5f&&it.to.x==.5f})
 }
}
