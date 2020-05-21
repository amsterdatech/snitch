package br.com.beblue.snitch.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import br.com.beblue.snitch.Snitch
import java.util.concurrent.TimeUnit

class SnitchActivityLifecycleCallback(val snitch: Snitch) : Application.ActivityLifecycleCallbacks {

    private val state: ScreenState = ScreenState()

    private var numberOfActivities = 0
    private var startTime = 0L

    companion object {

        fun get(snitch: Snitch): SnitchActivityLifecycleCallback {
            return SnitchActivityLifecycleCallback(snitch)
        }

        const val NOTIFY_BACKGROUND = "notify_background"
        const val IN_BACKGROUND = "In background"

    }

    override fun onActivityPaused(activity: Activity) {
        state.updateWithActivity(activity)

        numberOfActivities--
        if (numberOfActivities == 0) {
            val appUsedTime = System.currentTimeMillis() - startTime
            val timeString = String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(appUsedTime),
                TimeUnit.MILLISECONDS.toMinutes(appUsedTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(appUsedTime) % TimeUnit.MINUTES.toSeconds(1)
            )

            snitch.logger().d(Snitch.TAG, "Foreground Time $timeString")

            val splitedTime = timeString.split(":")

            snitch(context = (activity as Context).applicationContext).track {
                "name" to "session_duration"
                put("seconds", splitedTime[2])
                put("minutes", splitedTime[1])
                put("hours", splitedTime[0])

            }
        }

        snitch(context = (activity as Context).applicationContext)
            .screen(NOTIFY_BACKGROUND)

        snitch.logger().d(Snitch.TAG, IN_BACKGROUND)
    }

    override fun onActivityResumed(activity: Activity) {
        if (numberOfActivities == 0) {
            startTime = System.currentTimeMillis()
        }
        numberOfActivities++

        state.updateWithActivity(activity)
        snitch(context = (activity as Context).applicationContext)
            .screen(state.name.split(".").last().snakeCase())
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

}