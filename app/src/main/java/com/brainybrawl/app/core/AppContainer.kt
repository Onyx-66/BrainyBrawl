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
        install(Postgrest);install(Realtime);install(Functions);install(io.github.jan.supabase.storage.Storage)
    } else null
    val screenArt=com.brainybrawl.app.core.design.ScreenArtRepository(applicationContext)
    val settings=com.brainybrawl.app.feature.settings.SettingsRepository(applicationContext)
    val localAccounts=com.brainybrawl.app.feature.auth.LocalAccounts(object:com.brainybrawl.app.feature.auth.LocalAccountVault{
        private val encrypted=EncryptedAuthStore(applicationContext)
        override suspend fun read()=encrypted.readLocalAccounts()
        override suspend fun write(value:String)=encrypted.writeLocalAccounts(value)
    })
    val connectivity=com.brainybrawl.app.core.network.ConnectivityMonitor(applicationContext,scope)
    val auth=com.brainybrawl.app.feature.auth.HybridAuthRepository(SupabaseAuthRepository(supabase,scope),localAccounts,scope,connectivity.online,
        object:com.brainybrawl.app.feature.auth.PendingAccountStore{
            private val encrypted=EncryptedAuthStore(applicationContext)
            private val json=kotlinx.serialization.json.Json{ignoreUnknownKeys=true}
            override suspend fun read()=encrypted.readPendingAccount()?.let{json.decodeFromString<com.brainybrawl.app.feature.auth.PendingAccountConnection>(it)}
            override suspend fun write(value:com.brainybrawl.app.feature.auth.PendingAccountConnection?)=encrypted.writePendingAccount(value?.let{json.encodeToString(com.brainybrawl.app.feature.auth.PendingAccountConnection.serializer(),it)})
        })
    val avatars=com.brainybrawl.app.feature.profile.AvatarRepository(applicationContext,supabase,auth)
    val players=SupabasePlayerRepository(supabase)
    val store=com.brainybrawl.app.feature.store.SupabaseStoreRepository(supabase,applicationContext)
    val leaderboards=com.brainybrawl.app.feature.leaderboard.SupabaseLeaderboardRepository(supabase)
    val matches=com.brainybrawl.app.feature.match.SupabaseMatchRepository(supabase)
    val rooms=com.brainybrawl.app.feature.lobby.SupabaseRoomRepository(supabase)
    val socialQueue=com.brainybrawl.app.feature.friends.LocalSocialCoordinator(localAccounts,auth,players,rooms)
    val offlineStatistics=com.brainybrawl.app.feature.offline.OfflineStatistics(applicationContext){
        val signedIn=auth.state.value as? com.brainybrawl.app.feature.auth.AuthState.SignedIn
        localAccounts.state.value.current?.takeIf{signedIn?.email.equals(it.email,true)}?.id?:signedIn?.userId?:"guest"
    }
    val content=XmlContentRepository(applicationContext.assets::open,BuildConfig.DEBUG)
}
