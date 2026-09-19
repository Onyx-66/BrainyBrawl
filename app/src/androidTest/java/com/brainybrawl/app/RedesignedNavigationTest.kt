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
        rule.onNodeWithText(text(R.string.google)).performScrollTo().assertIsNotEnabled()
        rule.onNodeWithText(text(R.string.providers_later)).performScrollTo().assertIsDisplayed()
        rule.onNodeWithText(text(R.string.discord)).performScrollTo().assertIsNotEnabled()
        rule.onNodeWithText(text(R.string.register)).performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.username)).performScrollTo().assertIsDisplayed()
    }
    @Test fun languageDropdownShowsFlagsAndAppliesFrenchAndArabic(){
        rule.onNodeWithContentDescription(text(R.string.settings)).performClick()
        rule.onNodeWithText("🇬🇧  English").performScrollTo().performClick()
        rule.onNodeWithText("🇫🇷  Français").performClick()
        rule.onNodeWithText("🇫🇷  Français").assertIsDisplayed()
        capture("settings-fr")
        rule.onNodeWithText("🇫🇷  Français").performClick()
        rule.onNodeWithText("🇸🇦  العربية").performClick()
        rule.onNodeWithText("🇸🇦  العربية").assertIsDisplayed()
        capture("settings-ar")
    }
    @Test fun arabicUiStartsLocalizedPackWithImmediateAnswers(){
        rule.runOnIdle{app.container.settings.update(UserSettings(language="ar"))}
        rule.onNodeWithText(text(R.string.play_offline,"ar")).performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.offline_questions,"ar")).performScrollTo().performClick()
        rule.waitUntil(10_000){rule.onAllNodesWithTag("offline-question").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithTag("offline-question").assertIsDisplayed()
        capture("offline-ar")
        val choices=SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.Role,androidx.compose.ui.semantics.Role.Checkbox)
        rule.waitUntil(5_000){rule.onAllNodes(choices).fetchSemanticsNodes().size==5}
        rule.onAllNodes(choices)[0].performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.next_question,"ar")).performScrollTo().assertIsEnabled().performClick()
        rule.onNodeWithTag("offline-question").performScrollTo().assertIsDisplayed()
    }
    @Test fun offlineImagesOfferTenChoicesAndRequireFourSelections(){
        rule.onNodeWithText(text(R.string.play_offline)).performScrollTo().performClick()
        rule.onNodeWithText(text(R.string.offline_images)).performScrollTo().performClick()
        rule.waitUntil(10_000){rule.onAllNodesWithTag("offline-image").fetchSemanticsNodes().isNotEmpty()}
        val choices=SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.Role,androidx.compose.ui.semantics.Role.Checkbox)
        rule.onAllNodes(choices).assertCountEquals(10)
        capture("offline-image")
        for(index in 0..3)rule.onAllNodes(choices)[index].performScrollTo().performClick()
        val confirm=text(R.string.confirm_four).replace("{count}","4")
        rule.onNodeWithText(confirm).performScrollTo().assertIsEnabled().performClick()
        rule.onNodeWithText(text(R.string.next_question)).performScrollTo().assertIsEnabled().performClick()
        rule.onAllNodes(choices).assertCountEquals(10)
    }
    @Test fun authCardFollowsBothThemes(){
        rule.runOnIdle{app.container.settings.update(UserSettings(dark=true))}
        rule.onNodeWithText(text(R.string.email)).assertIsDisplayed()
        capture("auth-dark")
        val dark=rule.onRoot().captureToImage().asAndroidBitmap()
        rule.runOnIdle{app.container.settings.update(UserSettings(dark=false))}
        rule.onNodeWithText(text(R.string.email)).assertIsDisplayed()
        capture("auth-light")
        val light=rule.onRoot().captureToImage().asAndroidBitmap()
        // The center of the form card, away from text, must switch surface luminance.
        fun luminance(bitmap:android.graphics.Bitmap):Int{
            val pixel=bitmap.getPixel(bitmap.width/2,bitmap.height/2)
            return android.graphics.Color.red(pixel)+android.graphics.Color.green(pixel)+android.graphics.Color.blue(pixel)
        }
        org.junit.Assert.assertTrue(luminance(light)>luminance(dark)+250)
    }
}
