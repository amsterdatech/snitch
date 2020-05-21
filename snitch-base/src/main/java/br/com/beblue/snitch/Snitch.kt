package br.com.beblue.snitch

import android.content.Context
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.core.*
import br.com.beblue.snitch.events.Event

interface Snitch {

    companion object :
        Holder<Snitch, Context, SnitchBuilder.() -> Unit>(
            { context: Context, builder: SnitchBuilder.() -> Unit ->
                SnitchBuilder(context).apply(builder).build()
            }
        ) {
        const val TAG = "Snitch"
    }

    fun track(setup: EventBuilder.() -> Unit = {}) = apply {
        val eventBuilder = EventBuilder()
        eventBuilder.setup()
        addEvent(eventBuilder.build())
    }

    fun click(setup: ClickBuilder.() -> Unit = {}) = apply {
        val clickBuilder = ClickBuilder()
        clickBuilder.setup()
        addEvent(clickBuilder.build())
    }

    fun screen(name: String, setup: ScreenViewBuilder.() -> Unit = {}) = apply {
        val screenViewBuilder = ScreenViewBuilder(name)
        screenViewBuilder.setup()
        addEvent(screenViewBuilder.build())
    }

    fun pushReceived(setup: PushBuilder.() -> Unit = {}) = apply {
        val pushBuilder = PushBuilder(Event.PushReceived, setup)
        pushBuilder.setup()
        addEvent(pushBuilder.build())
    }

    fun pushClicked(setup: PushBuilder.() -> Unit = {}) = apply {
        val pushBuilder = PushBuilder(Event.PushClicked, setup)
        pushBuilder.setup()
        addEvent(pushBuilder.build())
    }

    fun pushDismissed(setup: PushBuilder.() -> Unit = {}) = apply {
        val pushBuilder = PushBuilder(Event.PushDismissed, setup)
        pushBuilder.setup()
        addEvent(pushBuilder.build())
    }

    fun device(setup: DeviceBuilder.() -> Unit = {}) = apply {
        val deviceBuilder = DeviceBuilder()
        deviceBuilder.setup()
        addEvent(deviceBuilder.build())
    }

    fun flush(): Snitch
    fun schedule(): Snitch
    fun addEvent(event: Event): Snitch
    fun shutdown()
    fun clean(): Snitch
    fun logger(): Logger
}