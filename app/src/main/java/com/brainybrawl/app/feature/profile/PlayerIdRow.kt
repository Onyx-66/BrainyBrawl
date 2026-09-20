package com.brainybrawl.app.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R

@Suppress("DEPRECATION")
@Composable fun PlayerIdRow(number:Long?){
    val clipboard=LocalClipboardManager.current
    if(number==null){Text(stringResource(R.string.id_pending),style=MaterialTheme.typography.bodySmall);return}
    val value=PlayerId.format(number)
    Column {
        Text(stringResource(R.string.user_id),style=MaterialTheme.typography.labelSmall)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr){
            Row(verticalAlignment=Alignment.CenterVertically){
                Text(value,style=MaterialTheme.typography.labelLarge,maxLines=1,softWrap=false)
                IconButton({clipboard.setText(AnnotatedString(value))},Modifier.size(48.dp)){
                    Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_copy),stringResource(R.string.copy_id),Modifier.size(18.dp))
                }
            }
        }
    }
}
