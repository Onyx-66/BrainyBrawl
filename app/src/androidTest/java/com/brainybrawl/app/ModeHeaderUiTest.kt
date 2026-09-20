package com.brainybrawl.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import com.brainybrawl.app.feature.settings.UserSettings
import kotlinx.coroutines.runBlocking
import org.junit.*

class ModeHeaderUiTest{
 @get:Rule val rule=createAndroidComposeRule<MainActivity>()
 private val container get()=(rule.activity.application as BrainyBrawlApplication).container
 @Before fun setup(){
  check(BuildConfig.APPLICATION_ID.endsWith(".qa"))
  rule.waitUntil(15_000){rule.onAllNodesWithText(rule.activity.getString(R.string.play_offline)).fetchSemanticsNodes().isNotEmpty()}
  rule.runOnIdle{container.settings.update(UserSettings())}
  val suffix=java.util.UUID.randomUUID().toString().take(8)
  runBlocking{container.auth.register("Brawler.$suffix","ui.$suffix@example.invalid","TestPassword123")}
  rule.waitUntil(10_000){rule.onAllNodesWithText("Level 1").fetchSemanticsNodes().isNotEmpty()}
 }
 @After fun cleanup(){runBlocking{container.localAccounts.state.value.current?.id?.let{container.localAccounts.deleteCurrent(it);container.appearance.remove(it)};container.auth.logout()};container.settings.update(UserSettings())}
 private fun capture(name:String){val file=java.io.File(rule.activity.getExternalFilesDir(null),"mode-header-$name.png");java.io.FileOutputStream(file).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}}
 @Test fun correctArtAndOnlyActiveBottomLabelAreDisplayed(){
  rule.onNodeWithTag("nav-MODES").performClick()
  rule.onNodeWithTag("nav-label-MODES",useUnmergedTree=true).assertIsDisplayed()
  listOf("HOME","STORE","PROFILE").forEach{rule.onNodeWithTag("nav-label-$it",useUnmergedTree=true).assertIsNotDisplayed()}
  listOf("1v1","duo","squad","solo").forEach{rule.onNodeWithTag("mode-art-$it",useUnmergedTree=true).performScrollTo().assertIsDisplayed()}
  rule.onNodeWithTag("mode-DUEL").performScrollTo();rule.waitForIdle();capture("games")
  rule.onNodeWithTag("mode-DUO").performClick()
  rule.onNodeWithTag("nav-label-MODES",useUnmergedTree=true).assertIsDisplayed()
  rule.onNodeWithText("Create private room").assertIsDisplayed()
  rule.onNodeWithTag("nav-HOME").performClick();rule.onNodeWithTag("nav-label-HOME",useUnmergedTree=true).assertIsDisplayed()
 }
 @Test fun eachHeaderResourceOpensTheMatchingStoreSection(){
  listOf("COINS","GEMS","FLAMES").forEach{currency->
   rule.onNodeWithTag("header-$currency").assertIsDisplayed().performClick()
   rule.onNodeWithTag("offer-$currency-0").performScrollTo().assertIsDisplayed()
  }
 }
 @Test fun arabicAndLightLayoutsRetainAllControls(){
  rule.runOnIdle{container.settings.update(UserSettings(language="ar"))}
  rule.onNodeWithTag("nav-MODES").performClick();rule.onNodeWithTag("mode-SOLO").performScrollTo().assertIsDisplayed()
  rule.onNodeWithTag("mode-DUEL").performScrollTo();capture("arabic")
  rule.runOnIdle{container.settings.update(UserSettings(dark=false))}
  rule.onNodeWithTag("mode-DUEL").assertIsDisplayed();capture("light")
 }
 @Test fun modeCardsStaySquareAndHeaderButtonsMatchHeight(){
  rule.onNodeWithTag("nav-MODES").performClick()
  listOf("DUEL","DUO","SQUAD","SOLO").forEach{mode->
   rule.onNodeWithTag("mode-$mode").performScrollTo()
   val bounds=rule.onNodeWithTag("mode-$mode").fetchSemanticsNode().boundsInRoot
   Assert.assertEquals(bounds.width,bounds.height,1f)
  }
  val profile=rule.onNodeWithTag("header-profile").fetchSemanticsNode().boundsInRoot
  val settings=rule.onNodeWithTag("header-settings").fetchSemanticsNode().boundsInRoot
  Assert.assertEquals(profile.height,settings.height,1f)
 }
 @Test fun bluetoothEntryIsAvailableWithoutAServerAccount(){
  rule.onNodeWithTag("nav-MODES").performClick()
  rule.onNodeWithText("Bluetooth with friends").performScrollTo().performClick()
  rule.onNodeWithText("Bluetooth with friends").assertIsDisplayed()
  rule.onNodeWithText("Back to modes").performScrollTo().performClick()
  rule.onNodeWithTag("mode-DUEL").performScrollTo().assertIsDisplayed()
 }

}
