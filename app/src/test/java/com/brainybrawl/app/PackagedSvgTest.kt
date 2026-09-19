package com.brainybrawl.app
import com.brainybrawl.app.core.security.PackagedSvg
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class PackagedSvgTest {
    @Test fun shippedAssetsAreStaticAndValid(){
        val root=requireNotNull(File(requireNotNull(System.getProperty("brainybrawl.contentDir"))).parentFile)
        root.resolve("assets/images").listFiles()!!.filter{it.extension=="svg"}.forEach {
            assertTrue(PackagedSvg.read("assets/images/${it.name}",it.inputStream()).contains("<svg"))
        }
    }
    @Test fun executableExternalAndOversizedAssetsAreRejected(){
        val bad=listOf("<script/>","<image href=\"https://example.com/x\"/>","<rect onclick=\"run()\"/>","<rect fill=\"url(https://example.com/x)\"/>")
        bad.forEach{body->assertThrows(IllegalArgumentException::class.java){PackagedSvg.read("assets/test.svg","<svg xmlns=\"http://www.w3.org/2000/svg\">$body</svg>".byteInputStream())}}
        assertThrows(IllegalArgumentException::class.java){PackagedSvg.read("assets/test.svg",ByteArray(1_048_577).inputStream())}
        assertThrows(IllegalArgumentException::class.java){PackagedSvg.read("assets/../test.svg","<svg/>".byteInputStream())}
    }
}
