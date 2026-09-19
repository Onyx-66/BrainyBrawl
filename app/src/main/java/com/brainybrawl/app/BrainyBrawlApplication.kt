package com.brainybrawl.app

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import android.app.Application
import com.brainybrawl.app.core.AppContainer

class BrainyBrawlApplication : Application() {
    override fun onCreate(){super.onCreate();Diagnostics.record(ProductEvent.APP_LAUNCH)}
    val container by lazy { AppContainer(this) }
}
