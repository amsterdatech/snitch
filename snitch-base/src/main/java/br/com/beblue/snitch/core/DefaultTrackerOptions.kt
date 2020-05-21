package br.com.beblue.snitch.core

import android.util.Log
import br.com.beblue.snitch.common.ConfigKeys
import java.util.concurrent.TimeUnit

data class DefaultTrackerOptions(
    val mobile: Boolean = true,
    val geo: Boolean = true,
    val dryRun: Boolean = false,
    val user: Boolean = true,
    val logLevel: Int = Log.INFO,
    val logEnable: Boolean = false,
    val baseUrl: String = ConfigKeys.BASE_URL,
    val batchEventsSize: Int = 15,
    val dispatchInterval: Int = 2,
    val timeUnit: TimeUnit = TimeUnit.MINUTES,
    val automaticTrackScreens: Boolean = true
)