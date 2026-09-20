package com.brainybrawl.app

import com.brainybrawl.app.feature.profile.AppearanceSelection
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AppearanceSelectionTest{
    @Test fun onlyTheTwelvePackagedSlotsCanBeSelected(){
        for(index in 1..12){val choice=AppearanceSelection(index,index);assertEquals("assets/avatars/avatar_${index.toString().padStart(2,'0')}.png",choice.avatarPath);assertEquals("assets/frames/frame_${index.toString().padStart(2,'0')}.png",choice.framePath)}
        for(index in listOf(-1,0,13,Int.MAX_VALUE)){
            assertThrows(IllegalArgumentException::class.java){AppearanceSelection(index,1)}
            assertThrows(IllegalArgumentException::class.java){AppearanceSelection(1,index)}
        }
    }
    @Test fun serverMetadataCannotInjectArbitraryArtworkPathsOrIds(){
        assertThrows(Exception::class.java){Json.decodeFromString<AppearanceSelection>("""{"avatar":99,"frame":1}""")}
        assertThrows(Exception::class.java){Json.decodeFromString<AppearanceSelection>("""{"avatar":"../../secret","frame":1}""")}
        assertEquals(AppearanceSelection(),Json.decodeFromString<AppearanceSelection>("{}"))
    }
}
