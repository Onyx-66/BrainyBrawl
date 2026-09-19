package com.brainybrawl.app.feature.settings
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(val language:String="en",val dark:Boolean=true,val audio:Boolean=true,val vibration:Boolean=true)
class SettingsRepository(context:Context){
    private val prefs=context.getSharedPreferences("settings",Context.MODE_PRIVATE)
    private val mutable=MutableStateFlow(UserSettings(prefs.getString("language","en").takeIf{it in setOf("en","fr","ar")}?:"en",
        prefs.getBoolean("dark",true),prefs.getBoolean("audio",true),prefs.getBoolean("vibration",true)))
    val state=mutable.asStateFlow()
    fun update(settings:UserSettings){
        require(settings.language in setOf("en","fr","ar"))
        mutable.value=settings
        prefs.edit().putString("language",settings.language).putBoolean("dark",settings.dark)
            .putBoolean("audio",settings.audio).putBoolean("vibration",settings.vibration).apply()
    }
}
