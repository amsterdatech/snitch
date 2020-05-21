package br.com.beblue.snitch.dispatcher

interface Dispatcher {
    val dispatcherName: String
    val init: Boolean

    fun dispatch(events: List<Map<String, Any>>, callback: (List<Map<String, Any>>) -> Unit = {})
    fun initDispatcher()
}