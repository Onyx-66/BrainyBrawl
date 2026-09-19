package com.brainybrawl.app
import com.brainybrawl.app.core.diagnostics.*
import org.junit.Assert.*
import org.junit.Test
class DiagnosticsTest {
    @Test fun boundedHistoryEvictsOldEventsAndSnapshotIsIndependent(){
        val buffer=EventBuffer(2)
        buffer.record(ProductEvent.APP_LAUNCH)
        val first=buffer.snapshot()
        buffer.record(ProductEvent.CONNECTION_LOST);buffer.record(ProductEvent.CONNECTION_RESTORED)
        assertEquals(listOf(2L,3L),buffer.snapshot().map{it.sequence})
        assertEquals(ProductEvent.APP_LAUNCH,first.single().event)
        buffer.clear();assertTrue(buffer.snapshot().isEmpty());assertEquals(1,first.size)
    }
}
