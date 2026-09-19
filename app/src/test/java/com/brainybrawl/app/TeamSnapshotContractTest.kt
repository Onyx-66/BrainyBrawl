package com.brainybrawl.app

import com.brainybrawl.app.feature.match.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class TeamSnapshotContractTest {
    private val json=Json { ignoreUnknownKeys=true;classDiscriminator="kind" }
    private fun read(name:String):MatchSnapshot = javaClass.getResourceAsStream("/server/$name.json")!!.bufferedReader().use { json.decodeFromString(it.readText()) }
    @Test fun actualServerBoardsDecodeAndKeepTeamIsolation(){
        val duo=read("duo_initial")
        val puzzle=duo.board as PuzzleBoardView
        assertEquals(96,puzzle.pieces.size)
        assertEquals(96,puzzle.slots.size)
        puzzle.slots.forEach { assertEquals(4,polygonPoints(it.polygon).size) }
        val squad=read("squad_initial").board as PrecisionBoard
        assertEquals(4,squad.players.size)
        assertEquals(1.0,squad.config.speed,0.0)
        assertEquals(2,(read("squad_sort").board as SortBoard).buckets.size)
        assertFalse((read("duo_scramble").board as ScrambleBoard).answered)
    }
    @Test fun actualDraftAndResultsSurviveReconnect(){
        for(mode in listOf("duo","squad")){
            val draft=read("${mode}_draft")
            assertEquals(1,draft.draft!!.question)
            assertEquals(draft.teams.size,draft.draft!!.answerers.size)
            assertNull(draft.draft!!.roundId)
            assertTrue(MatchSnapshotGate(draft.match.id).accept(draft))
            val results=read("${mode}_results")
            assertEquals("results",results.match.status)
            assertEquals(if(mode=="duo")2 else 4,results.results.count{it.winner})
        }
    }
    @Test fun irregularPuzzleHitTestingRejectsBoundingBoxCorners(){
        val diamond=polygonPoints("0.5,0 1,0.5 0.5,1 0,0.5")
        assertTrue(polygonContains(diamond,.5f,.5f))
        assertFalse(polygonContains(diamond,.01f,.01f))
        assertThrows(IllegalArgumentException::class.java){polygonPoints("NaN,0 1,0 0,1")}
    }
    @Test fun soloSnapshotsDecodeIndividualBoardsAndFinalRewards(){
        val precision=read("solo_precision")
        assertEquals("solo",precision.match.mode)
        assertEquals(1,(precision.board as PrecisionBoard).players.size)
        val sort=read("solo_sort").board as SortBoard
        assertEquals(0,sort.index)
        assertEquals(2,sort.buckets.size)
        val results=read("solo_results")
        assertEquals("results",results.match.status)
        assertEquals(17,results.rounds.size)
        assertEquals(1,results.results.count{it.winner})
    }
}
