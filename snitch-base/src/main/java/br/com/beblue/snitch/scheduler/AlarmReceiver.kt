package br.com.beblue.snitch.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.com.beblue.snitch.Snitch
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.di.Dependencies

class AlarmReceiver : BroadcastReceiver() {

    val logger: Logger = Dependencies.provideLogger()

    companion object {
        const val ACTION_ALARM = "br.com.beblue.ACTION_ALARM"
        const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        logger.d(Snitch.TAG, "$TAG onReceive triggered")
        intent?.let {
            if (it.action == ACTION_ALARM) {
                logger.d(Snitch.TAG, "$TAG enqueuing job")
                DispatchJobIntentService.enqueue(context, Intent())
            }
        }
    }
}