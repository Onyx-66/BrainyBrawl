package com.brainybrawl.app.core.design

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.R
import com.brainybrawl.app.feature.auth.*

@Composable fun AccountConnectionPanel(identity:AuthState,connected:Boolean,busy:Boolean,notice:AuthNotice,retry:()->Unit,signIn:()->Unit){
    if(identity is AuthState.SignedIn&&!identity.local&&connected)return
    BrawlPanel(Modifier.fillMaxWidth()){
        Text(stringResource(R.string.account_connection),style=MaterialTheme.typography.titleMedium)
        Text(stringResource(when{
            !connected->R.string.offline_connection
            busy->R.string.account_connecting
            identity !is AuthState.SignedIn->R.string.online_account_required
            notice==AuthNotice.VERIFY_EMAIL->R.string.verify_email
            notice==AuthNotice.BACKEND_REQUIRED->R.string.backend_not_configured
            notice==AuthNotice.REAUTH_REQUIRED->R.string.finish_account_connection
            notice==AuthNotice.REQUEST_FAILED->R.string.auth_failed
            else->R.string.account_sync_info
        }))
        val needsLogin=identity !is AuthState.SignedIn||notice in setOf(AuthNotice.REAUTH_REQUIRED,AuthNotice.REQUEST_FAILED)
        BrawlButton(stringResource(if(needsLogin)R.string.login else R.string.retry),if(needsLogin)signIn else retry,enabled=connected&&!busy)
    }
}
