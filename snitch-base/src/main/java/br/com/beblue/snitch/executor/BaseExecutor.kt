package br.com.beblue.snitch.executor

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class BaseExecutor : Executor<ExecutorService, Void>() {

    private val executorService: ExecutorService by lazy {
        Executors.newCachedThreadPool()
    }


    private object Holder {
        val INSTANCE = BaseExecutor()
    }

    companion object {
        private val instance: BaseExecutor by lazy { Holder.INSTANCE }
        fun get() = instance
    }


    override fun execute(runnable: () -> Unit) {
        executorService.submit(runnable)
    }

    override fun call(callable: Callable<Void>): Future<Void> {
        return executorService.submit(callable)
    }

    override fun shutdown() {
        executorService.shutdown()
    }

}