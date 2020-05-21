package br.com.beblue.snitch.dispatcher

import android.net.TrafficStats
import br.com.beblue.snitch.Snitch
import br.com.beblue.snitch.common.ConfigKeys
import br.com.beblue.snitch.common.Logger
import br.com.beblue.snitch.di.Dependencies
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class OkHttpDispatcher : Dispatcher {

    override val dispatcherName: String = TAG

    override val init: Boolean = true

    private var httpClient: OkHttpClient = Dependencies.provideHttpClient()
    var logger: Logger = Dependencies.provideLogger()
    var serializer: Gson = Dependencies.provideSerializer()

    var remoteUrl = ""
    var timeout: Long = 5 * 1000

    override fun initDispatcher() {
    }

    override fun dispatch(events: List<Map<String, Any>>, callback: (List<Map<String, Any>>) -> Unit) {
        logger.d(Snitch.TAG, "Dispatching payload to $TAG: $events")
        TrafficStats.setThreadStatsTag(THREAD_ID)

        val mediaTypeJson = MediaType.parse(ConfigKeys.MEDIA_TYPE_JSON)

        val body = RequestBody
            .create(mediaTypeJson, serializer.toJson(events))

        val request = Request.Builder()
            .url(remoteUrl)
            .post(body)
            .addHeader("content-type", ConfigKeys.MEDIA_TYPE_JSON)
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            when (response.code()) {
                200 -> {
                    logger.d(
                        Snitch.TAG,
                        "Dispatch Success: $events , Code: ${response.code()} "
                    )
                    callback.invoke(events)
                }

                else -> {
                    logger.d(
                        Snitch.TAG,
                        "Dispatch Error: $events, Code: ${response.code()}"
                    )
                }
            }

        } catch (e: java.net.ConnectException) {
            logger.d(Snitch.TAG, "No internet connection found, unable to upload events")

        } catch (e: java.net.UnknownHostException) {
            logger.d(
                Snitch.TAG,
                "No internet connection found, unable to upload events"
            )

        } catch (e: IOException) {
            logger.d(Snitch.TAG, e.toString())

        } catch (e: Exception) {
            logger.e(Snitch.TAG, "Exception:", e)
        }
    }

    companion object {
        const val TAG = "OkHttpDispatcher"
        const val THREAD_ID = 12345678
    }

}