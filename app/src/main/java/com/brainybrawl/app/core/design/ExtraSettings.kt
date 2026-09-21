package com.brainybrawl.app.core.design
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.R
import com.brainybrawl.app.BuildConfig
import com.brainybrawl.app.feature.settings.*
@Composable fun ExtraSettings(repository:SettingsRepository,settings:UserSettings){
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(stringResource(R.string.keep_screen_awake),Modifier.weight(1f));Switch(settings.keepAwake,{repository.update(settings.copy(keepAwake=it))})}
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(stringResource(R.string.puzzle_guide),Modifier.weight(1f));Switch(settings.puzzleGuide,{repository.update(settings.copy(puzzleGuide=it))})}
 val context=LocalContext.current
 TextButton({context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))}){Text(stringResource(R.string.bluetooth_settings))}
 Text(stringResource(R.string.app_version_label)+" "+BuildConfig.VERSION_NAME,style=MaterialTheme.typography.bodySmall)
}
