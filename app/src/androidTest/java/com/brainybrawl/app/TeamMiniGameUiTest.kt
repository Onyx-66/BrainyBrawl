package com.brainybrawl.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.core.localization.LocalizedContent
import com.brainybrawl.app.feature.match.*
import com.brainybrawl.app.ui.theme.BrainyBrawlTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import kotlinx.serialization.json.jsonObject

class TeamMiniGameUiTest {
    @get:Rule val rule=createComposeRule()
    private val start="2026-09-19T10:00:00Z"
    private fun round(kind:String)=MatchRound("round",0,"active",kind,start,start,"2026-09-19T10:01:20Z",RoundDisplay(letters="TAC"))
    @Test fun precisionAllowsOnlyTheActiveTeammate(){
        var taps=0
        val board=PrecisionBoard(PrecisionSettings(90.0,30.0,10.0,1.0,90.0),listOf(PrecisionPlayer("first",0.0,start,90.0,0,0)))
        rule.setContent { LocalizedContent("en") { BrainyBrawlTheme { Surface { Column {
            PrecisionGame(board,round("precision_tap"),listOf(MatchPlayer("first","First",0,true,seat=0),MatchPlayer("second","Second",0,true,seat=1)),"second",Instant.parse(start).toEpochMilli()+1000,true,{taps++})
        } } } } }
        rule.onNodeWithText("Tap!").assertIsNotEnabled()
        rule.runOnIdle { assertEquals(0,taps) }
    }
    @Test fun sortSubmitsTheVisibleItemAndChosenBucket(){
        var submitted:Pair<Int,String>?=null
        rule.setContent { LocalizedContent("en") { BrainyBrawlTheme { Surface { Column(Modifier.padding(16.dp)) {
            SpeedSortGame(SortBoard(7,2,"A test item","player",listOf("Category A","Category B")),true){index,bucket->submitted=index to bucket}
        } } } } }
        rule.onNodeWithText("Category B").performClick()
        rule.runOnIdle { assertEquals(7 to "Category B",submitted) }
    }
    @Test fun scrambleRejectsBlankInputAndSubmitsTypedAnswer(){
        var answer:String?=null
        rule.setContent { LocalizedContent("en") { BrainyBrawlTheme { Surface { Column {
            ScrambleGame(round("word_scramble"),true){answer=it}
        } } } } }
        rule.onNodeWithText("Submit answer").assertIsNotEnabled()
        rule.onNodeWithText("Your answer").performTextInput("cat")
        rule.onNodeWithText("Submit answer").performClick()
        rule.runOnIdle { assertEquals("cat",answer) }
    }
    @Test fun puzzleRendersPackagedArtAndSubmitsOwnedPiecePlacement(){
        val context=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val puzzle=com.brainybrawl.app.game.content.XmlContentParser().parse(context.assets.open("content/collaborative_puzzle.xml"),com.brainybrawl.app.game.content.ContentKind.PUZZLE).filterIsInstance<com.brainybrawl.app.game.content.PuzzleContent>().first{it.assetRef.endsWith(".png")}
        fun polygon(piece:com.brainybrawl.app.game.content.PuzzlePiece)=piece.polygon.joinToString(" "){"${it.x},${it.y}"}
        val board=PuzzleBoardView(puzzle.pieces.map{PuzzleTile(it.id,it.side,it.rotation,polygon(it),it.assetRef)},puzzle.pieces.map{PuzzleSlot(it.slot,polygon(it))},emptyList(),emptyList(),emptyList())
        val current=androidx.compose.runtime.mutableStateOf(board)
        var placement:Triple<String,String,Int>?=null
        rule.setContent { LocalizedContent("en") { BrainyBrawlTheme { Surface(Modifier.fillMaxSize()) { Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            PuzzleGame(current.value,puzzle.assetRef,0,"test-player",true,{piece,slot,rotation->placement=Triple(piece,slot,rotation)},{_,_->})
        } } } } }
        rule.waitUntil(10_000){rule.onAllNodesWithText("Choose a board slot").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Choose a board slot").assertIsDisplayed()
        val faded=rule.onNodeWithTag("puzzle-board").captureToImage().asAndroidBitmap()
        val original=com.brainybrawl.app.core.design.loadPackagedArtwork(context.assets,puzzle.assetRef)
        fun checkColor(rendered:android.graphics.Bitmap,column:Int,row:Int,opacity:Float){
            val u=(column+.5f)/12;val v=(row+.5f)/8
            val source=original.getPixel((u*original.width).toInt(),(v*original.height).toInt())
            val actual=rendered.getPixel((u*rendered.width).toInt(),(v*rendered.height).toInt())
            for(channel in listOf<(Int)->Int>(android.graphics.Color::red,android.graphics.Color::green,android.graphics.Color::blue)){
                assertEquals("Image opacity",channel(source)*opacity+255*(1-opacity),channel(actual).toFloat(),18f)
            }
        }
        checkColor(faded,0,0,.6f);checkColor(faded,5,2,.6f);checkColor(faded,11,6,.6f)
        rule.runOnIdle{current.value=board.copy(placements=listOf(PuzzlePlacement(puzzle.pieces.first().id,puzzle.pieces.first().slot,"test-player")))}
        checkColor(rule.onNodeWithTag("puzzle-board").captureToImage().asAndroidBitmap(),0,0,1f)
        rule.runOnIdle{current.value=board}
        val screenshot=rule.onRoot().captureToImage()
        val file=java.io.File(context.getExternalFilesDir(null),"puzzle-ui.png")
        java.io.FileOutputStream(file).use{screenshot.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
        rule.onNodeWithText("Choose a board slot").performClick()
        rule.onNodeWithText("Slot 1").performClick()
        rule.runOnIdle{
            val submitted=requireNotNull(placement)
            assertEquals("LEFT",puzzle.pieces.single{it.id==submitted.first}.side)
            assertEquals(puzzle.pieces.first().slot,submitted.second)
            assertEquals(0,submitted.third)
        }
    }
    @Test fun imageTapKeepsViewerOpenAndCloseButtonDismisses(){
        var closed=false
        rule.setContent { BrainyBrawlTheme {
            com.brainybrawl.app.core.design.ImageViewer("Close",{closed=true}){androidx.compose.material3.Text("Artwork")}
        } }
        rule.onNodeWithText("Artwork").performClick()
        rule.runOnIdle{assertFalse(closed)}
        rule.onNodeWithText("Close").performClick()
        rule.runOnIdle{assertTrue(closed)}
    }
    @Test fun hiddenImageWeightsAppearOnlyAfterServerClosesRound(){
        val content=RoundDisplay(options=(1..10).map{RoundOption("o$it","Choice $it")})
        val reveal=kotlinx.serialization.json.Json.parseToJsonElement("""{"choices":[{"id":"o1","correct":true,"points":5}],"explanation":"Verified answer"}""").jsonObject
        val current=androidx.compose.runtime.mutableStateOf(round("image_guess").copy(content=content,reveal=reveal))
        rule.setContent{LocalizedContent("en"){BrainyBrawlTheme{Surface{RoundReveal(current.value)}}}}
        rule.onAllNodesWithText("Choice 1 · 5 points").assertCountEquals(0)
        rule.runOnIdle{current.value=current.value.copy(status="results")}
        rule.onNodeWithText("Choice 1 · 5 points").assertIsDisplayed()
        rule.onNodeWithText("Verified answer").assertIsDisplayed()
    }
}
