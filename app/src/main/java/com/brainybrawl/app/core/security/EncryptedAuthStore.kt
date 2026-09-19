package com.brainybrawl.app.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import io.github.jan.supabase.auth.CodeVerifierCache
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/** Keys remain in Android Keystore. Both session and PKCE verifier are encrypted. */
class EncryptedAuthStore(context: Context) : SessionManager, CodeVerifierCache {
    private val preferences = context.getSharedPreferences("auth_encrypted", Context.MODE_PRIVATE)
    companion object { private val mutex = Mutex() }
    private val json = Json { ignoreUnknownKeys = true }
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val alias = "brainybrawl.auth.v1"
        (store.getKey(alias, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true).build())
        }.generateKey()
    }
    private fun write(name: String, value: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        cipher.updateAAD(name.toByteArray(Charsets.UTF_8))
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        check(preferences.edit().putString(name, Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)).commit())
    }
    private fun read(name: String): String? {
        val stored = preferences.getString(name, null) ?: return null
        return try {
            val bytes = Base64.decode(stored, Base64.NO_WRAP)
            require(bytes.size > 28)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
            cipher.updateAAD(name.toByteArray(Charsets.UTF_8))
            String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
        } catch (error: Exception) {
            if(name=="local_accounts") throw error
            // Invalidated keys or corrupt ciphertext require fresh sign-in, never plaintext fallback.
            preferences.edit().remove(name).commit(); null
        }
    }
    suspend fun readLocalAccounts():String?=withContext(Dispatchers.IO){mutex.withLock{read("local_accounts")}}
    suspend fun writeLocalAccounts(value:String)=withContext(Dispatchers.IO){mutex.withLock{write("local_accounts",value)}}
    suspend fun readPendingAccount():String?=withContext(Dispatchers.IO){mutex.withLock{read("pending_account")}}
    suspend fun writePendingAccount(value:String?)=withContext(Dispatchers.IO){mutex.withLock{
        if(value==null)check(preferences.edit().remove("pending_account").commit())else write("pending_account",value)
    }}
    override suspend fun saveSession(session: UserSession) = withContext(Dispatchers.IO) {
        mutex.withLock { write("session", json.encodeToString(UserSession.serializer(), session)) }
    }
    override suspend fun loadSession(): UserSession? = withContext(Dispatchers.IO) {
        mutex.withLock { read("session")?.let { runCatching { json.decodeFromString(UserSession.serializer(), it) }.getOrNull() } }
    }
    override suspend fun deleteSession() { withContext(Dispatchers.IO) { mutex.withLock { check(preferences.edit().remove("session").commit()) } } }
    override suspend fun saveCodeVerifier(codeVerifier: String) = withContext(Dispatchers.IO) {
        mutex.withLock { write("verifier", "${System.currentTimeMillis()}:$codeVerifier") }
    }
    override suspend fun loadCodeVerifier(): String? = withContext(Dispatchers.IO) {
        mutex.withLock {
            read("verifier")?.split(':', limit = 2)?.takeIf {
                it.size == 2 && (System.currentTimeMillis() - (it[0].toLongOrNull() ?: 0)) in 0..600_000
            }?.get(1)
        }
    }
    override suspend fun deleteCodeVerifier() { withContext(Dispatchers.IO) { mutex.withLock { check(preferences.edit().remove("verifier").commit()) } } }
}
