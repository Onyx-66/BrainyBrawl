package com.brainybrawl.app

import android.os.SystemClock
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import com.brainybrawl.app.core.localization.LocalizedContent
import com.brainybrawl.app.core.network.ServerClock
import com.brainybrawl.app.feature.auth.*
import com.brainybrawl.app.feature.match.*
import com.brainybrawl.app.ui.theme.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import org.junit.Rule
import org.junit.Test
import java.time.Instant

/** Real SQL snapshots rendered through the production screen; no test backend enters the app APK. */
class GameHudVisualTest {
    @get:Rule val rule=createComposeRule()
    private val json=Json{ignoreUnknownKeys=true;classDiscriminator="kind"}
    private class Auth(id:String):AuthRepository {
        override val state=MutableStateFlow<AuthState>(AuthState.SignedIn(id,null))
        override suspend fun login(email:String,password:String)=AuthNotice.REQUEST_FAILED
        override suspend fun register(username:String,email:String,password:String)=AuthNotice.REQUEST_FAILED
        override suspend fun recover(email:String)=AuthNotice.REQUEST_FAILED
        override suspend fun changePassword(password:String)=AuthNotice.REQUEST_FAILED
        override suspend fun oauth(provider:AuthProvider)=AuthNotice.REQUEST_FAILED
        override suspend fun callback(uri:String)=AuthNotice.REQUEST_FAILED
        override suspend fun logout()=AuthNotice.REQUEST_FAILED
    }
    private class SnapshotRepository(val snapshot:MatchSnapshot):MatchRepository {
        override val clock=ServerClock(SystemClock::elapsedRealtime).apply{
            val time=SystemClock.elapsedRealtime();sample(Instant.parse(snapshot.serverTime).toEpochMilli(),time,time)
        }
        override fun observe(id:String)=flowOf<MatchConnection>(MatchConnection.Live(snapshot))
        override suspend fun chooseTheme(match:String,question:Int,theme:String)=error("Read-only screenshot fixture")
        override suspend fun miniAction(match:String,round:String,key:String,action:JsonObject,relay:Boolean):MiniReceipt=error("Read-only screenshot fixture")
        override suspend fun cursor(match:String,round:String,x:Float,y:Float){}
        override suspend fun submit(match:String,round:String,key:String,action:JsonObject)=error("Read-only screenshot fixture")
    }
    private fun render(name:String,locale:String,scale:Float,assertions:()->Unit){
        val instrumentation=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val snapshot=instrumentation.context.assets.open("server/$name.json").bufferedReader().use{
            json.decodeFromString<MatchSnapshot>(it.readText())
        }
        val self=when(val board=snapshot.board){is PrecisionBoard->board.players.first().userId;is SortBoard->board.activeUser;else->snapshot.participants.first().userId}
        val model=MatchViewModel(SnapshotRepository(snapshot),Auth(self));val store=ViewModelStore();store.put("match",model)
        try{
            rule.setContent{LocalizedContent(locale){BrainyBrawlTheme{
                val density=LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density,scale)){
                    Surface(Modifier.fillMaxSize(),color=Navy){
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                            MatchScreen(model,snapshot.match.id,{})
                        }
                    }
                }
            }}}
            assertions()
            val file=java.io.File(instrumentation.targetContext.getExternalFilesDir(null),"hud-$name-$locale.png")
            java.io.FileOutputStream(file).use{rule.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
        }finally{rule.runOnIdle{store.clear()}}
    }
    @Test fun englishPrecisionHudKeepsTapActionVisible()=render("squad_initial","en",1f){
        rule.waitUntil(10_000){rule.onAllNodesWithText("Tap!").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Tap!").assertIsDisplayed()
    }
    @Test fun frenchSortHudSupportsLargeText()=render("squad_sort","fr",1.3f){
        rule.waitUntil(10_000){rule.onAllNodesWithText("A").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("A").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("B").assertIsDisplayed()
    }
    @Test fun arabicDuoDraftHasReadableWaitingState()=render("duo_draft","ar",1.3f){
        rule.waitUntil(10_000){rule.onAllNodesWithText("test").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("test").performScrollTo().assertIsDisplayed()
    }
    @Test fun soloPrecisionShowsOnlyOwnPlayableBoard()=render("solo_precision","en",1f){
        rule.waitUntil(10_000){rule.onAllNodesWithText("Tap!").fetchSemanticsNodes().isNotEmpty()}
        rule.onNodeWithText("Tap!").assertIsDisplayed().assertIsEnabled()
    }
}
