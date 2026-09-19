package com.brainybrawl.app
import com.brainybrawl.app.game.content.*
import com.brainybrawl.app.game.engine.*
import org.junit.Assert.*
import org.junit.Test
class OfflineImagesTest{
 private val content=ImageContent(ContentMeta("image","en",Approval.APPROVED),"theme","spec","prompt","assets/image.svg",List(10){ImageChoice("c$it","Choice $it",if(it==0)10 else 8,it==0)},"explanation",ImageScoringPolicy.CORRECT_ONLY)
 private fun selected()=(0..3).fold(OfflineImages(listOf(content),1000)){game,n->game.select("c$n",1000)}
 @Test fun requiresFourDistinctChoicesAndCapsSelection(){
  val game=selected();assertEquals(4,game.selected.size);assertEquals(game,game.select("c4",1001))
  assertEquals(3,game.select("c3",1001).selected.size)
  try{game.select("c3",1001).confirm(1002);fail("Partial selection accepted")}catch(_:IllegalArgumentException){}
 }
 @Test fun onlyCorrectChoicesScoreAndConfirmationCannotRepeat(){
  val game=selected().confirm(1002);assertEquals(10,game.score);assertEquals(10,game.possible);assertTrue(game.finished)
  assertEquals(game,game.confirm(1003));assertEquals(game,game.select("c8",1003))
 }
 @Test fun deadlineNeverAwardsUnconfirmedSelections(){
  val expired=selected().confirm(31000);assertTrue(expired.revealed);assertFalse(expired.confirmed);assertEquals(0,expired.score)
  assertEquals(10,selected().confirm(30999).score)
 }
 @Test fun nextImageStartsFreshThirtySecondWindow(){
  val game=OfflineImages(listOf(content,content.copy(meta=content.meta.copy(id="other"))),1000).tick(31000).next(45000)
  assertEquals(1,game.index);assertEquals(75000,game.window.deadline);assertTrue(game.selected.isEmpty());assertFalse(game.revealed)
 }
}
