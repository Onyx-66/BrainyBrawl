package com.brainybrawl.app.feature.profile

import android.content.Context
import android.graphics.*
import com.brainybrawl.app.feature.auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable data class AppearanceSelection(val avatar:Int=1,val frame:Int=1){
    init{require(avatar in 1..12&&frame in 1..12)}
    val avatarPath get()="assets/avatars/avatar_${avatar.toString().padStart(2,'0')}.png"
    val framePath get()="assets/frames/frame_${frame.toString().padStart(2,'0')}.png"
}
interface AppearanceRemote {
    suspend fun read(owner:String):AppearanceSelection?
    suspend fun save(owner:String,selection:AppearanceSelection,image:ByteArray)
}
/** Account-scoped cosmetic preferences. No inventory ownership or competitive values live here. */
class AppearanceRepository(private val context:Context,private val auth:AuthRepository,private val remote:AppearanceRemote?,
    scope:CoroutineScope?=null,private val connectivity:StateFlow<Boolean> = MutableStateFlow(true),private val localOwner:(AuthState.SignedIn)->String? = {null}){
    private val preferences=context.getSharedPreferences("appearance",Context.MODE_PRIVATE)
    private val json=Json{ignoreUnknownKeys=true}
    private val lock=Mutex()
    val revision=MutableStateFlow(0)
    init{scope?.launch{combine(auth.state,connectivity){identity,connected->identity to connected}.collectLatest{(identity,connected)->
        if(identity is AuthState.SignedIn&&!identity.local&&connected){
            try{adoptLocal(identity);refresh(identity.userId)}catch(e:CancellationException){throw e}catch(_:Exception){}
            while(isActive&&pending(identity.userId)&&auth.state.value==identity){
                delay(15_000)
                try{refresh(identity.userId)}catch(e:CancellationException){throw e}catch(_:Exception){}
            }
        }
    }}}
    private fun key(user:String):String{require(user.matches(Regex("[A-Za-z0-9_-]{1,80}")));return user}
    fun read(user:String):AppearanceSelection=runCatching{preferences.getString(key(user),null)?.let{json.decodeFromString<AppearanceSelection>(it)}}.getOrNull()?:AppearanceSelection()
    fun pending(user:String)=preferences.getBoolean(key(user)+".pending",false)
    private fun persist(user:String,value:AppearanceSelection,pending:Boolean){
        val edit=preferences.edit().putString(key(user),json.encodeToString(AppearanceSelection.serializer(),value)).putBoolean(key(user)+".pending",pending)
        val identity=auth.state.value as? AuthState.SignedIn
        if(identity?.userId==user&&!identity.local)localOwner(identity)?.takeIf{it!=user}?.let{alias->
            edit.putString(key(alias),json.encodeToString(AppearanceSelection.serializer(),value)).putBoolean(key(alias)+".pending",false)
        }
        check(edit.commit());revision.value++
    }
    private suspend fun adoptLocal(identity:AuthState.SignedIn)=withContext(Dispatchers.IO){lock.withLock{
        val local=localOwner(identity)?:return@withLock
        if(auth.state.value==identity&&pending(local)&&!pending(identity.userId))persist(identity.userId,read(local),true)
    }}
    suspend fun remove(user:String)=withContext(Dispatchers.IO){lock.withLock{check(preferences.edit().remove(key(user)).remove(key(user)+".pending").commit());revision.value++}}
    suspend fun select(user:String,selection:AppearanceSelection):Boolean=withContext(Dispatchers.IO){lock.withLock{
        val identity=auth.state.value as? AuthState.SignedIn?:error("account_required")
        require(identity.userId==user)
        persist(user,selection,true)
        if(identity.local||!connectivity.value||remote==null)return@withLock false
        upload(identity,selection)
    }}
    private suspend fun upload(identity:AuthState.SignedIn,selection:AppearanceSelection):Boolean{
        try{
            require(auth.state.value==identity)
            val bitmap=AppearanceArt.avatar(context,selection.avatar)
            val buffer=java.io.ByteArrayOutputStream();bitmap.compress(Bitmap.CompressFormat.JPEG,85,buffer);bitmap.recycle()
            val bytes=buffer.toByteArray();require(bytes.size<=350_000)
            remote!!.save(identity.userId,selection,bytes)
            require(auth.state.value==identity)
            persist(identity.userId,selection,false);return true
        }catch(e:CancellationException){throw e}catch(_:Exception){return false}
    }
    suspend fun refresh(user:String)=withContext(Dispatchers.IO){lock.withLock{
        val identity=auth.state.value as? AuthState.SignedIn?:return@withLock
        if(identity.userId!=user||identity.local||remote==null||!connectivity.value)return@withLock
        if(pending(user)){upload(identity,read(user));return@withLock}
        val saved=remote.read(user)
        if(auth.state.value==identity&&saved!=null)persist(user,saved,false)
    }}
}

/** Fixed packaged paths and bounded decodes. Square fallback art works before the owner adds the pack. */
object AppearanceArt{
    private val colors=intArrayOf(0xFF7155D9.toInt(),0xFF197BB7.toInt(),0xFF1D9B82.toInt(),0xFFD77826.toInt(),0xFFBA4F99.toInt(),0xFF456DC3.toInt(),0xFFB44759.toInt(),0xFF6A9746.toInt(),0xFF9D6DB0.toInt(),0xFF2E8D99.toInt(),0xFFC19529.toInt(),0xFF746FC2.toInt())
    fun avatar(context:Context,index:Int):Bitmap{
        val selection=AppearanceSelection(avatar=index)
        packaged(context,selection.avatarPath)?.let{return it}
        return Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888).also{bitmap->
            val canvas=Canvas(bitmap);canvas.drawColor(colors[index-1]);val p=Paint(Paint.ANTI_ALIAS_FLAG)
            p.color=0x33FFFFFF;canvas.drawCircle(450f,50f,260f,p)
            p.color=0xFFEAE5FF.toInt();canvas.drawCircle(256f,202f,86f,p);canvas.drawRoundRect(98f,302f,414f,554f,150f,150f,p)
            p.color=colors[index-1];canvas.drawCircle(229f,197f,9f,p);canvas.drawCircle(283f,197f,9f,p)
            p.style=Paint.Style.STROKE;p.strokeWidth=9f;canvas.drawArc(230f,201f,282f,244f,15f,150f,false,p)
            p.style=Paint.Style.FILL;p.color=0xFF172447.toInt();p.textAlign=Paint.Align.CENTER;p.typeface=Typeface.DEFAULT_BOLD;p.textSize=44f;canvas.drawText(index.toString(),256f,419f,p)
        }
    }
    fun frame(context:Context,index:Int):Bitmap{
        val selection=AppearanceSelection(frame=index)
        packaged(context,selection.framePath)?.let{return it}
        return Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888).also{bitmap->
            val canvas=Canvas(bitmap);val p=Paint(Paint.ANTI_ALIAS_FLAG);p.style=Paint.Style.STROKE;p.color=colors[index-1];p.strokeWidth=32f
            canvas.drawRect(22f,22f,490f,490f,p);p.strokeWidth=6f;p.color=0xFFFFD55C.toInt();canvas.drawRect(43f,43f,469f,469f,p)
            p.style=Paint.Style.FILL;listOf(22f to 22f,490f to 22f,22f to 490f,490f to 490f).forEach{(x,y)->canvas.drawRect(x-14,y-14,x+14,y+14,p)}
        }
    }
    private fun packaged(context:Context,path:String):Bitmap?=try{
        val bytes=context.assets.open(path).use{it.readAppearanceBytes(8*1024*1024)};require(bytes.size<=8*1024*1024)
        val bounds=BitmapFactory.Options().apply{inJustDecodeBounds=true};BitmapFactory.decodeByteArray(bytes,0,bytes.size,bounds)
        require(bounds.outWidth in 1..4096&&bounds.outHeight==bounds.outWidth)
        val source=requireNotNull(BitmapFactory.decodeByteArray(bytes,0,bytes.size,BitmapFactory.Options().apply{inSampleSize=(bounds.outWidth/512).coerceAtLeast(1)}))
        Bitmap.createScaledBitmap(source,512,512,true).also{if(it!==source)source.recycle()}
    }catch(_:Exception){null}
}

internal fun java.io.InputStream.readAppearanceBytes(limit:Int):ByteArray{
    val output=java.io.ByteArrayOutputStream();val buffer=ByteArray(8192)
    while(true){val count=read(buffer);if(count<0)break;require(output.size()+count<=limit);output.write(buffer,0,count)}
    return output.toByteArray()
}
