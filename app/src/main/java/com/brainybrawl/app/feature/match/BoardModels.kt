package com.brainybrawl.app.feature.match
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonObject
import com.brainybrawl.app.game.content.Point

@Serializable sealed interface BoardState
@Serializable @SerialName("precision_tap") data class PrecisionBoard(val config:PrecisionSettings,val players:List<PrecisionPlayer>):BoardState
@Serializable data class PrecisionSettings(@SerialName("rotation_degrees_per_second")val speed:Double,
    @SerialName("hot_zone_degrees")val width:Double,@SerialName("speed_increment")val speedIncrement:Double,
    @SerialName("width_increment")val widthIncrement:Double,@SerialName("max_hot_zone_degrees")val maxWidth:Double)
@Serializable data class PrecisionPlayer(@SerialName("user_id")val userId:String,val position:Double,
    @SerialName("anchor_at")val anchorAt:String,@SerialName("zone_start")val zoneStart:Double,val streak:Int,val score:Int)
@Serializable @SerialName("speed_sort") data class SortBoard(val index:Int,val streak:Int,val label:String,
    @SerialName("active_user")val activeUser:String,val buckets:List<String>):BoardState
@Serializable @SerialName("collaborative_puzzle") data class PuzzleBoardView(val pieces:List<PuzzleTile>,val slots:List<PuzzleSlot>,
    val placements:List<PuzzlePlacement>,val cursors:List<PuzzleCursor>,val attempts:List<PuzzleAttempt>):BoardState
@Serializable data class PuzzleTile(val id:String,val side:String,val rotation:Int,val polygon:String,@SerialName("asset_ref")val assetRef:String?=null)
@Serializable data class PuzzleSlot(val id:String,val polygon:String)
@Serializable data class PuzzlePlacement(@SerialName("piece_id")val pieceId:String,@SerialName("slot_id")val slotId:String,@SerialName("user_id")val userId:String)
@Serializable data class PuzzleCursor(@SerialName("user_id")val userId:String,val x:Float,val y:Float)
@Serializable data class PuzzleAttempt(@SerialName("user_id")val userId:String,val action:JsonObject,val correct:Boolean)
fun polygonPoints(text:String):List<Point> = text.trim().split(Regex("\\s+")).map{pair->
    val parts=pair.split(',');require(parts.size==2)
    Point(parts[0].toFloat(),parts[1].toFloat()).also{require(it.x in 0f..1f&&it.y in 0f..1f)}
}.also{require(it.size>=3)}
fun polygonContains(points:List<Point>,x:Float,y:Float):Boolean {
    var inside=false;var previous=points.last()
    for(current in points){
        if((current.y>y)!=(previous.y>y)&&x<(previous.x-current.x)*(y-current.y)/(previous.y-current.y)+current.x)inside=!inside
        previous=current
    }
    return inside
}

@Serializable @SerialName("word_scramble") data class ScrambleBoard(val answered:Boolean):BoardState
