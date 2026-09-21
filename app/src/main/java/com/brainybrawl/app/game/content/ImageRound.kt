package com.brainybrawl.app.game.content
import kotlin.random.Random
/** Full correct pool is never a source of distractors, even for unselected correct labels. */
fun ImageContent.roundChoices(random:Random=Random.Default):ImageContent{
 val positive=choices.filter{it.correct};val negative=choices.filterNot{it.correct}
 require(positive.size>=4&&negative.size>=6)
 return copy(choices=(positive.shuffled(random).take(4)+negative.shuffled(random).take(6)).shuffled(random))
}
