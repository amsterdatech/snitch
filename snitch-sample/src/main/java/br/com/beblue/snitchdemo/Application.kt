package br.com.beblue.snitchdemo

import android.app.Application
import android.os.StrictMode
import android.util.Log
import br.com.beblue.snitch.core.snitch
import com.facebook.stetho.Stetho

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build()
        )

        snitch(this) {
            automaticTrackScreens(false)
            dryRun(true)
            dispatchInterval(1)

            http {
                url = BuildConfig.EVENT_STREAM
            }

            log {
                enable = true
            }
        }
    }
}