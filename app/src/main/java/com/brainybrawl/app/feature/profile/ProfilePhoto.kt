package com.brainybrawl.app.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import kotlinx.coroutines.*

@Composable fun ProfileAvatar(repository:AvatarRepository,user:String,label:String,modifier:Modifier=Modifier){
 val version by repository.revision.collectAsStateWithLifecycle()
 val bitmap by produceState<android.graphics.Bitmap?>(null,user,version){value=repository.read(user)}
 Box(modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha=.25f)),contentAlignment=Alignment.Center){
  bitmap?.let{Image(it.asImageBitmap(),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)}?:Text(label.take(1).uppercase(),style=MaterialTheme.typography.titleLarge)
 }
}
@Composable fun PhotoPicker(repository:AvatarRepository,local:Boolean){
 var busy by remember{mutableStateOf(false)};var result by remember{mutableStateOf<Int?>(null)}
 val scope=rememberCoroutineScope()
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->if(uri!=null&&!busy){busy=true;scope.launch{
  try{result=if(repository.choose(uri))R.string.photo_synced else R.string.photo_saved}catch(e:CancellationException){throw e}catch(_:PhotoUploadPending){result=R.string.photo_sync_pending}catch(_:Exception){result=R.string.request_failed}finally{busy=false}
 }}}
 BrawlButton(stringResource(R.string.upload_photo),{picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},Modifier.fillMaxWidth(),enabled=!busy)
 if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
 result?.let{Text(stringResource(it),style=MaterialTheme.typography.bodySmall)}
}
