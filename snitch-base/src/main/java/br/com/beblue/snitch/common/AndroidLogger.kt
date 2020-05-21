package br.com.beblue.snitch.common

import android.util.Log


class AndroidLogger(var logLevel: Int = Log.INFO) : Logger {
    private var enable = true

    fun enableLogging(flag: Boolean): Logger {
        enable = flag
        return this@AndroidLogger
    }

    override fun logLevel(level: Int): Logger {
        logLevel = level
        return this@AndroidLogger
    }


    override fun d(tag: String, msg: String): Int {
        return if (enable) Log.d(tag, msg) else 0
    }

    override fun d(tag: String, msg: String, tr: Throwable): Int {
        return if (enable) Log.d(tag, msg, tr) else 0
    }

    override fun e(tag: String, msg: String): Int {
        return if (enable) Log.e(tag, msg) else 0
    }

    override fun e(tag: String, msg: String, tr: Throwable): Int {
        return if (enable) Log.e(tag, msg, tr) else 0
    }

    override fun getStackTraceString(tr: Throwable): String {
        return Log.getStackTraceString(tr)
    }

    override fun i(tag: String, msg: String): Int {
        return if (enable) Log.i(tag, msg) else 0
    }

    override fun i(tag: String, msg: String, tr: Throwable): Int {
        return if (enable) Log.i(tag, msg, tr) else 0
    }

    override fun isLoggable(tag: String, level: Int): Boolean {
        return Log.isLoggable(tag, level)
    }

    override fun println(priority: Int, tag: String, msg: String): Int {
        return Log.println(priority, tag, msg)
    }

    override fun v(tag: String, msg: String): Int {
        return if (enable) Log.v(tag, msg) else 0
    }

    override fun v(tag: String, msg: String, tr: Throwable): Int {
        return if (enable) Log.v(tag, msg, tr) else 0
    }
}