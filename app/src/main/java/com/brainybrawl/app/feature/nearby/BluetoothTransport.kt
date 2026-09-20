package com.brainybrawl.app.feature.nearby

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

sealed interface BluetoothEvent {
    data class Connected(val id:String):BluetoothEvent
    data class Received(val id:String,val message:NearbyMessage):BluetoothEvent
    data class Disconnected(val id:String):BluetoothEvent
    data object Failed:BluetoothEvent
}
data class PairedDevice(val address:String,val name:String)

/** Secure paired RFCOMM sockets; bounded length-prefixed JSON, protocol version 1. */
@SuppressLint("MissingPermission")
class BluetoothTransport(context:Context,private val scope:CoroutineScope){
    val adapter:BluetoothAdapter?=context.getSystemService(BluetoothManager::class.java)?.adapter
    private val eventsChannel=Channel<Pair<Int,BluetoothEvent>>(64)
    val events=eventsChannel.receiveAsFlow().filter{it.first==epoch}.map{it.second}
    private val connections=ConcurrentHashMap<String,Peer>()
    @Volatile private var listener:BluetoothServerSocket?=null
    @Volatile private var connecting:BluetoothSocket?=null
    private var job:Job?=null
    private val json=Json{ignoreUnknownKeys=false}
    @Volatile private var epoch=0
    private class Peer(val socket:BluetoothSocket,val outgoing:Channel<NearbyMessage>)
    fun paired():List<PairedDevice> = adapter?.bondedDevices.orEmpty().map{PairedDevice(it.address,it.name?:it.address)}.sortedBy{it.name}
    fun host(){
        close();val generation=epoch
        job=scope.launch(Dispatchers.IO){
            var owned:BluetoothServerSocket?=null
            try{
                val server=requireNotNull(adapter).listenUsingRfcommWithServiceRecord("Brainy Brawl",SERVICE);owned=server
                if(generation!=epoch)return@launch
                listener=server
                while(isActive&&generation==epoch){val socket=server.accept();if(generation!=epoch||connections.size>=7)socket.close()else attach(UUID.randomUUID().toString(),socket,generation)}
            }catch(e:CancellationException){throw e}catch(_:Exception){if(generation==epoch)eventsChannel.send(generation to BluetoothEvent.Failed)}
            finally{runCatching{owned?.close()};if(listener===owned)listener=null}
        }
    }
    fun join(address:String){
        close();val generation=epoch
        job=scope.launch(Dispatchers.IO){
            try{
                val socket=requireNotNull(adapter).getRemoteDevice(address).createRfcommSocketToServiceRecord(SERVICE)
                if(generation!=epoch){socket.close();return@launch}
                connecting=socket
                val timeout=scope.launch{delay(20_000);if(connecting===socket)runCatching{socket.close()}}
                try{socket.connect()}finally{timeout.cancel();if(connecting===socket)connecting=null}
                if(generation==epoch)attach("host",socket,generation)else socket.close()
            }catch(e:CancellationException){throw e}catch(_:Exception){if(generation==epoch)eventsChannel.send(generation to BluetoothEvent.Failed)}
        }
    }
    private suspend fun attach(id:String,socket:BluetoothSocket,generation:Int){
        val peer=Peer(socket,Channel(32));connections[id]=peer
        eventsChannel.send(generation to BluetoothEvent.Connected(id))
        scope.launch(Dispatchers.IO){
            val writer=launch{
                try{val output=DataOutputStream(socket.outputStream);for(message in peer.outgoing){val bytes=json.encodeToString(NearbyMessage.serializer(),message).toByteArray(Charsets.UTF_8);require(bytes.size in 1..MAX_BYTES);output.writeInt(bytes.size);output.write(bytes);output.flush()}}
                catch(e:CancellationException){throw e}catch(_:Exception){}finally{runCatching{socket.close()}}
            }
            try{
                val input=DataInputStream(socket.inputStream)
                while(isActive&&generation==epoch){val size=input.readInt();require(size in 1..MAX_BYTES);val bytes=ByteArray(size);input.readFully(bytes)
                    val message=json.decodeFromString<NearbyMessage>(bytes.toString(Charsets.UTF_8));require(message.version==1)
                    eventsChannel.send(generation to BluetoothEvent.Received(id,message))
                }
            }catch(e:CancellationException){throw e}catch(_:Exception){}finally{
                writer.cancel();peer.outgoing.close();runCatching{socket.close()};connections.remove(id,peer)
                if(generation==epoch)eventsChannel.send(generation to BluetoothEvent.Disconnected(id))
            }
        }
    }
    fun send(id:String,message:NearbyMessage){connections[id]?.let{if(it.outgoing.trySend(message).isFailure)disconnect(id)}}
    fun broadcast(message:NearbyMessage){connections.keys.forEach{send(it,message)}}
    fun disconnect(id:String){connections.remove(id)?.let{it.outgoing.close();runCatching{it.socket.close()}}}
    fun close(){epoch++;job?.cancel();job=null;runCatching{listener?.close()};listener=null;runCatching{connecting?.close()};connecting=null;connections.keys.toList().forEach(::disconnect)}
    companion object {const val MAX_BYTES=65_536;val SERVICE:UUID=UUID.fromString("e67c097f-4f8b-4d1c-a3bd-1e32c9231695")}
}
