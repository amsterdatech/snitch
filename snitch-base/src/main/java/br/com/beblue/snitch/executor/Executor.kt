package br.com.beblue.snitch.executor

import java.util.concurrent.Callable
import java.util.concurrent.Future

abstract class Executor<T, U> {

    abstract fun execute(runnable: () -> Unit)

    abstract fun call(callable: Callable<U>): Future<U>

    abstract fun shutdown()
}