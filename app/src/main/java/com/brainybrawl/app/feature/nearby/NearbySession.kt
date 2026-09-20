package com.brainybrawl.app.feature.nearby

import android.content.Context
import android.os.SystemClock
import com.brainybrawl.app.game.content.ContentRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/** Screen-owned session. Never grants ranked results, wallet changes or server identity. */
class NearbySession(context:Context,private val scope:CoroutineScope,private val content:ContentRepository,private val name:String){
    val transport=BluetoothTransport(context,scope)
    private val mutable=MutableStateFlow<NearbyState?>(null);val state=mutable.asStateFlow()
    var isHost=false;private set
    var myId="host";private set
    private val statusMutable=MutableStateFlow("idle");val status=statusMutable.asStateFlow()
    private var game:NearbyGame?=null
    private var started:Job?=null
    private var lastHostMessage=0L
    private val pending=mutableMapOf<String,Long>()
    init{
        scope.launch{transport.events.collect{event->when(event){
            is BluetoothEvent.Connected->{
                if(isHost)pending[event.id]=SystemClock.elapsedRealtime()
                else{lastHostMessage=SystemClock.elapsedRealtime();transport.send("host",NearbyMessage(type="hello",name=name))}
            }
            is BluetoothEvent.Received->receive(event.id,event.message)
            is BluetoothEvent.Disconnected->{
                pending.remove(event.id)
                if(isHost){if(game?.state?.players?.any{it.id==event.id}==true){game?.leave(event.id);if(game?.state?.phase=="disconnected")statusMutable.value="disconnected";publish()}}
                else{statusMutable.value="disconnected";mutable.value=mutable.value?.copy(phase="disconnected")}
            }
            BluetoothEvent.Failed->{game=null;isHost=false;transport.close();statusMutable.value="failed";mutable.value=mutable.value?.copy(phase="disconnected")}
        }}}
        scope.launch{while(isActive){delay(1_000);val now=SystemClock.elapsedRealtime()
            if(isHost){pending.filterValues{now-it>10_000}.keys.toList().forEach{pending.remove(it);transport.disconnect(it)};game?.tick(now);publish()}
            else if(statusMutable.value in setOf("connected","connecting")&&now-lastHostMessage>if(statusMutable.value=="connecting")25_000 else 12_000){transport.close();statusMutable.value="disconnected";mutable.value=mutable.value?.copy(phase="disconnected")}
        }}
    }
    fun host(mode:NearbyMode){leave();isHost=true;myId="host";game=NearbyGame(mode,name);mutable.value=game!!.state;statusMutable.value="connected";transport.host()}
    fun join(address:String){leave();isHost=false;myId="";statusMutable.value="connecting";lastHostMessage=SystemClock.elapsedRealtime();transport.join(address)}
    private fun receive(id:String,message:NearbyMessage){
        if(isHost){
            when(message.type){
                "hello"->{if(pending.remove(id)!=null&&game?.join(id,message.name)==true){transport.send(id,NearbyMessage(type="welcome",playerId=id));publish()}else transport.disconnect(id)}
                "ready"->{game?.ready(id,message.ready);publish()}
                "answer"->{game?.answer(id,message.round,message.answer,SystemClock.elapsedRealtime());publish()}
                else->transport.disconnect(id)
            }
        }else when(message.type){
            "welcome"->{if(message.playerId.length !in 1..64)return;myId=message.playerId;statusMutable.value="connected";lastHostMessage=SystemClock.elapsedRealtime()}
            "state"->{val next=message.state?:return
                if(next.players.size !in 1..8||next.players.any{it.name.length>24}||next.prompt.length>4000||next.options.size>10||next.options.any{it.label.length>2000}){transport.close();statusMutable.value="failed";return}
                mutable.value=next;if(next.phase=="disconnected")statusMutable.value="disconnected";lastHostMessage=SystemClock.elapsedRealtime()
            }
            else->{transport.close();statusMutable.value="failed"}
        }
    }
    private fun publish(){game?.let{mutable.value=it.state;transport.broadcast(NearbyMessage(type="state",state=it.state))}}
    fun ready(value:Boolean){if(isHost)game?.ready("host",value)else transport.send("host",NearbyMessage(type="ready",ready=value));if(isHost)publish()}
    fun answer(answer:String){val round=mutable.value?.round?:return;if(isHost){game?.answer("host",round,answer,SystemClock.elapsedRealtime());publish()}else transport.send("host",NearbyMessage(type="answer",round=round,answer=answer))}
    fun start(locale:String){
        val current=game?:return;if(!isHost||!current.canStart||started?.isActive==true)return
        started=scope.launch{
            try{val questions=content.questionSample(locale,15);if(game===current){if(!current.start(questions,SystemClock.elapsedRealtime()))statusMutable.value="content_failed";publish()}}
            catch(e:CancellationException){throw e}catch(_:Exception){statusMutable.value="content_failed"}
        }
    }
    fun leave(){started?.cancel();transport.close();game=null;pending.clear();mutable.value=null;statusMutable.value="idle";isHost=false;myId="host"}
}
