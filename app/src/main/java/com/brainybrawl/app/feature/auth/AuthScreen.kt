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
fun AuthScreen(viewModel: AuthViewModel, offline: () -> Unit, changePassword: Boolean = false) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    var form by rememberSaveable(changePassword) { mutableStateOf(if(changePassword) AuthForm.CHANGE_PASSWORD else AuthForm.LOGIN) }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    // Passwords deliberately never enter saved instance state or a ViewModel property.
    var password by remember { mutableStateOf("") }
    val enabled=!state.busy
    val fields=OutlinedTextFieldDefaults.colors(focusedContainerColor=MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor=MaterialTheme.colorScheme.surfaceVariant,focusedBorderColor=com.brainybrawl.app.ui.theme.Cyan,
        unfocusedBorderColor=androidx.compose.ui.graphics.Color.Transparent)
    val title=when(form) {
        AuthForm.LOGIN -> R.string.login
        AuthForm.REGISTER -> R.string.register
        AuthForm.RECOVER -> R.string.recover_password
        AuthForm.CHANGE_PASSWORD -> R.string.change_password
    }
    if(!changePassword)Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.Center){BrainMark(Modifier.size(56.dp))}
    Text(stringResource(title),Modifier.fillMaxWidth(),style=MaterialTheme.typography.headlineMedium,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
    Text(stringResource(R.string.auth_description),Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    BrawlPanel(Modifier.fillMaxWidth()) {
        if(form==AuthForm.REGISTER) OutlinedTextField(username,{username=it},Modifier.fillMaxWidth(),
            label={Text(stringResource(R.string.username))},singleLine=true,enabled=enabled,colors=fields,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
        if(form!=AuthForm.CHANGE_PASSWORD) OutlinedTextField(email,{email=it},Modifier.fillMaxWidth(),
            label={Text(stringResource(R.string.email))},singleLine=true,enabled=enabled,colors=fields,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email))
        if(form!=AuthForm.RECOVER) OutlinedTextField(password,{password=it},Modifier.fillMaxWidth(),
            label={Text(stringResource(R.string.password))},singleLine=true,enabled=enabled,colors=fields,shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            visualTransformation=PasswordVisualTransformation(), keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password))
        BrawlButton(stringResource(title),{
            val submitted=password;password="";viewModel.submit(form,username,email,submitted)
        },Modifier.fillMaxWidth(),enabled=enabled)
        if(state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        if(state.notice!=AuthNotice.NONE&&state.notice!=AuthNotice.BACKEND_REQUIRED) Text(stringResource(state.notice.label()),color=MaterialTheme.colorScheme.onSurface)
    }
    if(state.notice==AuthNotice.BACKEND_REQUIRED) FeedbackPanel(stringResource(R.string.online_unavailable),stringResource(R.string.backend_not_configured))
    if(!changePassword) {
        if(form==AuthForm.LOGIN) {
            ProviderButton(stringResource(R.string.google),"G",{viewModel.oauth(AuthProvider.GOOGLE)},enabled)
            ProviderButton(stringResource(R.string.discord),"D",{viewModel.oauth(AuthProvider.DISCORD)},enabled)
            TextButton(onClick={form=AuthForm.RECOVER;password=""}) { Text(stringResource(R.string.recover_password)) }
        }
        TextButton(onClick={form=if(form==AuthForm.LOGIN) AuthForm.REGISTER else AuthForm.LOGIN;password=""}) {
            Text(stringResource(if(form==AuthForm.LOGIN) R.string.register else R.string.login))
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

@Composable private fun ProviderButton(label:String,symbol:String,onClick:()->Unit,enabled:Boolean){
    Button(onClick,Modifier.fillMaxWidth().heightIn(min=48.dp),enabled=enabled,
        shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors=ButtonDefaults.buttonColors(containerColor=androidx.compose.ui.graphics.Color(0xFFE9F3FF),contentColor=com.brainybrawl.app.ui.theme.Ink)){
        Text(symbol,Modifier.padding(end=12.dp),style=MaterialTheme.typography.titleLarge,color=androidx.compose.ui.graphics.Color(0xFF5261DE))
        Text(label,style=MaterialTheme.typography.labelLarge)
    }
}
