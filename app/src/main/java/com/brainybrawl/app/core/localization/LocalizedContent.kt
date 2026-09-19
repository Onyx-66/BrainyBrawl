package com.brainybrawl.app.core.localization
import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

@Composable fun LocalizedContent(language:String,content:@Composable ()->Unit){
    val base=LocalContext.current
    val original=LocalConfiguration.current
    val configuration=remember(original,language){Configuration(original).apply{
        val locale=Locale.forLanguageTag(language);setLocale(locale);setLayoutDirection(locale)
    }}
    val localized=remember(base,configuration){base.createConfigurationContext(configuration)}
    CompositionLocalProvider(LocalContext provides localized,LocalConfiguration provides configuration,
        LocalLayoutDirection provides if(language=="ar")LayoutDirection.Rtl else LayoutDirection.Ltr,content=content)
}
