package com.brainybrawl.app.feature.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface LocalAccountVault { suspend fun read():String?;suspend fun write(value:String) }
@Serializable private data class StoredAccount(val id:String,val username:String,val email:String,val salt:String,val verifier:String)
@Serializable data class PendingSocial(val id:String,val owner:String,val target:String,val kind:String,val mode:String="duel",val room:String?=null,val sent:Boolean=false)
@Serializable private data class AccountDatabase(val accounts:List<StoredAccount> = emptyList(),val current:String?=null,val queue:List<PendingSocial> = emptyList())
data class LocalIdentity(val id:String,val username:String,val email:String)
data class LocalAccountState(val ready:Boolean=false,val failed:Boolean=false,val current:LocalIdentity?=null,val queue:List<PendingSocial> = emptyList())

/** Local identity is not a Supabase identity. No remote role, score or Flame can be granted here. */
class LocalAccounts(private val vault:LocalAccountVault){
    private val lock=Mutex()
    private val mutable=MutableStateFlow(LocalAccountState())
    val state=mutable.asStateFlow()
    private var db=AccountDatabase()
    private val json=Json{ignoreUnknownKeys=true}
    private fun publish(){val a=db.accounts.find{it.id==db.current};mutable.value=LocalAccountState(true,false,a?.let{LocalIdentity(it.id,it.username,it.email)},db.queue.filter{it.owner==a?.id})}
    suspend fun initialize()=lock.withLock{if(!mutable.value.ready||mutable.value.failed){db=vault.read()?.let{json.decodeFromString<AccountDatabase>(it)}?:AccountDatabase();publish()}}
    fun storageFailed(){mutable.value=LocalAccountState(ready=true,failed=true)}
    private suspend fun save(next:AccountDatabase){vault.write(json.encodeToString(AccountDatabase.serializer(),next));db=next;publish()}
    private fun normalize(email:String)=email.trim().lowercase(java.util.Locale.ROOT)
    private suspend fun derive(password:String,salt:ByteArray):ByteArray=withContext(Dispatchers.Default){
        val spec=PBEKeySpec(password.toCharArray(),salt,210_000,256)
        try{SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded}finally{spec.clearPassword()}
    }
    suspend fun register(username:String,email:String,password:String):AuthNotice{
        if(!AuthValidation.username(username)||!AuthValidation.email(email)||!AuthValidation.password(password)||password.length>1024)return AuthNotice.INVALID_INPUT
        initialize()
        return lock.withLock{
            val address=normalize(email)
            if(db.accounts.size>=32||db.accounts.any{it.email==address||it.username.equals(username,true)})return@withLock AuthNotice.REQUEST_FAILED
            val salt=ByteArray(16).also{SecureRandom().nextBytes(it)}
            val account=StoredAccount("local_"+UUID.randomUUID(),username,address,Base64.getEncoder().encodeToString(salt),Base64.getEncoder().encodeToString(derive(password,salt)))
            save(db.copy(accounts=db.accounts+account,current=account.id));AuthNotice.NONE
        }
    }
    suspend fun login(email:String,password:String):AuthNotice{
        if(password.length>1024)return AuthNotice.INVALID_INPUT
        initialize()
        return lock.withLock{
            val account=db.accounts.find{it.email==normalize(email)||it.username.equals(email.trim(),true)}?:return@withLock AuthNotice.REQUEST_FAILED
            val expected=Base64.getDecoder().decode(account.verifier)
            val actual=derive(password,Base64.getDecoder().decode(account.salt))
            if(!MessageDigest.isEqual(expected,actual))return@withLock AuthNotice.REQUEST_FAILED
            save(db.copy(current=account.id));AuthNotice.NONE
        }
    }
    /** Called only after the remote service authenticated these credentials. */
    suspend fun rememberVerifiedLogin(email:String,password:String){
        initialize();lock.withLock{
            val address=normalize(email)
            val existing=db.accounts.find{it.email==address}
            val salt=ByteArray(16).also{SecureRandom().nextBytes(it)}
            val verifier=Base64.getEncoder().encodeToString(derive(password,salt))
            val base=address.substringBefore('@').filter{it.isLetterOrDigit()&&it.code<128}.take(16).padEnd(3,'_')
            var name=base;var suffix=1
            while(db.accounts.any{it.id!=existing?.id&&it.username.equals(name,true)}){name=base+suffix++}
            val account=StoredAccount(existing?.id?:"local_"+UUID.randomUUID(),existing?.username?:name,address,Base64.getEncoder().encodeToString(salt),verifier)
            require(existing!=null||db.accounts.size<32)
            save(db.copy(accounts=db.accounts.filterNot{it.id==account.id}+account,current=account.id))
        }
    }
    suspend fun deleteCurrent(expectedId:String){initialize();lock.withLock{
        val id=requireNotNull(db.current)
        require(id==expectedId)
        save(db.copy(accounts=db.accounts.filterNot{it.id==id},queue=db.queue.filterNot{it.owner==id},current=null))
    }}
    suspend fun logout(){initialize();lock.withLock{save(db.copy(current=null))}}
    suspend fun changePassword(password:String):AuthNotice{
        if(!AuthValidation.password(password)||password.length>1024)return AuthNotice.INVALID_INPUT
        initialize();return lock.withLock{
            val id=db.current?:return@withLock AuthNotice.REQUEST_FAILED
            val salt=ByteArray(16).also{SecureRandom().nextBytes(it)}
            val encoded=Base64.getEncoder().encodeToString(derive(password,salt))
            save(db.copy(accounts=db.accounts.map{if(it.id==id)it.copy(salt=Base64.getEncoder().encodeToString(salt),verifier=encoded)else it}));AuthNotice.PASSWORD_UPDATED
        }
    }
    suspend fun enqueue(target:String,kind:String,mode:String="duel"){
        require(target.trim().length in 3..254&&kind in setOf("friend","invite")&&mode in setOf("duel","duo","squad","solo"))
        initialize();lock.withLock{
            val owner=requireNotNull(db.current)
            require(db.queue.count{it.owner==owner&&!it.sent}<100)
            if(db.queue.any{it.owner==owner&&it.target.equals(target.trim(),true)&&it.kind==kind&&it.mode==mode&&!it.sent})return@withLock
            save(db.copy(queue=db.queue+PendingSocial(UUID.randomUUID().toString(),owner,target.trim(),kind,mode)))
        }
    }
    suspend fun cancel(id:String){initialize();lock.withLock{save(db.copy(queue=db.queue.filterNot{it.id==id&&it.owner==db.current}))}}
    suspend fun update(item:PendingSocial){lock.withLock{
        require(item.owner==db.current)
        save(db.copy(queue=db.queue.map{if(it.id==item.id&&it.owner==item.owner)item else it}))
    }}
}
