package com.brainybrawl.app
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.*
import org.junit.Test
class LocalizationContractTest {
    private fun strings(locale:String):Map<String,String>{
        val root=requireNotNull(File(requireNotNull(System.getProperty("brainybrawl.contentDir"))).parentFile)
        val doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(root.resolve("app/src/main/res/values$locale/strings.xml"))
        val nodes=doc.getElementsByTagName("string")
        return (0 until nodes.length).associate { val n=nodes.item(it);n.attributes.getNamedItem("name").nodeValue to n.textContent }
    }
    @Test fun allLocalesHaveIdenticalKeysAndNamedPlaceholders(){
        val master=strings("")
        for(locale in listOf("-fr","-ar")){
            val translated=strings(locale);assertEquals(master.keys,translated.keys)
            master.forEach{(key,value)->
                fun placeholders(text:String)=Regex("\\{[A-Za-z][A-Za-z0-9_]*\\}").findAll(text).map{it.value}.toList().sorted()
                assertEquals(key,placeholders(value),placeholders(translated.getValue(key)))
                assertTrue(translated.getValue(key).isNotBlank())
            }
        }
        assertTrue(strings("-ar").getValue("home").any{it in '\u0600'..'\u06ff'})
    }
}
