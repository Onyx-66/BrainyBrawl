package com.brainybrawl.app
import com.brainybrawl.app.core.design.chooseScreenAsset
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random
class ScreenArtSelectionTest {
    @Test fun everyValidBackgroundCanBeSelected(){
        val names=listOf("one.png","two.jpg","three.webp","notes.txt","../escape.png")
        val choices=(0..100).map{chooseScreenAsset("home",names,Random(it))}.toSet()
        assertEquals(setOf("assets/screen/home/one.png","assets/screen/home/two.jpg","assets/screen/home/three.webp"),choices)
    }
    @Test fun emptyOrInvalidFoldersHaveSafeFallbacks(){
        assertNull(chooseScreenAsset("splash",listOf("readme.md")))
        assertEquals("assets/screen/splash/one.png",chooseScreenAsset("splash",listOf("one.png")))
        assertThrows(IllegalArgumentException::class.java){chooseScreenAsset("../private",listOf("one.png"))}
    }
}
