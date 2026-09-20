package com.brainybrawl.app.feature.profile

import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.AppContainer
import com.brainybrawl.app.feature.auth.*

@Composable fun LocalProfileScreen(container:AppContainer,authModel:AuthViewModel,friends:()->Unit,password:()->Unit,logout:()->Unit){
    val state by container.localAccounts.state.collectAsStateWithLifecycle()
    val user=state.current?:return
    val identity by container.auth.state.collectAsStateWithLifecycle()
    Text(stringResource(R.string.profile),modifier=androidx.compose.ui.Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Start,style=MaterialTheme.typography.headlineMedium)
    PlayerCard(container.appearance,user.id,user.username,1,number=user.playerNumber,openFriends=friends)
    (identity as? AuthState.SignedIn)?.let{AccountDetailsCard(container,it,authModel,user.email,listOf("email"),password,logout)}
}
