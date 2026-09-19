package com.brainybrawl.app.core

import android.content.Context
import com.brainybrawl.app.BuildConfig
import com.brainybrawl.app.core.network.BackendConfig
import com.brainybrawl.app.core.security.EncryptedAuthStore
import com.brainybrawl.app.feature.auth.SupabaseAuthRepository
import com.brainybrawl.app.feature.profile.SupabasePlayerRepository
import com.brainybrawl.app.game.content.XmlContentRepository
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.functions.Functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {
    private val applicationContext=context.applicationContext
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Default)
    private val config=BackendConfig(BuildConfig.SUPABASE_URL,BuildConfig.SUPABASE_PUBLISHABLE_KEY)
    val supabase=if(config.configured) createSupabaseClient(config.url,config.publishableKey) {
        install(Auth) {
            val encrypted=EncryptedAuthStore(applicationContext)
            sessionManager=encrypted
            codeVerifierCache=encrypted
            flowType=FlowType.PKCE
            scheme="brainybrawl"; host="auth"
            defaultRedirectUrl=SupabaseAuthRepository.CALLBACK
        }
        install(Postgrest);install(Realtime);install(Functions)
    } else null
    val settings=com.brainybrawl.app.feature.settings.SettingsRepository(applicationContext)
    val auth=SupabaseAuthRepository(supabase,scope)
    val players=SupabasePlayerRepository(supabase)
    val store=com.brainybrawl.app.feature.store.SupabaseStoreRepository(supabase,applicationContext)
    val leaderboards=com.brainybrawl.app.feature.leaderboard.SupabaseLeaderboardRepository(supabase)
    val matches=com.brainybrawl.app.feature.match.SupabaseMatchRepository(supabase)
    val rooms=com.brainybrawl.app.feature.lobby.SupabaseRoomRepository(supabase)
    val offlineStatistics=com.brainybrawl.app.feature.offline.OfflineStatistics(applicationContext)
    val content=XmlContentRepository(applicationContext.assets::open,BuildConfig.DEBUG)
}
