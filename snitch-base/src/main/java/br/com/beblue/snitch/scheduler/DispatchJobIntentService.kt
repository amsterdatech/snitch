package br.com.beblue.snitch.scheduler

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import br.com.beblue.snitch.Snitch
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.di.Dependencies
import br.com.beblue.snitch.core.snitch

class DispatchJobIntentService : JobIntentService() {
    val logger: Logger = Dependencies.provideLogger()

    companion object {

        private const val JOB_ID = 1939
        const val TAG = "DispatchJobIntentService"

        fun enqueue(context: Context, work: Intent) {
            enqueueWork(context, DispatchJobIntentService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        logger.d(Snitch.TAG, "$TAG onHandleWork triggered")

        snitch(this)
            .flush()
            .schedule()

    }
}