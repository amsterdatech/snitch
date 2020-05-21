package br.com.beblue.snitch.common


interface Logger {
    fun d(tag: String, msg: String): Int
    fun d(tag: String, msg: String, tr: Throwable): Int
    fun e(tag: String, msg: String): Int
    fun e(tag: String, msg: String, tr: Throwable): Int
    fun getStackTraceString(tr: Throwable): String
    fun i(tag: String, msg: String): Int
    fun i(tag: String, msg: String, tr: Throwable): Int
    fun isLoggable(tag: String, level: Int): Boolean
    fun println(priority: Int, tag: String, msg: String): Int
    fun v(tag: String, msg: String): Int
    fun v(tag: String, msg: String, tr: Throwable): Int
    fun logLevel(level: Int): Logger
}