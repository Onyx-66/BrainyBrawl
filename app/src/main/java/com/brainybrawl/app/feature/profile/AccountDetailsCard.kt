package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.AppContainer
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.feature.auth.*

@Composable fun AccountDetailsCard(container:AppContainer,identity:AuthState.SignedIn,model:AuthViewModel,email:String?,providers:List<String>,password:()->Unit,logout:()->Unit){
    val ui by model.ui.collectAsStateWithLifecycle()
    val links by model.accounts.collectAsStateWithLifecycle()
    var editing by remember{mutableStateOf(false)}
    var address by remember(email){mutableStateOf(email.orEmpty())}
    BrawlPanel(Modifier.fillMaxWidth()){
        Text(stringResource(R.string.account),style=MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
            NavigationSymbol(NavSymbol.EMAIL)
            Column(Modifier.weight(1f)){
                Text(stringResource(R.string.email),style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Text(email?:stringResource(R.string.no_account_email),style=MaterialTheme.typography.bodyMedium)
            }
            val editLabel=stringResource(R.string.edit_email)
            IconButton({model.consumeNotice();editing=true},Modifier.semantics{contentDescription=editLabel}){NavigationSymbol(NavSymbol.EDIT)}
        }
        val linked=(providers+links.keys).distinct().filter{it in listOf("google","discord")}
        linked.forEach{provider->
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
                ProviderMark(if(provider=="google")AuthProvider.GOOGLE else AuthProvider.DISCORD)
                Column(Modifier.weight(1f)){
                    Text(stringResource(if(provider=="google")R.string.provider_google else R.string.provider_discord),style=MaterialTheme.typography.labelMedium)
                    Text(links[provider]?.takeIf{it.isNotBlank()}?:stringResource(R.string.connected),style=MaterialTheme.typography.bodyMedium)
                }
                NavigationSymbol(NavSymbol.CHECK)
            }
        }
        val available=listOf(AuthProvider.GOOGLE,AuthProvider.DISCORD).filter{it.name.lowercase() !in linked}
        if(available.isNotEmpty()){
            HorizontalDivider(color=MaterialTheme.colorScheme.outline.copy(alpha=.3f))
            Text(stringResource(R.string.connect_platform),style=MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                available.forEach{provider->OutlinedButton({model.link(provider)},Modifier.weight(1f),enabled=!ui.busy&&model.providerEnabled(provider)){
                    ProviderMark(provider);Spacer(Modifier.width(7.dp));Text(stringResource(if(provider==AuthProvider.GOOGLE)R.string.provider_google else R.string.provider_discord))
                }}
            }
            if(available.any{!model.providerEnabled(it)})Text(stringResource(R.string.providers_later),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(color=MaterialTheme.colorScheme.outline.copy(alpha=.3f))
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            FilledTonalButton(password,Modifier.weight(1f).fillMaxHeight().heightIn(min=64.dp),colors=ButtonDefaults.filledTonalButtonColors(containerColor=MaterialTheme.colorScheme.surfaceVariant,contentColor=MaterialTheme.colorScheme.onSurface)){
                Column(horizontalAlignment=Alignment.CenterHorizontally){NavigationSymbol(NavSymbol.PASSWORD);Text(stringResource(R.string.change_password),style=MaterialTheme.typography.labelMedium)}
            }
            AccountPrivacy(container,identity,Modifier.weight(1f).fillMaxHeight())
        }
        TextButton(logout,Modifier.fillMaxWidth(),colors=ButtonDefaults.textButtonColors(contentColor=MaterialTheme.colorScheme.error)){
            NavigationSymbol(NavSymbol.SIGN_OUT);Spacer(Modifier.width(10.dp));Text(stringResource(R.string.sign_out))
        }
        if(!editing&&ui.notice in setOf(AuthNotice.REQUEST_FAILED,AuthNotice.NETWORK_ERROR,AuthNotice.REAUTH_REQUIRED))Text(stringResource(if(ui.notice==AuthNotice.REAUTH_REQUIRED)R.string.finish_account_connection else R.string.request_failed),style=MaterialTheme.typography.bodySmall)
    }
    if(editing)AlertDialog(containerColor=MaterialTheme.colorScheme.surface,onDismissRequest={if(!ui.busy){editing=false;model.consumeNotice()}},title={Text(stringResource(R.string.edit_email))},
        text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
            OutlinedTextField(address,{address=it},Modifier.fillMaxWidth(),label={Text(stringResource(R.string.email))},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),enabled=!ui.busy)
            Text(stringResource(R.string.email_change_explanation),style=MaterialTheme.typography.bodySmall)
            if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth())
            if(ui.notice!=AuthNotice.NONE)Text(stringResource(when(ui.notice){AuthNotice.EMAIL_UPDATE_SENT->R.string.email_change_sent;AuthNotice.NETWORK_ERROR->R.string.network_error;AuthNotice.REAUTH_REQUIRED->R.string.finish_account_connection;AuthNotice.VERIFY_EMAIL->R.string.verify_email;else->R.string.request_failed}))
        }},confirmButton={TextButton({model.changeEmail(address)},enabled=!ui.busy&&AuthValidation.email(address)&&!address.equals(email,true)&&ui.notice!=AuthNotice.EMAIL_UPDATE_SENT){Text(stringResource(R.string.save))}},
        dismissButton={TextButton({editing=false;model.consumeNotice()},enabled=!ui.busy){Text(stringResource(R.string.close))}})
}
