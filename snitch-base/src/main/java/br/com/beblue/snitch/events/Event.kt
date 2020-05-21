package br.com.beblue.snitch.events

import br.com.beblue.snitch.data.DeviceInfo
import br.com.beblue.snitch.events.Keys.Companion.CLICK
import br.com.beblue.snitch.events.Keys.Companion.DEVICE
import br.com.beblue.snitch.events.Keys.Companion.GENERIC
import br.com.beblue.snitch.events.Keys.Companion.PUSH_CLICKED
import br.com.beblue.snitch.events.Keys.Companion.PUSH_DISMISSED
import br.com.beblue.snitch.events.Keys.Companion.PUSH_EVENT_NAME
import br.com.beblue.snitch.events.Keys.Companion.PUSH_RECEIVED
import br.com.beblue.snitch.events.Keys.Companion.SCREEN
import java.text.SimpleDateFormat
import java.util.*

interface Payload {
    fun payload(): MutableMap<String, Any>
}

data class GeoPayload(val lat: Double, val long: Double) : Payload {
    override fun payload(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            this[Geo.LATITUDE] = lat
            this[Geo.LONGITUDE] = long
        }
    }
}

sealed class Event(
    open var event_key: String = "",
    open var event_params: MutableMap<String, Any> = mutableMapOf(),
    open var event_name: String = ""
) :
    Payload {
    private val payload: MutableMap<String, Any> = mutableMapOf()
    private val eventId: String = DeviceInfo.generateUUID()
    private val deviceCreatedTimestamp: Long = DeviceInfo.timestamp()

    var customerId: String = ""
    var id: Int = Random().nextInt()

    override fun payload(): MutableMap<String, Any> {
        payload.apply {
            put(Keys.EVENT_ID, eventId())
            put(Keys.KEY_EVENT_TYPE, event_key)
            put(Keys.KEY_NAME, event_name)
            put(Keys.TIMESTAMP, createdAt())

            if (customerId.isNotEmpty()) {
                put(Keys.KEY_CUSTOMER_ID, customerId)
            }

            val paramsList = mutableListOf<MutableMap<String, Any>>()
            event_params.forEach {
                if (it.key == PUSH_EVENT_NAME) {
                    this[it.key] = it.value
                } else {
                    paramsList.add(mutableMapOf(Pair(it.key, it.value)))
                }
            }

            this[Keys.KEY_DATA] = paramsList
        }
        return payload
    }

    private fun eventId(): String = eventId

    private fun createdAt(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            .apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            .run {
                format(Date(deviceCreatedTimestamp))
            }
    }

    object Click : Event(event_key = CLICK)
    object Screen : Event(event_key = SCREEN)
    object Generic : Event(event_key = GENERIC)
    object Device : Event(event_key = DEVICE)
    object PushReceived : Event(event_key = PUSH_RECEIVED)
    object PushDismissed : Event(event_key = PUSH_DISMISSED)
    object PushClicked : Event(event_key = PUSH_CLICKED)
}




