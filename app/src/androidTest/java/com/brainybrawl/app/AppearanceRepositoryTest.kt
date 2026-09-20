package com.brainybrawl.app

import androidx.test.platform.app.InstrumentationRegistry
import com.brainybrawl.app.feature.auth.*
import com.brainybrawl.app.feature.profile.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Test

class AppearanceRepositoryTest{
    private class Auth:AuthRepository{
        override val state=MutableStateFlow<AuthState>(AuthState.SignedIn("appearance-test",null,local=true))
        override suspend fun login(email:String,password:String)=AuthNotice.NONE
        override suspend fun register(username:String,email:String,password:String)=AuthNotice.NONE
        override suspend fun recover(email:String)=AuthNotice.NONE
        override suspend fun changePassword(password:String)=AuthNotice.NONE
        override suspend fun oauth(provider:AuthProvider)=AuthNotice.NONE
        override suspend fun callback(uri:String)=AuthNotice.NONE
        override suspend fun logout()=AuthNotice.NONE
    }
    private class Remote:AppearanceRemote{
        var selection:AppearanceSelection?=null;var fail=false;var calls=0;var afterSave:()->Unit={}
        override suspend fun read(owner:String)=selection
        override suspend fun save(owner:String,selection:AppearanceSelection,image:ByteArray){
            calls++;if(fail)error("offline")
            val bitmap=android.graphics.BitmapFactory.decodeByteArray(image,0,image.size)
            assertEquals(512,bitmap.width);assertEquals(512,bitmap.height);assertTrue(image.size<=350_000);bitmap.recycle()
            this.selection=selection;afterSave()
        }
    }
    @Test fun choicesPersistOfflineRetryAndRestoreFromServer()=runBlocking{
        val context=InstrumentationRegistry.getInstrumentation().targetContext;val auth=Auth();val remote=Remote()
        val repo=AppearanceRepository(context,auth,remote);repo.remove("appearance-test")
        try{
            assertFalse(repo.select("appearance-test",AppearanceSelection(12,7)));assertEquals(0,remote.calls)
            assertEquals(AppearanceSelection(12,7),AppearanceRepository(context,auth,remote).read("appearance-test"));assertTrue(repo.pending("appearance-test"))
            auth.state.value=AuthState.SignedIn("appearance-test",null);remote.fail=true;repo.refresh("appearance-test");assertTrue(repo.pending("appearance-test"))
            remote.fail=false;repo.refresh("appearance-test");assertFalse(repo.pending("appearance-test"));assertEquals(AppearanceSelection(12,7),remote.selection)
            repo.remove("appearance-test");repo.refresh("appearance-test");assertEquals(AppearanceSelection(12,7),repo.read("appearance-test"))
        }finally{repo.remove("appearance-test")}
    }
    @Test fun accountSwitchCannotApplyOldSaveToTheNewAccount()=runBlocking{
        val context=InstrumentationRegistry.getInstrumentation().targetContext;val auth=Auth();val remote=Remote();val repo=AppearanceRepository(context,auth,remote)
        auth.state.value=AuthState.SignedIn("appearance-test",null)
        remote.afterSave={auth.state.value=AuthState.SignedIn("appearance-other",null)}
        try{
            assertFalse(repo.select("appearance-test",AppearanceSelection(6,9)))
            assertTrue(repo.pending("appearance-test"));assertEquals(AppearanceSelection(),repo.read("appearance-other"))
            try{repo.select("appearance-test",AppearanceSelection());fail("Another account was edited")}catch(_:IllegalArgumentException){}
        }finally{repo.remove("appearance-test");repo.remove("appearance-other")}
    }
    @Test fun offlineAppearanceFollowsTheSameAccountWhenItConnects()=runBlocking{
        val context=InstrumentationRegistry.getInstrumentation().targetContext;val auth=Auth();val remote=Remote()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.Default)
        val repo=AppearanceRepository(context,auth,remote,scope,localOwner={"appearance-test"})
        try{
            repo.select("appearance-test",AppearanceSelection(4,8))
            auth.state.value=AuthState.SignedIn("appearance-server",null)
            withTimeout(5_000){while(remote.selection!=AppearanceSelection(4,8)||repo.pending("appearance-server"))delay(25)}
            assertEquals(AppearanceSelection(4,8),repo.read("appearance-server"));assertFalse(repo.pending("appearance-test"))
        }finally{scope.cancel();repo.remove("appearance-test");repo.remove("appearance-server")}
    }
    @Test fun suppliedFramesHaveTransparentOpeningsAndSquarePresentation(){
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        (1..20).forEach{index->
            val frame=AppearanceArt.frame(context,index)
            assertEquals(0,android.graphics.Color.alpha(frame.getPixel(256,256)))
            val opening=AppearanceArt.opening(frame)
            assertTrue(opening.width()>.4f&&opening.height()>.4f)
            assertTrue(opening.left>=0f&&opening.top>=0f&&opening.right<=1f&&opening.bottom<=1f)
            var visible=0
            for(y in 0 until 512 step 4)for(x in 0 until 512 step 4)if(android.graphics.Color.alpha(frame.getPixel(x,y))>200)visible++
            assertTrue("Frame $index must render its supplied border",visible>300)
            assertEquals(512,frame.width);assertEquals(512,frame.height);frame.recycle()
        }
        val avatar=AppearanceArt.avatar(context,1)
        assertEquals(255,android.graphics.Color.alpha(avatar.getPixel(0,0)));avatar.recycle()
    }
}
