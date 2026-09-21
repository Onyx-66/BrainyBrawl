package com.brainybrawl.app.game.engine
import com.brainybrawl.app.game.content.Point
import kotlin.math.roundToInt
data class PuzzleEdge(val from:Point,val to:Point,val solved:Boolean)
/** Shared edges inside a correctly placed region disappear; its exterior is white. */
fun puzzleEdges(slots:Map<String,List<Point>>,placed:Set<String>):List<PuzzleEdge>{
 data class Edge(val a:Point,val b:Point,var solved:Int=0)
 fun key(p:Point)="${(p.x*1000000).roundToInt()},${(p.y*1000000).roundToInt()}"
 val edges=linkedMapOf<String,Edge>()
 slots.forEach{(id,points)->points.indices.forEach{i->
  val a=points[i];val b=points[(i+1)%points.size];val ka=key(a);val kb=key(b)
  val k=if(ka<kb)"$ka:$kb" else "$kb:$ka"
  val edge=edges.getOrPut(k){Edge(a,b)}
  if(id in placed)edge.solved++
 }}
 return edges.values.filter{it.solved<2}.map{PuzzleEdge(it.a,it.b,it.solved==1)}
}
