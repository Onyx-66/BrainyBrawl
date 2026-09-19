package com.brainybrawl.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthForm { LOGIN, REGISTER, RECOVER, CHANGE_PASSWORD }
data class AuthUiState(val busy: Boolean = false, val notice: AuthNotice = AuthNotice.NONE)
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val auth = repository.state
    private val mutableUi=MutableStateFlow(AuthUiState())
    val ui=mutableUi.asStateFlow()
    private fun perform(action: suspend () -> AuthNotice) {
        if(mutableUi.value.busy) return
        if(auth.value==AuthState.Unconfigured){mutableUi.value=AuthUiState(notice=AuthNotice.BACKEND_REQUIRED);return}
        mutableUi.value=AuthUiState(busy=true)
        viewModelScope.launch {
            try { mutableUi.value=AuthUiState(notice=action()) }
            finally { mutableUi.value=mutableUi.value.copy(busy=false) }
        }
    }
    fun submit(form: AuthForm, username: String, email: String, password: String) = perform {
        when(form) {
            AuthForm.LOGIN -> repository.login(email,password)
            AuthForm.REGISTER -> repository.register(username,email,password)
            AuthForm.RECOVER -> repository.recover(email)
            AuthForm.CHANGE_PASSWORD -> repository.changePassword(password)
        }
    }
    fun oauth(provider: AuthProvider) = perform { repository.oauth(provider) }
    fun callback(uri: String) = perform { repository.callback(uri) }
    fun consumeNotice(){mutableUi.value=mutableUi.value.copy(notice=AuthNotice.NONE)}
    fun logout() = perform { repository.logout() }
    companion object {
        fun factory(repository: AuthRepository)=object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(AuthViewModel::class.java))
                @Suppress("UNCHECKED_CAST") return AuthViewModel(repository) as T
            }
        }
    }
}
