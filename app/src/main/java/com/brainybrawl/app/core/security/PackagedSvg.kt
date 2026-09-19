package com.brainybrawl.app.core.security

import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import javax.xml.parsers.DocumentBuilderFactory

object PackagedSvg {
    fun read(reference:String,input:InputStream):String = input.use { source ->
        require(reference.matches(Regex("assets/[A-Za-z0-9_/.-]+\\.svg")) && reference.split('/').none{it==".."})
        val out=ByteArrayOutputStream()
        val buffer=ByteArray(8192)
        while(true){val n=source.read(buffer);if(n<0)break;require(out.size()+n<=1_048_576);out.write(buffer,0,n)}
        val text=Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(out.toByteArray())).toString()
        require(!text.contains("<!DOCTYPE",true)&&!text.contains("<!ENTITY",true))
        val factory=DocumentBuilderFactory.newInstance().apply{isNamespaceAware=true;isExpandEntityReferences=false}
        val builder=factory.newDocumentBuilder().apply{setEntityResolver{_,_->throw IllegalArgumentException("External entity")}}
        val document=builder.parse(ByteArrayInputStream(out.toByteArray()))
        val allowed=setOf("svg","g","defs","path","rect","circle","ellipse","line","polyline","polygon","linearGradient","radialGradient","stop","clipPath","title","desc","text","tspan")
        val nodes=document.getElementsByTagName("*")
        for(index in 0 until nodes.length){
            val node=nodes.item(index)
            require(node.namespaceURI=="http://www.w3.org/2000/svg"&&node.localName in allowed)
            for(a in 0 until node.attributes.length){
                val attribute=node.attributes.item(a)
                val name=attribute.localName.lowercase()
                require(!name.startsWith("on")&&name !in setOf("href","src","style"))
                val value=attribute.nodeValue
                require(!value.contains("url",true) || value.matches(Regex("url\\(#[A-Za-z0-9_.-]+\\)")))
            }
        }
        text
    }
}
