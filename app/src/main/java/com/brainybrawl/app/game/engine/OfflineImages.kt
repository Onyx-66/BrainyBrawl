package com.brainybrawl.app.game.engine
import com.brainybrawl.app.game.content.ImageContent

/** Practice shares the content/scoring contract, never the competitive reward ledger. */
data class OfflineImages(val images:List<ImageContent>,val startedAt:Long,val index:Int=0,val score:Int=0,
 val selected:Set<String> = emptySet(),val revealed:Boolean=false,val confirmed:Boolean=false){
 init{require(images.isNotEmpty()&&index in images.indices&&images.all{it.scoringPolicy!=null})}
 val image get()=images[index]
 val window get()=RoundWindow(startedAt,startedAt,startedAt+30_000)
 val finished get()=revealed&&index==images.lastIndex
 val possible get()=images.sumOf{i->i.choices.filter{it.correct}.sortedByDescending{it.points}.take(4).sumOf{it.points}}
 fun tick(now:Long)=if(now>=window.deadline)copy(revealed=true)else this
 fun select(id:String,now:Long):OfflineImages{
  if(revealed||!window.accepting(now))return tick(now)
  require(image.choices.any{it.id==id})
  return copy(selected=if(id in selected)selected-id else if(selected.size<4)selected+id else selected)
 }
 fun confirm(now:Long):OfflineImages{
  if(revealed||!window.accepting(now))return tick(now)
  require(selected.size==4)
  return copy(score=score+imageScore(image,selected),revealed=true,confirmed=true)
 }
 fun next(now:Long):OfflineImages{require(revealed&&!finished);return copy(startedAt=now,index=index+1,selected=emptySet(),revealed=false,confirmed=false)}
}
