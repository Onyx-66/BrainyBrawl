package com.brainybrawl.app.feature.profile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.R
import com.brainybrawl.app.feature.auth.AuthValidation
@Composable fun UsernameDialog(original:String,dismiss:()->Unit,save:(String)->Unit){
 var value by remember{mutableStateOf(original)}
 AlertDialog(onDismissRequest=dismiss,title={Text(stringResource(R.string.username))},text={OutlinedTextField(value,{value=it},singleLine=true)},confirmButton={TextButton({save(value)},enabled=AuthValidation.username(value)){Text(stringResource(R.string.save))}},dismissButton={TextButton(dismiss){Text(stringResource(R.string.cancel))}})
}
