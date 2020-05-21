package br.com.beblue.snitch.core

import android.content.Context
import android.util.Log
import br.com.beblue.snitch.BuildConfig
import br.com.beblue.snitch.Snitch
import br.com.beblue.snitch.common.AndroidLogger
import br.com.beblue.snitch.common.ConfigKeys
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.di.Dependencies
import br.com.beblue.snitch.dispatcher.Dispatcher
import br.com.beblue.snitch.dispatcher.OkHttpDispatcher
import br.com.beblue.snitch.events.Event
import br.com.beblue.snitch.events.Keys
import br.com.beblue.snitch.events.Keys.Companion.CLICK_EVENT_NAME
import br.com.beblue.snitch.events.Keys.Companion.DEEPLINK
import br.com.beblue.snitch.events.Keys.Companion.DEVICE_EVENT_NAME
import br.com.beblue.snitch.events.Keys.Companion.EMOJI
import br.com.beblue.snitch.events.Keys.Companion.GENERIC_EVENT_NAME
import br.com.beblue.snitch.events.Keys.Companion.IMAGE
import br.com.beblue.snitch.events.Keys.Companion.MESSAGE
import br.com.beblue.snitch.events.Keys.Companion.NOTIFICATION_ID
import br.com.beblue.snitch.events.Keys.Companion.PUSH_EVENT_NAME
import br.com.beblue.snitch.events.Keys.Companion.PUSH_TYPE
import br.com.beblue.snitch.events.Keys.Companion.SCREEN_EVENT_NAME
import br.com.beblue.snitch.events.Keys.Companion.TITLE

@DslMarker
annotation class DslAnalytics

fun snitch(context: Context, initializer: SnitchBuilder.() -> Unit = {}): Snitch =
    Snitch.with(context, initializer)

fun snitch(context: Context): Snitch = Snitch.with(context) {
    log {
        enable = true
    }

    http {
        url = ConfigKeys.BASE_URL
    }
}

@DslAnalytics
class SnitchBuilder(val context: Context) {

    private val trackers = mutableListOf<Tracker>()
    private var defaults = DefaultTrackerOptions()
    private var dispatcher: Dispatcher = Dependencies.provideDispatcher()
    private var logger: Logger = Dependencies.provideLogger()

    fun build(): Snitch {
        return SnitchImpl(
            context = context,
            defaultOptions = defaults,
            deviceInfo = Dependencies.provideDeviceInfo(context),
            store = Dependencies.provideEventStore(context, defaults.batchEventsSize),
            preferences = Dependencies.providePreferenceManager(context),
            dispatcher = dispatcher,
            trackers = trackers,
            logger = logger,
            executor = Dependencies.provideExecutor()
        )
    }

    fun automaticTrackScreens(enable: Boolean) = apply {
        defaults = defaults.copy(automaticTrackScreens = enable)
    }

    fun logEnable(enable: Boolean) = apply {
        defaults = defaults.copy(logEnable = enable)
    }

    fun kit(tracker: Tracker) = apply {
        trackers += tracker
    }

    fun dryRun(enable: Boolean) = apply {
        defaults = defaults.copy(dryRun = enable)
    }

    fun dispatchInterval(intervalInMinutes: Int) = apply {
        defaults = defaults.copy(dispatchInterval = intervalInMinutes)
    }

    fun http(setup: HttpDispatcherBuilder.() -> Unit = {}) {
        val dispatcherBuilder = HttpDispatcherBuilder()
        dispatcherBuilder.setup()
        dispatcher = dispatcherBuilder.build()
    }

    fun log(setup: LoggerBuilder.() -> Unit = {}) {
        val dispatcherBuilder = LoggerBuilder()
        dispatcherBuilder.setup()
        logger = dispatcherBuilder.build()
    }
}

@DslAnalytics
open class EventBuilder(
    open var name: String = "",
    open var data: MutableMap<String, Any> = mutableMapOf(),
    open var customerId: String = ""

) {

    open fun build(): Event {
        return Event.Generic.apply {
            event_name = GENERIC_EVENT_NAME
            event_params = data
            customerId = this@EventBuilder.customerId
        }
    }

    fun put(key: String, value: Any): EventBuilder {
        if (!this.data.containsKey(key)) {
            this.data[key] = value
        }
        return this@EventBuilder
    }

    infix fun String.to(value: Any): EventBuilder {
        if (!data.containsKey(this)) {
            data[this] = value
        }
        return this@EventBuilder
    }
}

@DslAnalytics
data class ScreenViewBuilder(private var screen: String) : EventBuilder() {

    override fun build(): Event.Screen {
        return Event.Screen.apply {
            if (screen.isEmpty()) {
                throw  IllegalArgumentException("screen view events should have at least screenName ")
            }
            event_name = SCREEN_EVENT_NAME
            event_params = data
            customerId = this@ScreenViewBuilder.customerId
            put(Keys.SCREEN_NAME, screen)
        }
    }
}

@DslAnalytics
class ClickBuilder : EventBuilder() {
    override fun build(): Event.Click {
        return Event.Click.apply {
            event_name = CLICK_EVENT_NAME
            event_params = data
            customerId = this@ClickBuilder.customerId
        }
    }
}

class DeviceBuilder : EventBuilder() {
    override fun build(): Event {
        return Event.Device.apply {
            event_name = DEVICE_EVENT_NAME
            event_params = data
            customerId = this@DeviceBuilder.customerId
        }
    }

}

@DslAnalytics
class PushBuilder(
    private val type: Event,
    private val setup: PushBuilder.() -> Unit = {}
) :
    EventBuilder() {
    private val pushData: MutableMap<String, Any> = mutableMapOf()

    var notificationId: String = ""
    var pushType: String = ""
    var title: String = ""
    var message: String = ""
    var image: String = ""
    var deepLink: String = ""
    var emoji: String = ""

    override fun build(): Event {
        setup()
        pushData[NOTIFICATION_ID] = notificationId
        pushData[PUSH_TYPE] = pushType
        pushData[TITLE] = title
        pushData[MESSAGE] = message
        pushData[IMAGE] = image
        pushData[DEEPLINK] = deepLink
        pushData[EMOJI] = emoji

        val pushParams = mutableMapOf<String, Any>(Pair(PUSH_EVENT_NAME, pushData))
        data.putAll(pushParams)

        return type.apply {
            event_name = PUSH_EVENT_NAME
            event_params = data
            customerId = this@PushBuilder.customerId
        }
    }
}

@DslAnalytics
class HttpDispatcherBuilder(
    var url: String = ConfigKeys.BASE_URL,
    var httpTimeout: Long = (5 * 1000).toLong()
) {

    fun build(): OkHttpDispatcher {
        return OkHttpDispatcher().apply {
            remoteUrl = url
            timeout = httpTimeout
        }
    }
}

@DslAnalytics
class LoggerBuilder(var level: Int = Log.DEBUG, var enable: Boolean = true) {
    fun build(): Logger {
        return AndroidLogger().apply {
            this.logLevel = level
            this.enableLogging(enable)
        }
    }
}
