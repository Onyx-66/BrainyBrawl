package com.brainybrawl.app.core.diagnostics

/** No arbitrary payloads: identifiers, answers, emails and credentials cannot enter events. */
enum class ProductEvent {
    APP_LAUNCH,AUTH_SUCCESS,AUTH_FAILURE,MODE_SELECTED,ROOM_JOIN,ROOM_LEAVE,
    MATCH_START,MATCH_END,CONNECTION_LOST,CONNECTION_RESTORED,
    PURCHASE_ATTEMPT,PURCHASE_SUCCESS,PURCHASE_FAILURE,CONTENT_ERROR
}
data class DiagnosticEvent(val sequence:Long,val event:ProductEvent)
class EventBuffer(private val capacity:Int=256){
    init{require(capacity in 1..4096)}
    private val events=ArrayDeque<DiagnosticEvent>()
    private var sequence=0L
    @Synchronized fun record(event:ProductEvent){
        if(events.size==capacity)events.removeFirst()
        events.addLast(DiagnosticEvent(++sequence,event))
    }
    @Synchronized fun snapshot():List<DiagnosticEvent> = events.toList()
    @Synchronized fun clear(){events.clear()}
}
/** Local process-only diagnostics. No external analytics vendor or transmission is enabled. */
object Diagnostics {
    val events=EventBuffer()
    fun record(event:ProductEvent)=events.record(event)
}
