package com.brainybrawl.app.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*

@Composable
fun AuthScreen(viewModel: AuthViewModel, offline: () -> Unit, changePassword: Boolean = false,onlineOnly:Boolean=false) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    var form by rememberSaveable(changePassword) { mutableStateOf(if(changePassword) AuthForm.CHANGE_PASSWORD else AuthForm.LOGIN) }
    var local by rememberSaveable(onlineOnly){mutableStateOf(!onlineOnly&&!viewModel.onlineConfigured)}
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    // Passwords deliberately never enter saved instance state or a ViewModel property.
    var password by remember { mutableStateOf("") }
    var showPassword by remember{mutableStateOf(false)}
    val enabled=!state.busy
    val title=when(form) {
        AuthForm.LOGIN -> R.string.login
        AuthForm.REGISTER -> R.string.register
        AuthForm.RECOVER -> R.string.recover_password
        AuthForm.CHANGE_PASSWORD -> R.string.change_password
    }
    if(!changePassword)Text(stringResource(R.string.auth_welcome),style=MaterialTheme.typography.headlineLarge)
    if(changePassword||form==AuthForm.RECOVER)Text(stringResource(title),style=MaterialTheme.typography.headlineMedium)
    Text(stringResource(R.string.auth_description),Modifier.fillMaxWidth(),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
        androidx.compose.material3.Surface(shape=androidx.compose.foundation.shape.RoundedCornerShape(28.dp),color=MaterialTheme.colorScheme.surface,modifier=Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
                val fields=OutlinedTextFieldDefaults.colors(focusedContainerColor=MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor=MaterialTheme.colorScheme.surfaceVariant,focusedBorderColor=MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor=androidx.compose.ui.graphics.Color.Transparent)

                if(!changePassword&&form!=AuthForm.RECOVER){
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()){
                        listOf(AuthForm.LOGIN to R.string.login,AuthForm.REGISTER to R.string.register).forEachIndexed{index,(target,label)->
                            SegmentedButton(form==target,{form=target;password="";viewModel.consumeNotice()},shape=SegmentedButtonDefaults.itemShape(index,2)){Text(stringResource(label))}
                        }
                    }
                }
                if(!changePassword&&!onlineOnly){
                    Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){
                        Checkbox(local,{local=it;viewModel.consumeNotice()})
                        Text(stringResource(R.string.device_account))
                    }
                    if(local)Text(stringResource(R.string.device_account_description),style=MaterialTheme.typography.bodySmall)
                }

                if(form==AuthForm.REGISTER) OutlinedTextField(username,{username=it},Modifier.fillMaxWidth(),
                label={Text(stringResource(R.string.username))},singleLine=true,enabled=enabled,colors=fields,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                if(form!=AuthForm.CHANGE_PASSWORD) OutlinedTextField(email,{email=it},Modifier.fillMaxWidth(),
                label={Text(stringResource(R.string.email))},singleLine=true,enabled=enabled,colors=fields,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email))
                if(form!=AuthForm.RECOVER) OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),
                label={Text(stringResource(R.string.password))},singleLine=true,enabled=enabled,colors=fields,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                visualTransformation=if(showPassword)androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon={TextButton({showPassword=!showPassword}){Text(stringResource(if(showPassword)R.string.hide_password else R.string.show_password))}}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password))
                BrawlButton(stringResource(title),{
                    val submitted=password;password="";viewModel.submit(form,username,email,submitted,local)
                },Modifier.fillMaxWidth(),enabled=enabled)
                if(state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                if(state.notice!=AuthNotice.NONE&&state.notice!=AuthNotice.BACKEND_REQUIRED) Text(stringResource(state.notice.label()),color=MaterialTheme.colorScheme.onSurface)
            }
        }
    if(state.notice==AuthNotice.BACKEND_REQUIRED) FeedbackPanel(stringResource(R.string.online_unavailable),stringResource(R.string.backend_not_configured))
    if(!changePassword) {
        if(form==AuthForm.LOGIN) {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                ProviderButton(stringResource(R.string.google),"G",{viewModel.oauth(AuthProvider.GOOGLE)},enabled&&viewModel.providerEnabled(AuthProvider.GOOGLE),Modifier.weight(1f).fillMaxHeight())
                ProviderButton(stringResource(R.string.discord),"D",{viewModel.oauth(AuthProvider.DISCORD)},enabled&&viewModel.providerEnabled(AuthProvider.DISCORD),Modifier.weight(1f).fillMaxHeight())
            }
            if(!viewModel.providerEnabled(AuthProvider.GOOGLE)||!viewModel.providerEnabled(AuthProvider.DISCORD))Text(stringResource(R.string.providers_later),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            if(!local)TextButton(onClick={form=AuthForm.RECOVER;password=""}) { Text(stringResource(R.string.recover_password)) }
        }
        BrawlButton(stringResource(R.string.play_offline),offline,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
    }
}
private fun AuthNotice.label(): Int = when(this) {
    AuthNotice.VERIFY_EMAIL -> R.string.verify_email
    AuthNotice.RECOVERY_SENT -> R.string.recovery_sent
    AuthNotice.PASSWORD_RESET_READY -> R.string.change_password
    AuthNotice.PASSWORD_UPDATED -> R.string.password_updated
    AuthNotice.INVALID_INPUT -> R.string.invalid_auth_input
    AuthNotice.NETWORK_ERROR -> R.string.network_error
    AuthNotice.CALLBACK_REJECTED -> R.string.callback_rejected
    else -> R.string.auth_failed
}

@Composable private fun ProviderButton(label:String,symbol:String,onClick:()->Unit,enabled:Boolean,modifier:Modifier=Modifier){
    Button(onClick,modifier.heightIn(min=84.dp),enabled=enabled,
        shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.surfaceVariant,contentColor=MaterialTheme.colorScheme.onSurface,disabledContainerColor=MaterialTheme.colorScheme.surface,disabledContentColor=MaterialTheme.colorScheme.onSurfaceVariant)){
        Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
            Text(symbol,style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.secondary)
            Text(label,style=MaterialTheme.typography.labelLarge)
        }
    }
}
