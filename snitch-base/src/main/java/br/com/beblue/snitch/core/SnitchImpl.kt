package br.com.beblue.snitch.core

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import br.com.beblue.snitch.Snitch
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.data.DeviceInfo
import br.com.beblue.snitch.data.EventStore
import br.com.beblue.snitch.data.LocalDatabaseStore
import br.com.beblue.snitch.data.PreferencesManager
import br.com.beblue.snitch.dispatcher.Dispatcher
import br.com.beblue.snitch.events.*
import br.com.beblue.snitch.executor.Executor
import br.com.beblue.snitch.scheduler.AlarmReceiver
import java.util.concurrent.*

internal class SnitchImpl(
    private val context: Context,
    val defaultOptions: DefaultTrackerOptions,
    private val deviceInfo: DeviceInfo,
    var store: EventStore,
    val preferences: PreferencesManager,
    private val dispatcher: Dispatcher,
    private val trackers: MutableList<Tracker> = mutableListOf(),
    val logger: Logger,
    var executor: Executor<ExecutorService, Void>
) : Snitch {

    companion object {
        const val ALARM_SCHEDULED = "ALARM_SCHEDULED"
    }

    @Volatile
    private var hasScheduled: Boolean = false

    private var eventStoreFuture: Future<Void>? = null
    private var schedulerReadFuture: Future<Void>? = null
    private val eventsToDelete: MutableSet<Long> = mutableSetOf()
    private var exceptionHandler: ExceptionHandler? = null

    private var lastScreenTracked: String? = null

    private val userProperties: MutableMap<String, Any> = mutableMapOf()

    private lateinit var activityCallback: SnitchActivityLifecycleCallback

    init {
        eventStoreFuture = executor.call(object : Callable<Void> {
            override fun call(): Void? {
                store = LocalDatabaseStore(context, defaultOptions.batchEventsSize)
                return null
            }
        })

        schedulerReadFuture = executor.call(object : Callable<Void> {
            override fun call(): Void? {
                hasScheduled = preferences.get(ALARM_SCHEDULED, false)
                return null
            }
        })

        waitForEventStore()
        waitForHasScheduledFlag()
        schedule()

        if (defaultOptions.automaticTrackScreens) {
            activityCallback = SnitchActivityLifecycleCallback.get(this)
            (context as Application).registerActivityLifecycleCallbacks(activityCallback)
        }
    }

    override fun schedule(): Snitch = apply {

        logger.d(Snitch.TAG, "Scheduling Alarm to trigger dispatch")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = AlarmReceiver.ACTION_ALARM
        }

        val pendingIntent = PendingIntent
            .getBroadcast(context, 0, alarmIntent, 0)

        val intervalInTimeUnits =
            SystemClock.elapsedRealtime() + defaultOptions.dispatchInterval * 60 * 1000L

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager
                .setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME,
                    intervalInTimeUnits, pendingIntent
                )
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, intervalInTimeUnits, pendingIntent)
        }

        preferences.add(ALARM_SCHEDULED, true)

    }

    private fun dispatch(callback: (List<Map<String, Any>>) -> Unit) {
        if (!deviceInfo.online()) {
            logger.d(Snitch.TAG, "Device is offline, will retry it later.")
            return
        }
        executor.execute {
            val events = store.events()

            if (events.isNotEmpty()) {
                try {
                    dispatcher.dispatch(events, callback)
                } catch (e: Exception) {
                    exceptionHandler?.onException(
                        EventNotTrackedException(dispatcher, events, e)
                    )
                }
            } else {
                logger.d(Snitch.TAG, "No events to dispatch.")
            }
        }
    }

    override fun logger(): Logger {
        return this.logger
    }

    override fun addEvent(event: Event) = apply {
        val payload = event
            .enrichMobile(deviceInfo = deviceInfo)
            .enrichNetwork(deviceInfo = deviceInfo)
            .enrichGeo(defaultOptions.geo, deviceInfo)


        when (event) {
            is Event.Screen -> {
                val screen = event.event_params[Keys.SCREEN_NAME] as? String
                lastScreenTracked = screen

                if (screen != lastScreenTracked) {
                    insertEvent(payload)
                    trackEvent(payload)
                    return@apply
                }
            }

            is Event.Device -> {
                event.enrichDevice(deviceInfo = deviceInfo)
            }
        }

        insertEvent(payload)
        trackEvent(payload)
    }

    private fun trackEvent(event: Event) {
        trackers.forEach { tracker: Tracker ->
            tracker.track(event)
        }
    }

    private fun insertEvent(payload: Event) {
        logger.d(Snitch.TAG, "Add payload to event storage: ${payload.payload()}")
        executor.execute {
            store.apply {
                insert(payload)
            }
        }
    }

    override fun flush() = apply {
        dispatch(callback = { events ->
            events.forEach {
                store.removeEvent((it["id"] as Int).toLong())
            }
        })
    }

    override fun clean() = apply {
        store.removeEvents(eventsToDelete.toMutableList()) {
            eventsToDelete.clear()
        }
    }

    override fun shutdown() {
        logger.d(Snitch.TAG, "Shutting down dispatcher.")
        executor.shutdown()
    }

    private fun waitForEventStore(): Boolean {
        val eventStoreFuture = this.eventStoreFuture
        try {
            eventStoreFuture?.get(5, TimeUnit.SECONDS)
        } catch (ie: InterruptedException) {
            logger.e(Snitch.TAG, "Event store loading has been interrupted: %s", ie)
        } catch (ee: ExecutionException) {
            logger.e(Snitch.TAG, "Event store loading has failed: %s", ee)
        } catch (te: TimeoutException) {
            logger.e(Snitch.TAG, "Event store loading has timeout: %s", te)
        }

        return eventStoreFuture?.isDone ?: false
    }

    private fun waitForHasScheduledFlag(): Boolean {
        val schedulerReadFuture = this.schedulerReadFuture

        try {
            schedulerReadFuture?.get(1, TimeUnit.SECONDS)
        } catch (ie: InterruptedException) {
            logger.e(Snitch.TAG, "Event store loading has been interrupted: %s", ie)
        } catch (ee: ExecutionException) {
            logger.e(Snitch.TAG, "Event store loading has failed: %s", ee)
        } catch (te: TimeoutException) {
            logger.e(Snitch.TAG, "Event store loading has timeout: %s", te)
        }

        return schedulerReadFuture?.isDone ?: false
    }

}