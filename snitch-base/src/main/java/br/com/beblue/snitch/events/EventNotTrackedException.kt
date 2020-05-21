package br.com.beblue.snitch.events

import br.com.beblue.snitch.dispatcher.Dispatcher

data class EventNotTrackedException(
    val dispatcher: Dispatcher,
    val events: List<Map<String, Any>>,
    val e: Exception
) : Exception("", e)

interface ExceptionHandler {
    fun onException(e: Exception)
}