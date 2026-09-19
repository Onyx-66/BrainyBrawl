package com.brainybrawl.app.core.localization
import android.content.res.Configuration
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

@Composable fun LocalizedContent(language:String,content:@Composable ()->Unit){
    val base=LocalContext.current
    // Configuration contexts are not Activities; preserve the launcher owner before replacing context.
    val activityResults=LocalActivityResultRegistryOwner.current
    val original=LocalConfiguration.current
    val configuration=remember(original,language){Configuration(original).apply{
        val locale=Locale.forLanguageTag(language);setLocale(locale);setLayoutDirection(locale)
    }}
    val localized=remember(base,configuration){base.createConfigurationContext(configuration)}
    CompositionLocalProvider(LocalContext provides localized,LocalConfiguration provides configuration,
        *listOfNotNull(activityResults?.let{LocalActivityResultRegistryOwner provides it}).toTypedArray(),
        LocalLayoutDirection provides if(language=="ar")LayoutDirection.Rtl else LayoutDirection.Ltr,content=content)
}
