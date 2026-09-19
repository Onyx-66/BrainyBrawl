package com.brainybrawl.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.core.localization.LocalizedContent
import androidx.lifecycle.ViewModelProvider
import com.brainybrawl.app.core.navigation.BrawlApp
import com.brainybrawl.app.feature.auth.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authModel by lazy {
        ViewModelProvider(this,AuthViewModel.factory((application as BrainyBrawlApplication).container.auth))[AuthViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        processCallback(intent)
        setContent {
            val settings by (application as BrainyBrawlApplication).container.settings.state.collectAsStateWithLifecycle()
            LocalizedContent(settings.language){ BrawlApp(authModel) }
        }
    }
    override fun onNewIntent(intent: Intent) { super.onNewIntent(intent); processCallback(intent) }
    private fun processCallback(intent: Intent) {
        val uri=intent.dataString ?: return
        intent.data=null
        authModel.callback(uri)
    }
}
