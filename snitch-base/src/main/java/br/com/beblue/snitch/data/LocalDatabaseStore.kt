package br.com.beblue.snitch.data

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.di.Dependencies
import br.com.beblue.snitch.events.Event
import com.google.gson.Gson
import java.util.*

class LocalDatabaseStore(context: Context, private var sendLimit: Int) : EventStore {
    private val TAG = LocalDatabaseStore::class.java.simpleName

    private var database: SQLiteDatabase? = null
    private var dbHelper: EventStoreHelper = EventStoreHelper(context)
    private val allColumns =
        arrayOf(
            EventStoreHelper.COLUMN_ID,
            EventStoreHelper.COLUMN_EVENT_DATA,
            EventStoreHelper.COLUMN_DATE_CREATED
        )
    private var lastInsertedRowId: Long = -1

    private var logger: Logger = Dependencies.provideLogger()
    private val serializer: Gson = Dependencies.provideSerializer()

    companion object {
        @JvmStatic
        fun joinLongList(list: List<Long>): String {
            var s = ""

            for (i in list.indices) {
                s += java.lang.Long.toString(list[i])
                if (i < list.size - 1) {
                    s += ","
                }
            }

            if (s.substring(s.length - 1) == ",") {
                s = s.substring(0, s.length - 1)
            }

            return s
        }
    }

    fun open() {
        if (!isDatabaseOpen()) {
            database = dbHelper.writableDatabase
            database!!.enableWriteAheadLogging()
        }
    }

    fun close() {
        dbHelper.close()
    }

    override fun insert(event: Event): Long {
        open()
        logger.d(TAG, "DB Path: ${database?.path}")

        if (isDatabaseOpen()) {
            val bytes = serializer.toJson(event.payload()).toByteArray(Charsets.UTF_8)
            val values = ContentValues(2)
            values.put(EventStoreHelper.COLUMN_EVENT_DATA, bytes)
            lastInsertedRowId = database!!.insert(EventStoreHelper.TABLE_EVENTS, null, values)
        }
        logger.d(TAG, "Added event to database: $lastInsertedRowId")
        return lastInsertedRowId
    }

    override fun removeEvent(id: Long): Boolean {
        var retval = -1
        if (isDatabaseOpen()) {
            retval = database!!.delete(
                EventStoreHelper.TABLE_EVENTS,
                EventStoreHelper.COLUMN_ID + "=" + id, null
            )
        }
        logger.d(TAG, "Removed event from database: $id")
        return retval == 1
    }

    override fun removeEvents(ids: MutableList<Long>, callback: (Int) -> Unit): Boolean {
        if (ids.isEmpty()) {
            return false
        }

        var retval = -1
        if (isDatabaseOpen()) {
            retval = database!!.delete(
                EventStoreHelper.TABLE_EVENTS,
                EventStoreHelper.COLUMN_ID + " in (" + joinLongList(ids) + ")", null
            )
        }
        logger.d(TAG, "Removed events from database: $retval")
        if (retval > 0) {
            callback.invoke(retval)
        }
        return retval == ids.size
    }

    override fun removeAllEvents(): Boolean {
        var retval = -1
        if (isDatabaseOpen()) {
            retval = database!!.delete(EventStoreHelper.TABLE_EVENTS, null, null)
        }
        logger.d(TAG, "Removing all events from database.");
        return retval == 0
    }

    override fun queryDatabase(query: String?, orderBy: String?): List<Map<String, Any>> {
        val res = ArrayList<Map<String, Any>>()
        if (isDatabaseOpen()) {
            val cursor = database!!.query(
                EventStoreHelper.TABLE_EVENTS,
                allColumns,
                query,
                null,
                null,
                null,
                orderBy
            )

            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val eventMetadata = HashMap<String, Any>()
                eventMetadata[EventStoreHelper.METADATA_ID] = cursor.getLong(0)
                eventMetadata[EventStoreHelper.METADATA_EVENT_DATA] = cursor.getBlob(1)
                eventMetadata[EventStoreHelper.METADATA_DATE_CREATED] = cursor.getString(2)
                cursor.moveToNext()
                res.add(eventMetadata)
            }
            cursor.close()
        }
        return res
    }

    override fun size(): Long {
        return DatabaseUtils.queryNumEntries(database, EventStoreHelper.TABLE_EVENTS)
    }

    override fun getLastInsertedRowId(): Long {
        return lastInsertedRowId
    }

    override fun events(): ArrayList<Map<String, Any>> {
        val events = ArrayList<Map<String, Any>>()

        for (eventMetadata in getDescEventsInRange(this.sendLimit)) {
            val jsonString =
                (eventMetadata[EventStoreHelper.METADATA_EVENT_DATA] as ByteArray).toString(Charsets.UTF_8)

            val event = serializer.fromJson(jsonString, Map::class.java)
            val mutableEvent = event.toMutableMap()

            mutableEvent["id"] = (eventMetadata[EventStoreHelper.METADATA_ID] as Long).toInt()

            events.add(mutableEvent.toMap() as Map<String, Any>)
        }
        return events
    }

    override fun event(id: Long): Map<String, Any>? {
        val res = queryDatabase(EventStoreHelper.COLUMN_ID + "=" + id, null)

        return if (res.isNotEmpty()) {
            res[0]
        } else {
            null
        }
    }

    override fun allEvents(): List<Map<String, Any>> {
        return queryDatabase(null, null)
    }

    override fun getDescEventsInRange(range: Int): List<Map<String, Any>> {
        return queryDatabase(null, "dateCreated ASC LIMIT $range")
    }

    private fun isDatabaseOpen(): Boolean {
        return database != null && database!!.isOpen
    }
}



