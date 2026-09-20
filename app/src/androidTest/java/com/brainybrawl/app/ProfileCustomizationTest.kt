package com.brainybrawl.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import com.brainybrawl.app.feature.settings.UserSettings
import kotlinx.coroutines.runBlocking
import org.junit.*

class ProfileCustomizationTest{
    @get:Rule val rule=createAndroidComposeRule<MainActivity>()
    private val container get()=(rule.activity.application as BrainyBrawlApplication).container
    @Before fun start(){
        check(BuildConfig.APPLICATION_ID.endsWith(".qa"))
        rule.waitUntil(15_000){rule.onAllNodesWithText(rule.activity.getString(R.string.play_offline)).fetchSemanticsNodes().isNotEmpty()}
        val suffix=java.util.UUID.randomUUID().toString().take(8)
        runBlocking{container.auth.register("Avatar.$suffix","avatar.$suffix@example.invalid","TestPassword123")}
        rule.runOnIdle{container.settings.update(UserSettings())}
        rule.waitUntil(10_000){rule.onAllNodesWithText("Level 1").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Profile").performClick()
    }
    @After fun clean(){runBlocking{container.localAccounts.state.value.current?.id?.let{container.localAccounts.deleteCurrent(it);container.appearance.remove(it)};container.auth.logout()};container.settings.update(UserSettings())}
    private fun capture(name:String){val file=java.io.File(rule.activity.getExternalFilesDir(null),"profile-$name.png");java.io.FileOutputStream(file).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}}
    @Test fun suppliedAvatarsAndFramesSaveWithTransientFeedback(){
        rule.onAllNodesWithText("Player card").assertCountEquals(0)
        rule.onAllNodesWithText("Choose profile photo").assertCountEquals(0)
        listOf("level","gold","gems","flames").forEach{rule.onNodeWithTag("card-$it").assertIsDisplayed()}
        rule.onNode(hasText("Friends") and hasAnyAncestor(hasTestTag("player-card"))).assertIsDisplayed()
        rule.onNodeWithText("Avatars").performClick()
        rule.onNodeWithTag("avatar-10").performScrollTo().performClick()
        rule.onNodeWithText("Save").performClick()
        rule.waitUntil(5_000){container.appearance.read(container.localAccounts.state.value.current!!.id).avatar==10}
        rule.onNodeWithTag("appearance-notice").assertIsDisplayed()
        rule.mainClock.advanceTimeBy(3_100);rule.waitForIdle();rule.onAllNodesWithTag("appearance-notice").assertCountEquals(0)
        rule.onNodeWithText("Frames").performClick()
        rule.onNodeWithTag("frame-20").performScrollTo().performClick();rule.onNodeWithText("Save").performClick()
        rule.waitUntil(5_000){container.appearance.read(container.localAccounts.state.value.current!!.id).frame==20}
        capture("card")
    }
    @Test fun accountActionsAreGroupedAndDestructiveActionCanBeCancelled(){
        rule.onNodeWithText("Account").performScrollTo().assertIsDisplayed()
        rule.onNodeWithContentDescription("Edit email").performScrollTo().performClick()
        rule.onNodeWithText("Save").assertIsNotEnabled()
        rule.onNodeWithText("Close").performClick()
        rule.onNodeWithText("Connect another platform").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Google").performScrollTo().assertIsNotEnabled()
        rule.onNodeWithText("Discord").assertIsNotEnabled()
        rule.onNodeWithText("Delete account").performScrollTo().performClick()
        rule.onNodeWithText("Cancel").performClick()
        Assert.assertNotNull(container.localAccounts.state.value.current)
        rule.onNodeWithText("Sign out").performScrollTo().assertIsDisplayed();capture("account")
        rule.onNodeWithText("Change password").performScrollTo().performClick()
        rule.onNodeWithText("Password").assertIsDisplayed()
    }
    @Test fun arabicProfileUsesSameSquareAvatarAndReadableAccountControls(){
        rule.runOnIdle{container.settings.update(UserSettings(language="ar"))}
        rule.onNodeWithTag("player-avatar").assertIsDisplayed()
        val size=rule.onNodeWithTag("player-avatar").fetchSemanticsNode().boundsInRoot
        Assert.assertEquals(size.width,size.height,1f)
        capture("arabic")
        rule.onNodeWithText("الحساب").performScrollTo().assertIsDisplayed()
        rule.onNodeWithContentDescription("تعديل البريد الإلكتروني").performScrollTo().assertIsDisplayed()
    }
    @Test fun permanentIdIsVisibleAndCopyableInArabic(){
        val user=container.localAccounts.state.value.current!!
        runBlocking{container.localAccounts.rememberProfile(user.email,10000001,user.username)}
        rule.runOnIdle{container.settings.update(UserSettings(language="ar"))}
        rule.onNodeWithText("000010000001").assertIsDisplayed()
        rule.onNodeWithContentDescription("نسخ المعرّف").performClick()
        rule.onNodeWithText("الصور").assertIsDisplayed()
        capture("arabic-id")
    }

}
