package com.brainybrawl.app
import com.brainybrawl.app.feature.match.*
import org.junit.Assert.*
import org.junit.Test
class MatchSnapshotTest {
    private fun snapshot(version:Long,time:String="2026-09-19T10:00:00Z")=MatchSnapshot(1,time,MatchData("match","duel","active",version,0),emptyList(),emptyList(),emptyList(),emptyList(),time)
    @Test fun staleEventsRejectedButSameVersionClockRefreshAccepted(){
        val gate=MatchSnapshotGate("match")
        assertTrue(gate.accept(snapshot(3)));assertFalse(gate.accept(snapshot(2)));assertFalse(gate.accept(snapshot(3)))
        assertTrue(gate.accept(snapshot(3,"2026-09-19T10:00:01Z")))
    }
    @Test fun otherMatchesAndProtocolVersionsRejected(){
        assertThrows(IllegalArgumentException::class.java){MatchSnapshotGate("other").accept(snapshot(1))}
        assertThrows(IllegalArgumentException::class.java){MatchSnapshotGate("match").accept(snapshot(1).copy(schemaVersion=2))}
    }
}
