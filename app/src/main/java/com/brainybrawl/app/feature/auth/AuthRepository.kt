package com.brainybrawl.app.feature.auth

import kotlinx.coroutines.flow.StateFlow

sealed interface AuthState {
    data object Loading : AuthState
    data object Unconfigured : AuthState
    data object SignedOut : AuthState
    data class SignedIn(val userId: String, val email: String?,val local:Boolean=false) : AuthState
}
enum class AuthProvider { GOOGLE, DISCORD }
enum class AuthNotice { NONE, BACKEND_REQUIRED, VERIFY_EMAIL, RECOVERY_SENT, PASSWORD_RESET_READY, PASSWORD_UPDATED, INVALID_INPUT, NETWORK_ERROR, REQUEST_FAILED, CALLBACK_REJECTED }
interface AuthRepository {
    val onlineConfigured:Boolean get()=state.value!=AuthState.Unconfigured
    val state: StateFlow<AuthState>
    suspend fun loginLocal(email:String,password:String):AuthNotice=AuthNotice.REQUEST_FAILED
    suspend fun registerLocal(username:String,email:String,password:String):AuthNotice=AuthNotice.REQUEST_FAILED
    suspend fun login(email: String, password: String): AuthNotice
    suspend fun register(username: String, email: String, password: String): AuthNotice
    suspend fun recover(email: String): AuthNotice
    suspend fun changePassword(password: String): AuthNotice
    suspend fun oauth(provider: AuthProvider): AuthNotice
    suspend fun callback(uri: String): AuthNotice
    suspend fun logout(): AuthNotice
}
object AuthValidation {
    fun username(value: String) = value.matches(Regex("[A-Za-z0-9_.]{3,24}"))
    fun email(value: String) = value.length in 3..254 && value.matches(Regex("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
    fun password(value: String) = value.length >= 8
}
