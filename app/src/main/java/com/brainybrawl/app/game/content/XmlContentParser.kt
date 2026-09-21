package com.brainybrawl.app.game.content

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

/** Bundled offline content only. Never use these answers to score an online match. */
class XmlContentParser {
    fun parse(input: InputStream, expected: ContentKind): List<GameContent> {
        val bytes = input.use { source ->
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                val n = source.read(buffer)
                if (n < 0) break
                require(out.size() + n <= 12_000_000) { "Content size limit" }
                out.write(buffer, 0, n)
            }
            out.toByteArray()
        }
        val text = Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes)).toString()
        require(!text.contains("<!DOCTYPE", true) && !text.contains("<!ENTITY", true)) { "DTD is forbidden" }
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
            isExpandEntityReferences = false
        }
        val builder = factory.newDocumentBuilder()
        builder.setEntityResolver { _, _ -> throw IllegalArgumentException("External entity forbidden") }
        val root = builder.parse(ByteArrayInputStream(bytes)).documentElement
        require(root.tagName == "content" && root.getAttribute("kind") == expected.fileName)
        require(root.getAttribute("schemaVersion") == "1" && root.getAttribute("contentVersion").toInt() > 0)
        val allIds = mutableSetOf<String>()
        fun id(value: String): String {
            require(value.matches(Regex("[A-Za-z0-9_.-]+")) && allIds.add(value)) { "Duplicate/invalid content ID" }
            return value
        }
        val items = root.children("item").map { item ->
            val meta = ContentMeta(id(item.getAttribute("id")), item.getAttribute("locale"),
                Approval.valueOf(item.getAttribute("status")))
            require(meta.locale in setOf("en", "fr", "ar", "global"))
            val fields = item.children("field")
            val values = fields.associate { it.getAttribute("name") to it.textContent }
            require(fields.size == values.size) { "Duplicate field" }
            fun value(key: String) = requireNotNull(values[key]?.takeIf { it.isNotBlank() }) { "Missing $key" }
            fun nodes(parent: String, child: String) = item.children(parent).single().children(child)
            fun asset(): String = value("asset_ref").also {
                require(it.startsWith("assets/") && !it.contains("..") && !it.contains('\\'))
            }
            when (expected) {
                ContentKind.QUESTION -> {
                    val options = nodes("options", "option").map {
                        QuestionOption(id(it.getAttribute("id")), it.textContent.also { t -> require(t.isNotBlank()) }, it.boolean("correct"))
                    }
                    require(options.size == 5 && options.count { it.correct } == 1)
                    require(value("time_limit_seconds") == "20")
                    QuestionContent(meta, value("theme"), value("question"), options, value("explanation"),item.children("acceptedAnswers").flatMap{it.children("answer")}.map{it.textContent}.toSet())
                }
                ContentKind.IMAGE -> {
                    val choices = nodes("choices", "choice").map {
                        ImageChoice(id(it.getAttribute("id")), it.textContent.also { t -> require(t.isNotBlank()) },
                            it.getAttribute("points").toInt().also { p -> require(p in 0..250) }, it.boolean("correct"))
                    }
                    val pool=item.children("choices").single().getAttribute("pool")=="true"
                    if(meta.approval!=Approval.RETIRED)require(if(pool)choices.count{it.correct}>=4&&choices.count{!it.correct}>=6 else choices.size==10&&choices.count{it.correct}==4)
                    require(value("selection_count") == "4" && value("time_limit_seconds") == "30")
                    val policy = when (value("scoring_policy")) {
                        "all_selected" -> ImageScoringPolicy.ALL_SELECTED
                        "correct_only" -> ImageScoringPolicy.CORRECT_ONLY
                        "OPEN_DECISION" -> null
                        else -> error("Unknown scoring policy")
                    }
                    require(meta.approval != Approval.APPROVED || policy != null)
                    ImageContent(meta, value("theme"), value("specification"), value("prompt"), asset(), choices, value("explanation"), policy)
                }
                ContentKind.PUZZLE -> {
                    require(value("piece_count") == "96" && value("columns") == "12" && value("rows") == "8" && value("time_limit_seconds") == if(values["layout_type"]=="grid_12x8")"180" else "120")
                    val pieces = nodes("pieces", "piece").map {
                        val row = it.getAttribute("row").toInt(); val column = it.getAttribute("column").toInt()
                        val side = it.getAttribute("side")
                        require(row in 0..7 && column in 0..11 && side == if (column < 6) "LEFT" else "RIGHT")
                        val polygon = it.getAttribute("polygon").split(' ').map { p ->
                            val xy = p.split(','); require(xy.size == 2)
                            Point(xy[0].toFloat(), xy[1].toFloat()).also { q -> require(q.x in 0f..1f && q.y in 0f..1f) }
                        }
                        require(polygon.size >= 4)
                        PuzzlePiece(id(it.getAttribute("id")), id(it.getAttribute("slot")), row, column, side, it.getAttribute("rotation").toInt(), polygon,it.getAttribute("asset_ref").takeIf(String::isNotBlank)?.also{ref->require(ref.startsWith("assets/")&&!ref.contains("..")&&!ref.contains('\\'))})
                    }
                    require(pieces.size == 96 && pieces.map { it.row to it.column }.toSet().size == 96)
                    PuzzleContent(meta, asset(), pieces)
                }
                ContentKind.PRECISION -> {
                    require(value("turn_seconds") == "20" && value("streak_bonus_at_3") == "2")
                    PrecisionContent(meta, value("rotation_degrees_per_second").toDouble(), value("hot_zone_degrees").toDouble(),
                        value("speed_increment").toDouble(), value("width_increment").toDouble(), value("max_hot_zone_degrees").toDouble()).also {
                        require(it.speed > 0 && it.zoneWidth > 0 && it.zoneWidth <= it.maxZoneWidth && it.maxZoneWidth < 360 && it.speedIncrement >= 0 && it.widthIncrement >= 0)
                    }
                }
                ContentKind.ROULETTE -> {
                    val slots = nodes("slots", "slot").map { id(it.getAttribute("id")); it.getAttribute("value").toInt() }
                    require(slots.size == 20 && slots.toSet() == (1..20).toSet())
                    RouletteContent(meta, slots)
                }
                ContentKind.SCRAMBLE -> {
                    val answers = nodes("acceptedAnswers", "answer").map { it.textContent.trim() }.toSet()
                    require(answers.isNotEmpty() && answers.none { it.isBlank() })
                    require(value("time_limit_seconds").toInt() in 10..15)
                    val normalize: (String) -> List<Char> = { it.filter(Char::isLetterOrDigit).lowercase().toList().sorted() }
                    require(normalize(value("shuffled_letters")) == normalize(value("answer")))
                    val full = value("full_points").toInt(); val reduced = value("reduced_points").toInt()
                    require(full > 0 && reduced in 0 until full)
                    ScrambleContent(meta, value("theme"), value("shuffled_letters"), answers, full, reduced)
                }
                ContentKind.SORT -> {
                    require(value("points") == "1")
                    SortContent(meta, value("set_theme"), value("item_text"), value("correct_bucket"))
                }
                ContentKind.REACTION -> ReactionContent(meta, value("localization_key"), value("text"), value("trigger"), value("category"))
            }
        }
        require(items.isNotEmpty())
        return items
    }
}

private fun Element.children(name: String): List<Element> = (0 until childNodes.length)
    .mapNotNull { childNodes.item(it) as? Element }.filter { it.tagName == name }
private fun Element.boolean(name: String): Boolean = when (getAttribute(name)) {
    "true" -> true; "false" -> false; else -> error("Invalid boolean")
}
