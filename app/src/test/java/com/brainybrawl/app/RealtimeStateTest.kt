package com.brainybrawl.app

import com.brainybrawl.app.core.network.ServerClock
import com.brainybrawl.app.feature.lobby.*
import org.junit.Assert.*
import org.junit.Test

class RealtimeStateTest {
    private fun snapshot(version:Long,time:String="2026-09-19T00:00:00Z",room:String="room")=RoomSnapshot(1,time,RoomData(room,"host",OnlineMode.DUO,false,"lobby",version),emptyList(),emptyList())
    @Test fun oldSnapshotsAndDuplicateEventsCannotRewindRoom() {
        val gate=RoomSnapshotGate("room")
        assertNotNull(gate.accept(snapshot(5)))
        assertNull(gate.accept(snapshot(4,"2026-09-19T00:00:01Z")))
        assertNull(gate.accept(snapshot(5)))
        assertNotNull(gate.accept(snapshot(5,"2026-09-19T00:00:02Z")))
        assertNotNull(gate.accept(snapshot(6,"2026-09-19T00:00:03Z")))
    }
    @Test(expected=IllegalArgumentException::class)
    fun wrongRoomIsRejected(){RoomSnapshotGate("room").accept(snapshot(1,room="other"))}
    @Test(expected=IllegalArgumentException::class)
    fun unknownProtocolIsRejected(){RoomSnapshotGate("room").accept(snapshot(1).copy(schemaVersion=2))}
    @Test fun deadlinesUseElapsedTimeAndClampAtZero() {
        var elapsed=1100L
        val clock=ServerClock{elapsed}
        clock.sample(10000,1000,1100)
        assertEquals(10050L,clock.nowMillis())
        elapsed=1600;assertEquals(10550L,clock.nowMillis())
        assertEquals(450L,clock.remainingMillis(11000))
        elapsed=100000;assertEquals(0L,clock.remainingMillis(11000))
    }
    @Test(expected=IllegalStateException::class)
    fun unsynchronizedClockCannotInventServerTime(){ServerClock{0}.nowMillis()}
}
