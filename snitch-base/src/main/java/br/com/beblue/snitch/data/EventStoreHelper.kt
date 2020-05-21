package br.com.beblue.snitch.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EventStoreHelper constructor(context: Context) :
        SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val TAG = EventStoreHelper::class.java.name

        const val TABLE_EVENTS = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_EVENT_DATA = "eventData"
        const val COLUMN_DATE_CREATED = "dateCreated"

        const val METADATA_ID = "id"
        const val METADATA_EVENT_DATA = "eventData"
        const val METADATA_DATE_CREATED = "dateCreated"

        private const val DATABASE_NAME = "snitchEvents.sqlite"
        private const val DATABASE_VERSION = 1
    }


    private val queryDropTable =
            "DROP TABLE IF EXISTS '$TABLE_EVENTS'"
    private val queryCreateTable = "CREATE TABLE IF NOT EXISTS 'events' " +
            "(id INTEGER PRIMARY KEY, eventData BLOB, " +
            "dateCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"


    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(queryCreateTable)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //        Logger.d(TAG, "Upgrade not implemented, resetting database...");
        database.execSQL(queryDropTable)
        onCreate(database)
    }

}