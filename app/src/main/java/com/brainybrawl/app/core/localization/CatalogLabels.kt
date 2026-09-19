package com.brainybrawl.app.core.localization

/** Catalog names follow the selected locale, with English master fallback. */
fun catalogLabel(labels:Map<String,String>,locale:String):String? =
    labels[locale]?.takeIf{it.isNotBlank()} ?: labels["en"]?.takeIf{it.isNotBlank()}
