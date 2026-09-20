package com.brainybrawl.app.feature.profile

/** Zero padding preserves the unique, immutable ID allocated by the server. */
object PlayerId {
    fun format(number:Long):String { require(number in 1..999_999_999_999L);return number.toString().padStart(12,'0') }
    fun searchQuery(value:String):String = value.trim().let{if(it.matches(Regex("[0-9]{12}")))it.toLong().toString()else it}
}
