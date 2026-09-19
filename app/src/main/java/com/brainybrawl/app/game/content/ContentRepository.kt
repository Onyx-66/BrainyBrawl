package com.brainybrawl.app.game.content

import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ContentRepository {
    suspend fun load(kind: ContentKind, locale: String): List<GameContent>
}

/** Per-kind loading bounds memory; callers own match-sized immutable lists. */
class XmlContentRepository(
    private val open: (String) -> InputStream,
    private val allowDevelopmentContent: Boolean,
    private val parser: XmlContentParser = XmlContentParser()
) : ContentRepository {
    override suspend fun load(kind: ContentKind, locale: String): List<GameContent> = withContext(Dispatchers.IO) {
        require(locale in setOf("en", "fr", "ar"))
        val records = parser.parse(open("content/${kind.fileName}.xml"), kind).filter {
            it.meta.approval == Approval.APPROVED || (allowDevelopmentContent && it.meta.approval == Approval.DEV_SAMPLE)
        }
        // Never silently show English content when the requested locale is missing.
        records.filter { it.meta.locale == locale || it.meta.locale == "global" }
    }
}
