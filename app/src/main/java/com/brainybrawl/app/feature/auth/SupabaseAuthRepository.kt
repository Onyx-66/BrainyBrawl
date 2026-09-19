package com.brainybrawl.app.feature.auth

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.Discord
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthRepository(private val client: SupabaseClient?, scope: CoroutineScope) : AuthRepository {
    override val state: StateFlow<AuthState> = client?.auth?.sessionStatus?.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> status.session.user?.let { AuthState.SignedIn(it.id, it.email) } ?: AuthState.SignedOut
            is SessionStatus.Initializing -> AuthState.Loading
            else -> AuthState.SignedOut
        }
     }?.distinctUntilChanged()?.onEach{if(it is AuthState.SignedIn)Diagnostics.record(ProductEvent.AUTH_SUCCESS)}?.stateIn(scope, SharingStarted.Eagerly, AuthState.Loading) ?: MutableStateFlow(AuthState.Unconfigured)

    private suspend fun request(action: suspend () -> AuthNotice): AuthNotice {
        if (client == null) return AuthNotice.REQUEST_FAILED
        return try { action() } catch (e: CancellationException) { throw e }
        catch (_: IOException) { Diagnostics.record(ProductEvent.AUTH_FAILURE);AuthNotice.NETWORK_ERROR }
        catch (e:io.github.jan.supabase.auth.exception.AuthRestException) {
            if(e.error=="email_not_confirmed")AuthNotice.VERIFY_EMAIL else AuthNotice.REQUEST_FAILED
        }
        catch (_: Exception) { Diagnostics.record(ProductEvent.AUTH_FAILURE);AuthNotice.REQUEST_FAILED }
    }
    override suspend fun login(email: String, password: String): AuthNotice {
        if (!AuthValidation.email(email) || password.isBlank()) return AuthNotice.INVALID_INPUT
        return request { client!!.auth.signInWith(Email) { this.email=email.trim(); this.password=password }; AuthNotice.NONE }
    }
    override suspend fun register(username: String, email: String, password: String): AuthNotice {
        if (!AuthValidation.username(username) || !AuthValidation.email(email) || !AuthValidation.password(password)) return AuthNotice.INVALID_INPUT
        return request {
            client!!.auth.signUpWith(Email,redirectUrl=CALLBACK) { this.email=email.trim(); this.password=password; data=buildJsonObject { put("username",username) } }
            if(client.auth.currentSessionOrNull()!=null)AuthNotice.NONE else AuthNotice.VERIFY_EMAIL
        }
    }
    override suspend fun recover(email: String): AuthNotice {
        if (!AuthValidation.email(email)) return AuthNotice.INVALID_INPUT
        return request { client!!.auth.resetPasswordForEmail(email.trim(), redirectUrl=RECOVERY_CALLBACK); AuthNotice.RECOVERY_SENT }
    }
    override suspend fun changePassword(password: String): AuthNotice {
        if (!AuthValidation.password(password)) return AuthNotice.INVALID_INPUT
        return request { client!!.auth.updateUser { this.password=password }; AuthNotice.PASSWORD_UPDATED }
    }
    override suspend fun oauth(provider: AuthProvider): AuthNotice = request {
        when(provider) {
            AuthProvider.GOOGLE -> client!!.auth.signInWith(Google, redirectUrl=CALLBACK)
            AuthProvider.DISCORD -> client!!.auth.signInWith(Discord, redirectUrl=CALLBACK)
        }
        AuthNotice.NONE
    }
    override suspend fun callback(uri: String): AuthNotice = request {
        val parsed=Uri.parse(uri)
        if (parsed.scheme!="brainybrawl" || parsed.host!="auth" || parsed.path!="/callback" || parsed.fragment!=null || parsed.getQueryParameter("error")!=null) return@request AuthNotice.CALLBACK_REJECTED
        val code=parsed.getQueryParameter("code")?.takeIf { it.isNotBlank() && it.length<=4096 } ?: return@request AuthNotice.CALLBACK_REJECTED
        client!!.auth.exchangeCodeForSession(code)
        if(parsed.getQueryParameter("flow")=="recovery")AuthNotice.PASSWORD_RESET_READY else AuthNotice.NONE
    }
    override suspend fun logout(): AuthNotice = request { client!!.auth.signOut(); AuthNotice.NONE }
    companion object { const val CALLBACK="brainybrawl://auth/callback"; const val RECOVERY_CALLBACK="$CALLBACK?flow=recovery" }
}
