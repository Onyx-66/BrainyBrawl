package com.brainybrawl.app.game.content

import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ContentRepository {
    suspend fun load(kind: ContentKind, locale: String): List<GameContent>
    suspend fun questionSample(locale:String,count:Int):List<QuestionContent> = load(ContentKind.QUESTION,locale).filterIsInstance<QuestionContent>().shuffled().take(count)
}

/** Per-kind loading bounds memory; callers own match-sized immutable lists. */
class XmlContentRepository(
    private val open: (String) -> InputStream,
    private val allowDevelopmentContent: Boolean,
    private val parser: XmlContentParser = XmlContentParser()
) : ContentRepository {
    override suspend fun questionSample(locale:String,count:Int):List<QuestionContent> = withContext(Dispatchers.IO){
        require(locale in setOf("en","fr","ar")&&count in 1..15)
        val packs=open("content/questions-index.tsv").bufferedReader().useLines{lines->lines.map{it.split('\t')}.filter{it.size==3&&it[0]==locale}.toList()}
        require(packs.isNotEmpty())
        var ticket=kotlin.random.Random.nextInt(packs.sumOf{it[2].toInt()})
        val pack=packs.first{ticket-=it[2].toInt();ticket<0}
        require(pack[1].matches(Regex("questions_(en|fr|ar)_[0-9]+[.]xml")))
        parser.parse(open("content/${pack[1]}"),ContentKind.QUESTION).filterIsInstance<QuestionContent>()
            .filter{it.meta.approval==Approval.APPROVED||(allowDevelopmentContent&&it.meta.approval==Approval.DEV_SAMPLE)}.shuffled().take(count)
    }
    override suspend fun load(kind: ContentKind, locale: String): List<GameContent> = withContext(Dispatchers.IO) {
        require(locale in setOf("en", "fr", "ar"))
        val records = parser.parse(open("content/${kind.fileName}.xml"), kind).filter {
            it.meta.approval == Approval.APPROVED || (allowDevelopmentContent && it.meta.approval == Approval.DEV_SAMPLE)
        }
        // Never silently show English content when the requested locale is missing.
        records.filter { it.meta.locale == locale || it.meta.locale == "global" }
    }
}
