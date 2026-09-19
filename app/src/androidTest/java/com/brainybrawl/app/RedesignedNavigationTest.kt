package com.brainybrawl.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import com.brainybrawl.app.feature.settings.UserSettings
import org.junit.Rule
import org.junit.Test

class RedesignedNavigationTest {
    private val rule=createAndroidComposeRule<MainActivity>()
    private val app get()=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as BrainyBrawlApplication
    private var previous=UserSettings()
    @get:Rule val rules:org.junit.rules.RuleChain=org.junit.rules.RuleChain.outerRule(object:org.junit.rules.ExternalResource(){
        override fun before(){previous=app.container.settings.state.value;app.container.settings.update(UserSettings())}
        override fun after(){app.container.settings.update(previous)}
    }).around(rule)
    private fun text(id:Int,locale:String="en"):String{
        val config=android.content.res.Configuration(rule.activity.resources.configuration)
        config.setLocale(java.util.Locale.forLanguageTag(locale))
        return rule.activity.createConfigurationContext(config).getString(id)
    }
    private fun capture(name:String){
        val file=java.io.File(rule.activity.getExternalFilesDir(null),"redesign-$name.png")
        java.io.FileOutputStream(file).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
    }
    @Test fun guestCanBrowseHomeModesAndOpenEveryAccountOption(){
        rule.onNodeWithText(text(R.string.email)).assertIsDisplayed()
        capture("auth")
        rule.onNodeWithText(text(R.string.play_offline)).performScrollTo().performClick()
        capture("modes")
        rule.onNodeWithText(text(R.string.home)).performClick()
        capture("home")
        rule.onNodeWithText(text(R.string.profile)).performClick()
        rule.onNodeWithText(text(R.string.email)).assertIsDisplayed()
        rule.onNodeWithText(text(R.string.google)).performScrollTo().assertIsEnabled().performClick()
        rule.onNodeWithText(text(R.string.online_unavailable)).performScrollTo().assertIsDisplayed()
        rule.onNodeWithText(text(R.string.discord)).performScrollTo().assertIsEnabled()
        rule.onNodeWithText(text(R.string.register)).performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.username)).performScrollTo().assertIsDisplayed()
    }
    @Test fun arabicUiExplicitlyStartsEnglishOfflinePack(){
        rule.runOnIdle{app.container.settings.update(UserSettings(language="ar"))}
        rule.onNodeWithText(text(R.string.play_offline,"ar")).performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.play_english_questions,"ar")).performScrollTo().performClick()
        rule.waitUntil(10_000){rule.onAllNodesWithTag("offline-question").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("offline-question").assertIsDisplayed()
        capture("offline-ar")
        val choices=SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.Role,androidx.compose.ui.semantics.Role.Checkbox)
        rule.waitUntil(15_000){rule.onAllNodes(choices).fetchSemanticsNodes().size==5}
        rule.onAllNodes(choices)[0].performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.next_question,"ar")).performScrollTo().assertIsEnabled().performClick()
        rule.onNodeWithTag("offline-question").performScrollTo().assertIsDisplayed()
    }
}
