package com.brainybrawl.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import com.brainybrawl.app.feature.settings.UserSettings
import kotlinx.coroutines.runBlocking
import org.junit.*

class LocalAccountNavigationTest {
    @get:Rule val rule=createAndroidComposeRule<MainActivity>()
    private val container get()=(rule.activity.application as BrainyBrawlApplication).container
    @After fun cleanup(){runBlocking{container.auth.logout()}}
    @org.junit.Before fun awaitStartup(){
        rule.waitUntil(15_000){rule.onAllNodes(androidx.compose.ui.test.hasText(rule.activity.getString(R.string.play_offline))).fetchSemanticsNodes().isNotEmpty()}
    }
    @Test fun registerQueueAndReconnectKeepLocalIdentity(){
        val name="Device."+java.util.UUID.randomUUID().toString().take(8)
        rule.runOnIdle{container.settings.update(UserSettings())}
        rule.onNodeWithText("Create account").performScrollTo().performClick()
        rule.onNodeWithText("Username").performScrollTo().performTextInput(name)
        rule.onNodeWithText("Email").performScrollTo().performTextInput(name+"@example.invalid")
        rule.onNodeWithText("Password").performScrollTo().performTextInput("TestPassword123")
        rule.onAllNodesWithText("Create account").filter(hasClickAction()).onLast().performScrollTo().performClick()
        rule.waitUntil(20_000){rule.onAllNodesWithText("Level 1").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Level 1").assertIsDisplayed()
        rule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        rule.onAllNodesWithText("Store").filter(hasClickAction()).onLast().performClick()
        rule.onNodeWithText("Account connection").assertIsDisplayed()
        rule.onNodeWithText("Profile").performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.avatars)).performScrollTo().assertIsDisplayed()
        rule.onNodeWithText(rule.activity.getString(R.string.frames)).assertIsDisplayed()
        rule.onAllNodesWithText(rule.activity.getString(R.string.upload_photo)).assertCountEquals(0)
        val profileFile=java.io.File(rule.activity.getExternalFilesDir(null),"profile-bento.png")
        java.io.FileOutputStream(profileFile).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
        rule.onNodeWithText("Friends").performScrollTo().performClick()
        rule.onNodeWithText("Username, email or Player ID").performTextInput("Friend.Test")
        rule.onNodeWithText("Queue friend request").performScrollTo().performClick()
        rule.onNodeWithText("Waiting to send").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Connect online account").performScrollTo().performClick()
        rule.onNodeWithText("Google").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText(name).assertIsDisplayed()
        rule.onNodeWithText("Play offline").performScrollTo().performClick()
        val file=java.io.File(rule.activity.getExternalFilesDir(null),"bento-account.png")
        java.io.FileOutputStream(file).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
    }
}
