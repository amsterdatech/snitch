package br.com.beblue.snitch.data

import br.com.beblue.snitch.events.Event
import java.util.ArrayList

interface EventStore {
    fun insert(event: Event): Long
    fun removeEvent(id: Long): Boolean
    fun removeEvents(ids: MutableList<Long>, callback: (Int) -> Unit = {}): Boolean
    fun removeAllEvents(): Boolean
    fun queryDatabase(query: String?, orderBy: String?): List<Map<String, Any>>
    fun size(): Long
    fun getLastInsertedRowId(): Long
    fun events(): ArrayList<Map<String, Any>>
    fun event(id: Long): Map<String, Any>?
    fun allEvents(): List<Map<String, Any>>
    fun getDescEventsInRange(range: Int): List<Map<String, Any>>
}