package com.brainybrawl.app

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.LocalizedContent
import com.brainybrawl.app.ui.theme.BrainyBrawlTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ScreenArtworkUiTest {
    @get:Rule val rule=createComposeRule()
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    @Test fun homeBackdropHasSeventyFivePercentOpacity(){
        val bitmap=Bitmap.createBitmap(8,8,Bitmap.Config.ARGB_8888).apply{eraseColor(AndroidColor.rgb(40,120,200))}
        rule.setContent{Box(Modifier.fillMaxSize().background(Color.White)){ScreenBackdrop(bitmap,.75f)}}
        val captured=rule.onRoot().captureToImage().asAndroidBitmap()
        val pixel=captured.getPixel(captured.width/2,captured.height/2)
        assertEquals(94f,AndroidColor.red(pixel).toFloat(),2f)
        assertEquals(154f,AndroidColor.green(pixel).toFloat(),2f)
        assertEquals(214f,AndroidColor.blue(pixel).toFloat(),2f)
    }
    @Test fun packagedScenesLoadOnceAndSplashDisplaysLogoAndProgress(){
        val repository=ScreenArtRepository(context)
        runBlocking{repository.prepare()}
        val art=repository.state.value
        assertTrue(art.ready);assertEquals(1f,art.progress,0f)
        assertNotNull(art.splash);assertNotNull(art.home)
        runBlocking{repository.prepare()}
        assertSame(art,repository.state.value)
        rule.setContent{LocalizedContent("en"){BrainyBrawlTheme{StartupSplash(art.copy(progress=.55f,ready=false),false)}}}
        rule.onNodeWithText(context.getString(R.string.loading)).assertIsDisplayed()
        rule.onNodeWithTag("startup-splash").assertIsDisplayed()
        rule.onAllNodes(SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.ProgressBarRangeInfo)).assertCountEquals(1)
        java.io.FileOutputStream(java.io.File(context.getExternalFilesDir(null),"splash-art.png")).use{
            rule.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG,100,it)
        }
    }
}
