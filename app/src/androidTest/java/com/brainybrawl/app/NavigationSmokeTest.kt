package com.brainybrawl.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class NavigationSmokeTest {
    private val rule = createAndroidComposeRule<MainActivity>()
    @get:Rule val rules:org.junit.rules.RuleChain = org.junit.rules.RuleChain.outerRule(object:org.junit.rules.ExternalResource(){
        override fun before(){
            val app=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as BrainyBrawlApplication
            app.container.settings.update(com.brainybrawl.app.feature.settings.UserSettings(language="en"))
        }
    }).around(rule)
    @org.junit.Before fun awaitStartup(){
        rule.waitUntil(15_000){rule.onAllNodes(androidx.compose.ui.test.hasText(rule.activity.getString(R.string.play_offline))).fetchSemanticsNodes().isNotEmpty()}
    }
    @Test fun offlineQuestionLoadsWithoutBackend() {
        rule.onNodeWithText(rule.activity.getString(R.string.play_offline)).performScrollTo().performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.offline_questions)).performScrollTo().performClick()
        rule.waitUntil(10_000) { rule.onAllNodes(androidx.compose.ui.test.hasText("Round 1 / 15")).fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Round 1 / 15").assertIsDisplayed()
        rule.onNodeWithText(rule.activity.getString(R.string.offline_no_flames)).assertIsDisplayed()
    }
    @Test fun freshInstallOffersOfflineMode() {
        rule.onNodeWithText(rule.activity.getString(R.string.play_offline)).performScrollTo().performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.choose_mode)).assertIsDisplayed()
        rule.onNodeWithText(rule.activity.getString(R.string.duel)).assertIsDisplayed()
    }
}
