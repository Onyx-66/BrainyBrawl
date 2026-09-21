package com.brainybrawl.app

import androidx.test.platform.app.InstrumentationRegistry
import com.brainybrawl.app.feature.auth.AuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Opt-in check of the installed account. No credentials are embedded or logged. */
class HostedSessionTest {
 @Test fun restoredAccountCanReadProfileAndFriends() {
  assumeTrue(InstrumentationRegistry.getArguments().getString("verifyHosted")=="true")
  val app=InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as BrainyBrawlApplication
  runBlocking {
   val identity=withTimeout(30000){app.container.auth.state.first{it!=AuthState.Loading}}
   check(identity is AuthState.SignedIn&&!identity.local){"A saved online account is required"}
   try {
    check(app.container.players.profile().profile.id==identity.userId)
    app.container.players.social()
   } catch(e:Exception) {
    // Exception messages can contain request headers; report types only.
    throw AssertionError("Hosted profile failed: ${e.javaClass.name}; cause=${e.cause?.javaClass?.name}")
   }
  }
 }
}
