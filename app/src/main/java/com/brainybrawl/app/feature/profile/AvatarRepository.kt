package com.brainybrawl.app.feature.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import com.brainybrawl.app.feature.auth.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class PhotoUploadPending(cause:Exception):Exception(cause)

/** Private app cache; photo-picker access only. Never requests broad media/storage permission. */
class AvatarRepository(private val context:Context,private val client:SupabaseClient?,private val auth:AuthRepository){
 val revision=MutableStateFlow(0)
 private val lock=Mutex()
 private fun file(user:String):File{
  require(user.matches(Regex("[A-Za-z0-9_-]{1,80}")))
  return File(File(context.filesDir,"avatars").apply{mkdirs()},"$user.jpg")
 }
 suspend fun removeLocal(user:String)=withContext(Dispatchers.IO){lock.withLock{file(user).takeIf{it.exists()}?.let{check(it.delete())};revision.value++}}
 suspend fun read(user:String):Bitmap?=withContext(Dispatchers.IO){
  val target=file(user)
  if(!target.exists()&&(auth.state.value as? AuthState.SignedIn)?.let{!it.local&&it.userId==user}==true){
   try{val bytes=client!!.storage.from("profile-photos").downloadAuthenticated("$user/photo.jpg");require(bytes.size<=350_000);if(BitmapFactory.decodeByteArray(bytes,0,bytes.size)!=null)target.writeBytes(bytes)}catch(e:CancellationException){throw e}catch(_:Exception){}
  }
  BitmapFactory.decodeFile(target.path)
 }
 suspend fun choose(uri:Uri):Boolean=withContext(Dispatchers.IO){lock.withLock{
  val identity=auth.state.value as? AuthState.SignedIn?:error("account_required")
  val bytes=context.contentResolver.openInputStream(uri)!!.use{input->
   val out=java.io.ByteArrayOutputStream();val block=ByteArray(8192)
   while(true){val n=input.read(block);if(n<0)break;require(out.size()+n<=16_000_000);out.write(block,0,n)};out.toByteArray()
  }
  val bounds=BitmapFactory.Options().apply{inJustDecodeBounds=true};BitmapFactory.decodeByteArray(bytes,0,bytes.size,bounds)
  require(bounds.outWidth>0&&bounds.outHeight>0&&bounds.outWidth.toLong()*bounds.outHeight<=100_000_000)
  var sample=1;while(maxOf(bounds.outWidth,bounds.outHeight)/sample>1024)sample*=2
  val decoded=requireNotNull(BitmapFactory.decodeByteArray(bytes,0,bytes.size,BitmapFactory.Options().apply{inSampleSize=sample}))
  val orientation=try{ExifInterface(bytes.inputStream()).getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)}catch(_:java.io.IOException){ExifInterface.ORIENTATION_NORMAL}
  val transform=Matrix().apply{when(orientation){
   ExifInterface.ORIENTATION_FLIP_HORIZONTAL->setScale(-1f,1f)
   ExifInterface.ORIENTATION_ROTATE_180->setRotate(180f)
   ExifInterface.ORIENTATION_FLIP_VERTICAL->setScale(1f,-1f)
   ExifInterface.ORIENTATION_TRANSPOSE->{setRotate(90f);postScale(-1f,1f)}
   ExifInterface.ORIENTATION_ROTATE_90->setRotate(90f)
   ExifInterface.ORIENTATION_TRANSVERSE->{setRotate(-90f);postScale(-1f,1f)}
   ExifInterface.ORIENTATION_ROTATE_270->setRotate(-90f)
  }}
  val oriented=Bitmap.createBitmap(decoded,0,0,decoded.width,decoded.height,transform,true)
  val side=minOf(oriented.width,oriented.height);val crop=Bitmap.createBitmap(oriented,(oriented.width-side)/2,(oriented.height-side)/2,side,side)
  val scaled=Bitmap.createScaledBitmap(crop,512,512,true)
  val out=java.io.ByteArrayOutputStream();scaled.compress(Bitmap.CompressFormat.JPEG,85,out);val photo=out.toByteArray();require(photo.size<=350_000)
  if(scaled!==crop)scaled.recycle();if(crop!==oriented)crop.recycle();if(oriented!==decoded)oriented.recycle();decoded.recycle()
  require(auth.state.value==identity)
  val target=file(identity.userId);val pending=File(target.path+".tmp");pending.writeBytes(photo);check(pending.renameTo(target));revision.value++
  if(identity.local)return@withLock false
  require(auth.state.value==identity)
  try{client!!.storage.from("profile-photos").upload("${identity.userId}/photo.jpg",photo){upsert=true}
  require(auth.state.value==identity)
  client.postgrest.rpc("set_profile_photo")}catch(e:CancellationException){throw e}catch(e:Exception){throw PhotoUploadPending(e)}
  true
 }}
}
