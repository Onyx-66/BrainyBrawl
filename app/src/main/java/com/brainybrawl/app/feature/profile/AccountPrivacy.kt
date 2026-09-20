package com.brainybrawl.app.feature.profile
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.core.design.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.R
import com.brainybrawl.app.core.AppContainer
import com.brainybrawl.app.feature.auth.AuthState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.*

@Composable fun AccountPrivacy(container:AppContainer,identity:AuthState.SignedIn,modifier:Modifier=Modifier){
 var confirm by remember{mutableStateOf(false)};var busy by remember{mutableStateOf(false)};var done by remember{mutableStateOf(false)};var failed by remember{mutableStateOf(false)}
 val scope=rememberCoroutineScope()
 Column(modifier){
 FilledTonalButton({confirm=true},Modifier.fillMaxWidth().heightIn(min=52.dp),enabled=!busy,colors=ButtonDefaults.filledTonalButtonColors(containerColor=MaterialTheme.colorScheme.error.copy(alpha=.14f),contentColor=MaterialTheme.colorScheme.error)){
  NavigationSymbol(NavSymbol.DELETE);Spacer(Modifier.width(10.dp));Text(stringResource(R.string.delete_account),maxLines=1)
 }
 if(done)Text(stringResource(R.string.deletion_requested))
 if(failed)Text(stringResource(R.string.request_failed))
 }
 if(confirm)AlertDialog(containerColor=MaterialTheme.colorScheme.surface,onDismissRequest={if(!busy)confirm=false},title={Text(stringResource(R.string.delete_account))},
  text={Text(stringResource(if(identity.local)R.string.delete_local_explanation else R.string.delete_online_explanation))},
  confirmButton={TextButton({if(!busy){busy=true;scope.launch{
   try{
    require(container.auth.state.value==identity)
    if(identity.local){
     withContext(NonCancellable + Dispatchers.IO){
      container.localAccounts.deleteCurrent(identity.userId)
      container.offlineStatistics.clear(identity.userId)
      container.avatars.removeLocal(identity.userId)
      container.appearance.remove(identity.userId)
     }
    }else{container.supabase!!.postgrest.rpc("request_account_deletion");done=true}
    confirm=false
   }catch(e:CancellationException){throw e}catch(_:Exception){failed=true}finally{busy=false}
  }}},enabled=!busy){Text(stringResource(R.string.confirm_action))}},
  dismissButton={TextButton({confirm=false},enabled=!busy){Text(stringResource(R.string.cancel))}})
}
