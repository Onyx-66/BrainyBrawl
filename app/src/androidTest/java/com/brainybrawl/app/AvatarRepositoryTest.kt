package com.brainybrawl.app

import android.graphics.Bitmap
import android.graphics.Color
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.brainybrawl.app.feature.auth.*
import com.brainybrawl.app.feature.profile.AvatarRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AvatarRepositoryTest {
 @Test fun photosAreOrientedBoundedPersistentAndAccountScoped()=runBlocking {
  check(BuildConfig.APPLICATION_ID.endsWith(".qa")){"Account mutation tests require the isolated QA package"}
  val context=InstrumentationRegistry.getInstrumentation().targetContext
  val container=(context.applicationContext as BrainyBrawlApplication).container
  container.auth.logout()
  val suffix=java.util.UUID.randomUUID().toString().take(8)
  container.auth.registerLocal("Photo.$suffix","photo.$suffix@example.invalid","TestPassword123")
  val owner=container.localAccounts.state.value.current!!.id
  withTimeout(5_000){container.auth.state.first{(it as? AuthState.SignedIn)?.userId==owner}}
  val input=File(context.cacheDir,"test-photo-$suffix.jpg")
  val bitmap=Bitmap.createBitmap(240,240,Bitmap.Config.ARGB_8888)
  for(y in 0 until 240)for(x in 0 until 240)bitmap.setPixel(x,y,if(y<120)Color.RED else Color.BLUE)
  input.outputStream().use{bitmap.compress(Bitmap.CompressFormat.JPEG,95,it)};bitmap.recycle()
  ExifInterface(input.path).apply{setAttribute(ExifInterface.TAG_ORIENTATION,"6");setAttribute(ExifInterface.TAG_MAKE,"PRIVATE_METADATA");saveAttributes()}
  try {
   assertFalse(container.avatars.choose(Uri.fromFile(input)))
   val stored=File(context.filesDir,"avatars/$owner.jpg")
   assertTrue(stored.length()<=350_000)
   assertNull(ExifInterface(stored.path).getAttribute(ExifInterface.TAG_MAKE))
   val restored=AvatarRepository(context,null,container.auth).read(owner)!!
   assertEquals(512,restored.width);assertEquals(512,restored.height)
   assertTrue(Color.red(restored.getPixel(400,256))>200)
   assertTrue(Color.blue(restored.getPixel(100,256))>200);restored.recycle()
   val before=stored.readBytes();input.writeText("invalid-image")
   try{container.avatars.choose(Uri.fromFile(input));fail("Invalid image accepted")}catch(_:IllegalArgumentException){}
   assertArrayEquals(before,stored.readBytes())
   container.auth.logout();container.auth.registerLocal("Other.$suffix","other.$suffix@example.invalid","TestPassword123")
   val other=container.localAccounts.state.value.current!!.id
   assertNull(container.avatars.read(other));assertTrue(stored.exists())
   container.localAccounts.deleteCurrent(other)
   container.auth.loginLocal("Photo.$suffix","TestPassword123")
   container.localAccounts.deleteCurrent(owner);container.avatars.removeLocal(owner)
   assertFalse(stored.exists())
  } finally {input.delete();container.auth.logout()}
 }
}
