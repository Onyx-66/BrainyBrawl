package com.brainybrawl.app.feature.profile

import com.brainybrawl.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URI

/** Pin the requesting session so a concurrent account switch cannot write another user's appearance. */
class SupabaseAppearanceRemote(private val client:SupabaseClient):AppearanceRemote{
    private val json=Json{ignoreUnknownKeys=true;encodeDefaults=true}
    private fun token(owner:String):String{
        val session=requireNotNull(client.auth.currentSessionOrNull());require(session.user?.id==owner);return session.accessToken
    }
    private fun request(path:String,method:String,token:String,body:ByteArray?=null,image:Boolean=false):JsonObject{
        val connection=URI(BuildConfig.SUPABASE_URL+path).toURL().openConnection() as HttpURLConnection
        try{
            connection.requestMethod=method;connection.connectTimeout=15_000;connection.readTimeout=15_000
            connection.setRequestProperty("Authorization","Bearer $token");connection.setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
            if(body!=null){connection.doOutput=true;connection.setRequestProperty("Content-Type",if(image)"image/jpeg" else "application/json")
                if(image)connection.setRequestProperty("x-upsert","true")
                connection.outputStream.use{it.write(body)}}
            check(connection.responseCode in 200..299){"appearance_sync_failed"}
            val response=connection.inputStream.use{it.readAppearanceBytes(1_048_576)};require(response.size<=1_048_576)
            return if(response.isEmpty()||response.contentEquals("null".toByteArray()))buildJsonObject{} else json.parseToJsonElement(response.decodeToString()).jsonObject
        }finally{connection.disconnect()}
    }
    override suspend fun read(owner:String)=withContext(Dispatchers.IO){
        val user=request("/auth/v1/user","GET",token(owner));require(user["id"]?.jsonPrimitive?.content==owner)
        user["user_metadata"]?.jsonObject?.get("brawl_appearance")?.let{runCatching{json.decodeFromJsonElement<AppearanceSelection>(it)}.getOrNull()}
    }
    override suspend fun save(owner:String,selection:AppearanceSelection,image:ByteArray)=withContext(Dispatchers.IO){
        require(owner.matches(Regex("[0-9a-f-]{36}"))&&image.size<=350_000)
        val bearer=token(owner)
        request("/storage/v1/object/profile-photos/$owner/photo.jpg","POST",bearer,image,true)
        request("/rest/v1/rpc/set_profile_photo","POST",bearer,"{}".toByteArray())
        val body=buildJsonObject{put("data",buildJsonObject{put("brawl_appearance",json.encodeToJsonElement(selection))})}
        val result=request("/auth/v1/user","PUT",bearer,body.toString().toByteArray());require(result["id"]?.jsonPrimitive?.content==owner)
    }
}
