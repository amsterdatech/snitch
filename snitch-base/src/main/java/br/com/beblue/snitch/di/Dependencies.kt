package br.com.beblue.snitch.di

import android.content.Context
import android.util.Log
import br.com.beblue.snitch.BuildConfig
import br.com.beblue.snitch.common.AndroidLogger
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.data.DeviceInfo
import br.com.beblue.snitch.data.EventStore
import br.com.beblue.snitch.data.LocalDatabaseStore
import br.com.beblue.snitch.data.PreferencesManager
import br.com.beblue.snitch.dispatcher.Dispatcher
import br.com.beblue.snitch.dispatcher.OkHttpDispatcher
import br.com.beblue.snitch.executor.BaseExecutor
import br.com.beblue.snitch.executor.Executor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

object Dependencies {
    private lateinit var logger: Logger

    fun provideHttpLogging(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(
            when (BuildConfig.DEBUG) {
                true -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
        )
    }

    fun provideHttpClient(timeout: Long = 5L): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideHttpLogging())
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
    }

    fun provideLogger(logLevel: Int = if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO): Logger {
        if (!Dependencies::logger.isInitialized) {
            logger = AndroidLogger(logLevel = Log.INFO)
        }
        return logger
    }

    fun provideDispatcher(): Dispatcher {
        return OkHttpDispatcher()
    }

    fun provideSerializer(): Gson {
        return GsonBuilder().create()
    }

    fun provideExecutor(): Executor<ExecutorService, Void> {
        return BaseExecutor()
    }

    fun provideDeviceInfo(context: Context, logger: Logger = provideLogger()): DeviceInfo {
        return DeviceInfo
            .with(context)
            .logger(logger)
    }

    fun provideEventStore(context: Context, batchEventsSize: Int): EventStore {
        return LocalDatabaseStore(context, batchEventsSize)
    }

    fun providePreferenceManager(context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}