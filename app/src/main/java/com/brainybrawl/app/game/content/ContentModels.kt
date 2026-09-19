package com.brainybrawl.app.game.content

enum class ContentKind(val fileName: String) {
    QUESTION("question_round"), IMAGE("image_guess"), PUZZLE("collaborative_puzzle"),
    PRECISION("precision_tap"), ROULETTE("roll_the_dice"), SCRAMBLE("word_scramble"),
    SORT("speed_sort"), REACTION("reactions")
}
enum class Approval { DEV_SAMPLE, DRAFT, REVIEW, APPROVED, RETIRED }
data class ContentMeta(val id: String, val locale: String, val approval: Approval)
sealed interface GameContent { val meta: ContentMeta }
data class QuestionOption(val id: String, val label: String, val correct: Boolean)
data class QuestionContent(override val meta: ContentMeta, val theme: String, val prompt: String,
    val options: List<QuestionOption>, val explanation: String,val acceptedAnswers:Set<String> = emptySet()) : GameContent

enum class ImageScoringPolicy { ALL_SELECTED, CORRECT_ONLY }
data class ImageChoice(val id: String, val label: String, val points: Int, val correct: Boolean)
data class ImageContent(override val meta: ContentMeta, val theme: String, val specification: String,
    val prompt: String, val assetRef: String, val choices: List<ImageChoice>, val explanation: String,
    val scoringPolicy: ImageScoringPolicy?) : GameContent

data class Point(val x: Float, val y: Float)
data class PuzzlePiece(val id: String, val slot: String, val row: Int, val column: Int,
    val side: String, val rotation: Int, val polygon: List<Point>, val assetRef:String?=null)
data class PuzzleContent(override val meta: ContentMeta, val assetRef: String,
    val pieces: List<PuzzlePiece>) : GameContent

data class PrecisionContent(override val meta: ContentMeta, val speed: Double, val zoneWidth: Double,
    val speedIncrement: Double, val widthIncrement: Double, val maxZoneWidth: Double) : GameContent

data class RouletteContent(override val meta: ContentMeta, val slots: List<Int>) : GameContent

data class ScrambleContent(override val meta: ContentMeta, val theme: String, val letters: String,
    val acceptedAnswers: Set<String>, val fullPoints: Int, val reducedPoints: Int) : GameContent

data class SortContent(override val meta: ContentMeta, val theme: String, val label: String,
    val bucket: String) : GameContent

data class ReactionContent(override val meta: ContentMeta, val key: String, val text: String,
    val trigger: String, val category: String) : GameContent
