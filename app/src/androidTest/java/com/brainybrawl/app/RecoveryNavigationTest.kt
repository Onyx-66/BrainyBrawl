package com.brainybrawl.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModelStore
import com.brainybrawl.app.core.navigation.BrawlApp
import com.brainybrawl.app.feature.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class RecoveryNavigationTest {
    @get:Rule val rule=createComposeRule()
    private class Auth:AuthRepository {
        override val state=MutableStateFlow<AuthState>(AuthState.SignedOut)
        override suspend fun login(email:String,password:String)=AuthNotice.NONE
        override suspend fun register(username:String,email:String,password:String)=AuthNotice.NONE
        override suspend fun recover(email:String)=AuthNotice.RECOVERY_SENT
        override suspend fun changePassword(password:String)=AuthNotice.PASSWORD_UPDATED
        override suspend fun oauth(provider:AuthProvider)=AuthNotice.NONE
        override suspend fun logout()=AuthNotice.NONE
        override suspend fun callback(uri:String):AuthNotice{
            state.value=AuthState.SignedIn("test-user",null)
            return AuthNotice.PASSWORD_RESET_READY
        }
    }
    @Test fun verifiedRecoveryOpensPasswordFormInsteadOfHome(){
        val app=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as BrainyBrawlApplication
        app.container.settings.update(com.brainybrawl.app.feature.settings.UserSettings(language="en"))
        val model=AuthViewModel(Auth());val store=ViewModelStore();store.put("auth",model)
        try{
            rule.setContent{BrawlApp(model)}
            rule.waitForIdle()
            rule.runOnIdle{model.callback("test-only-verified-callback")}
            rule.waitUntil(10_000){rule.onAllNodesWithText("Change password").fetchSemanticsNodes().size>=2}
            rule.onNodeWithText("Password").assertIsDisplayed()
            rule.onAllNodesWithText("Change password").assertCountEquals(2)
            rule.onAllNodesWithText("Email").assertCountEquals(0)
        }finally{rule.runOnIdle{store.clear()}}
    }
}
