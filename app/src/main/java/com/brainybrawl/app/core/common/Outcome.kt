package com.brainybrawl.app.core.common

sealed interface Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>
    data class Failure(val reason: FailureReason) : Outcome<Nothing>
}

/** Stable presentation keys; never display raw server exceptions or credentials. */
enum class FailureReason { OFFLINE, UNCONFIGURED, UNAUTHORIZED, INVALID_INPUT, CONFLICT, UNAVAILABLE }
