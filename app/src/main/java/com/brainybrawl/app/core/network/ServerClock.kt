package com.brainybrawl.app.core.network

/** UI countdown clock anchored to a server sample and monotonic elapsed time. */
class ServerClock(private val elapsed:()->Long) {
    private var serverAtReceipt=0L
    private var receiptElapsed=0L
    private var initialized=false
    @Synchronized fun sample(serverEpochMillis:Long,requestElapsed:Long,responseElapsed:Long) {
        require(responseElapsed>=requestElapsed)
        serverAtReceipt=serverEpochMillis+(responseElapsed-requestElapsed)/2
        receiptElapsed=responseElapsed;initialized=true
    }
    @Synchronized fun nowMillis():Long {
        check(initialized)
        return serverAtReceipt+(elapsed()-receiptElapsed).coerceAtLeast(0)
    }
    fun remainingMillis(deadline:Long):Long=(deadline-nowMillis()).coerceAtLeast(0)
}
