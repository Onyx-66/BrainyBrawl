package com.brainybrawl.app.core.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.BuildConfig
import com.brainybrawl.app.R

fun approvedWebLink(value:String):Boolean=runCatching {
    val uri=java.net.URI(value)
    uri.scheme=="https"&&!uri.host.isNullOrBlank()&&uri.userInfo==null
}.getOrDefault(false)

@Composable fun LegalLinks(){
    val context=LocalContext.current
    listOf(BuildConfig.PRIVACY_POLICY_URL to R.string.privacy_policy,BuildConfig.TERMS_URL to R.string.terms_of_service).forEach{(url,label)->
        if(approvedWebLink(url))TextButton({
            runCatching{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))}
        }){Text(stringResource(label))}
    }
}
