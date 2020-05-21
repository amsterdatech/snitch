package br.com.beblue.snitch.core

import br.com.beblue.snitch.events.Event

interface Tracker {
    fun track(event: Event)
}
