package com.brainybrawl.app.core.network

import java.net.URI
import java.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class BackendConfig(val url: String, val publishableKey: String) {
    val configured: Boolean get() = url.isNotBlank() && publishableKey.isNotBlank()
    init {
        if (configured) {
            val uri = URI(url)
            require(uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.userInfo == null)
            require(!publishableKey.startsWith("sb_secret_")) { "Privileged keys are forbidden" }
            if (publishableKey.count { it == '.' } == 2) {
                val claims = String(Base64.getUrlDecoder().decode(publishableKey.split('.')[1]))
                require(Json.parseToJsonElement(claims).jsonObject["role"]?.jsonPrimitive?.content == "anon")
            } else require(publishableKey.startsWith("sb_publishable_"))
        }
    }
}
