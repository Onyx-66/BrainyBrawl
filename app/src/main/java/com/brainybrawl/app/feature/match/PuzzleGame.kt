package com.brainybrawl.app.feature.match

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.testTag
import kotlin.math.roundToInt
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import com.brainybrawl.app.game.content.Point
import com.brainybrawl.app.ui.theme.*
import kotlinx.serialization.json.*

private fun shape(points:List<Point>,width:Float,height:Float)=Path().apply{
    points.forEachIndexed{index,p->if(index==0)moveTo(p.x*width,p.y*height)else lineTo(p.x*width,p.y*height)};close()
}
@Composable fun PuzzleGame(board:PuzzleBoardView,asset:String?,seat:Int,userId:String?,enabled:Boolean,
    onPlace:(String,String,Int)->Unit,onCursor:(Float,Float)->Unit,solo:Boolean=false){
    Text(stringResource(if(solo)R.string.offline_puzzle else R.string.collaborative_puzzle),style=MaterialTheme.typography.headlineSmall)
    Text(stringResource(if(solo)R.string.offline_puzzle_instruction else R.string.puzzle_instruction))
    if(asset==null){Text(stringResource(R.string.image_unavailable));return}
    val artwork by rememberArtwork(asset)
    val bitmap=(artwork as? ArtworkState.Ready)?.bitmap
    if(bitmap==null){if(artwork==ArtworkState.Failed)Text(stringResource(R.string.image_unavailable))else LinearProgressIndicator(Modifier.fillMaxWidth());return}
    val image=remember(bitmap){bitmap.asImageBitmap()}
    val ordered=remember(board.pieces,asset,userId,solo){board.pieces.filter{solo||it.side==if(seat==0)"LEFT" else "RIGHT"}.shuffled(kotlin.random.Random((asset+userId).hashCode()))}
    val remaining=ordered.filter{tile->board.placements.none{it.pieceId==tile.id}}
    var selected by remember(asset){mutableStateOf<String?>(null)}
    var rotation by remember(selected){mutableIntStateOf(0)}
    var cursor by remember{mutableStateOf<Offset?>(null)}
    LaunchedEffect(remaining.map{it.id}){if(remaining.none{it.id==selected})selected=remaining.firstOrNull()?.id}
    val slots=remember(board.slots){board.slots.associate{it.id to polygonPoints(it.polygon)}}
    val tiles=remember(board.pieces){board.pieces.associate{it.id to polygonPoints(it.polygon)}}
    fun place(x:Float,y:Float){if(enabled)selected?.let{piece->slots.entries.firstOrNull{polygonContains(it.value,x,y)}?.let{onPlace(piece,it.key,rotation)}}}
    Box(Modifier.fillMaxWidth().aspectRatio(1.5f).testTag("puzzle-board")){
        Canvas(Modifier.fillMaxSize().pointerInput(selected,rotation,enabled,slots){
            detectTapGestures{if(enabled)place(it.x/size.width,it.y/size.height)}
        }.pointerInput(selected,rotation,enabled,slots){
            if(enabled)detectDragGestures(onDragStart={cursor=it},onDragCancel={cursor=null},
                onDragEnd={cursor?.let{place(it.x/size.width,it.y/size.height)};cursor=null},
                onDrag={change,amount->change.consume();cursor=(cursor?:change.position)+amount;cursor?.let{onCursor(it.x/size.width,it.y/size.height)}})
        }){
            drawRect(Color.White)
            drawImage(image,dstSize=IntSize(size.width.roundToInt(),size.height.roundToInt()),alpha=.25f)
            board.slots.forEach{slot->
                val path=shape(slots.getValue(slot.id),size.width,size.height)
                val placed=board.placements.any{it.slotId==slot.id}
                if(placed)clipPath(path){drawImage(image,dstSize=IntSize(size.width.toInt(),size.height.toInt()))}
                drawPath(path,if(placed)Positive else Ink.copy(alpha=.85f),style=Stroke(1.dp.toPx()))
            }
            board.attempts.filter{!it.correct}.forEach{attempt->slots[attempt.action["slot_id"]?.jsonPrimitive?.contentOrNull]?.let{drawPath(shape(it,size.width,size.height),Negative.copy(alpha=.65f))}}
            board.cursors.filter{it.userId!=userId}.forEach{drawCircle(Gold,6.dp.toPx(),Offset(it.x*size.width,it.y*size.height))}
            cursor?.let{drawCircle(Cyan,9.dp.toPx(),it)}
        }
    }
    Text(namedString(R.string.puzzle_progress,"count" to board.placements.size,"total" to board.pieces.size))
    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){
        items(remaining,key={it.id}){tile->
            val label=namedString(R.string.puzzle_piece,"number" to (ordered.indexOf(tile)+1))
            val pieceState=tile.assetRef?.let{rememberArtwork(it).value}
            val pieceImage=(pieceState as? ArtworkState.Ready)?.bitmap?.asImageBitmap()
            val points=tiles.getValue(tile.id)
            val minX=points.minOf{it.x};val minY=points.minOf{it.y};val maxX=points.maxOf{it.x};val maxY=points.maxOf{it.y}
            val normalized=remember(points){points.map{Point((it.x-minX)/(maxX-minX),(it.y-minY)/(maxY-minY))}}
            Canvas(Modifier.size(72.dp).semantics{contentDescription=label;this.selected=selected==tile.id;role=Role.Button}
                .clickable(enabled=enabled){selected=tile.id}){
                val path=shape(normalized,size.width,size.height)
                rotate((if(selected==tile.id)rotation else 0).toFloat()-tile.rotation){
                clipPath(path){
                    if(pieceImage!=null)drawImage(pieceImage,dstSize=IntSize(size.width.roundToInt(),size.height.roundToInt()))
                    else drawImage(image,srcOffset=IntOffset((minX*image.width).roundToInt(),(minY*image.height).roundToInt()),
                        srcSize=IntSize(((maxX*image.width).roundToInt()-(minX*image.width).roundToInt()).coerceAtLeast(1),((maxY*image.height).roundToInt()-(minY*image.height).roundToInt()).coerceAtLeast(1)),
                        dstSize=IntSize(size.width.roundToInt(),size.height.roundToInt()))
                }
                drawPath(path,if(selected==tile.id)Gold else Cyan,style=Stroke(if(selected==tile.id)3.dp.toPx()else 1.dp.toPx()))
                }
            }
        }
    }
    BrawlButton(namedString(R.string.rotate_piece,"degrees" to rotation),{rotation=(rotation+90)%360},enabled=enabled&&selected!=null)
    // An accessible slot selector complements gestures; it still submits a server-validated placement.
    var slotPicker by remember{mutableStateOf(false)}
    BrawlButton(stringResource(R.string.choose_puzzle_slot),{slotPicker=true},enabled=enabled&&selected!=null)
    if(slotPicker)AlertDialog(onDismissRequest={slotPicker=false},title={Text(stringResource(R.string.choose_puzzle_slot))},text={
        androidx.compose.foundation.lazy.LazyColumn(Modifier.heightIn(max=320.dp)){
            items(board.slots,key={it.id}){slot->TextButton({selected?.let{onPlace(it,slot.id,rotation)};slotPicker=false},enabled=enabled){Text(namedString(R.string.puzzle_slot,"number" to board.slots.indexOf(slot)+1))}}
        }
    },confirmButton={TextButton({slotPicker=false}){Text(stringResource(R.string.cancel))}})
}
