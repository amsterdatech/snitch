package br.com.beblue.snitch.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.WindowManager
import br.com.beblue.snitch.common.Logger
import java.io.File
import java.lang.ref.WeakReference
import java.util.*


class DeviceInfo {

    companion object {
        const val OS_NAME = "android"
        const val TAG = "DeviceInfo"


        fun with(context: Context): DeviceInfo {
            return DeviceInfo().init(context)
        }

        @JvmStatic
        fun generateUUID(): String {
            return UUID.randomUUID().toString()
        }

        @JvmStatic
        fun timestamp(): Long {
            return System.currentTimeMillis()
        }
    }

    private lateinit var contextWrapper: WeakReference<Context>
    private lateinit var logger: Logger


    private fun init(context: Context): DeviceInfo {
        contextWrapper = WeakReference(context)

        return this@DeviceInfo
    }

    private fun context(): Context? {
        return contextWrapper.get()
    }

    fun logger(log: Logger): DeviceInfo {
        log.let {
            logger = it
        }

        return this@DeviceInfo
    }


    fun osName(): String {
        return OS_NAME
    }

    fun osVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun brand(): String {
        return Build.BRAND
    }

    fun manufacturer(): String {
        return Build.MANUFACTURER
    }

    fun model(): String {
        return Build.MODEL
    }

    fun versionName(): String? {
        val packageInfo: PackageInfo?
        try {
            packageInfo = context()?.packageManager?.getPackageInfo(context()?.packageName, 0)
            return packageInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
//            logError("Failed to get version name", e)
        }

        return null
    }

    fun countryFromLocale(): String {
        return Locale.getDefault().country
    }

    fun language(): String {
        return Locale.getDefault().language
    }

    @SuppressLint("HardwareIds")
    fun deviceId(): String {
        val androidId = Settings.Secure.getString(context()?.contentResolver, Settings.Secure.ANDROID_ID)
        return UUID.nameUUIDFromBytes(androidId.toByteArray()).toString()
    }


    fun enoughSpace(minMemory: Int = 1000): Boolean {
        return context()?.let {
            return ContextCompat.getExternalFilesDirs(it, null)
                .filterNotNull()
                .let {
                    var size = 0L
                    for (item: File in it) {
                        size += item.freeSpace
                    }

                        return@let size / 1048576
                    } > minMemory
        } ?: false
    }

    fun batteryStatus(): Intent? {
        return IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context()?.registerReceiver(null, ifilter)
        }
    }


    fun batteryPercent(): Int {
        val batteryPct: Int? = batteryStatus()?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level / scale.toFloat() * 100).toInt()
        }

        return batteryPct ?: 0
    }


    fun online(): Boolean {
        val cm = context()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        cm.let {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = it.activeNetworkInfo
                ni.let { networkInfo ->
                    return networkInfo.isConnected &&
                            (networkInfo.type == ConnectivityManager.TYPE_WIFI ||
                                    networkInfo.type == ConnectivityManager.TYPE_MOBILE)
                }

            } else {
                val n = it.activeNetwork
                n.let { network ->
                    val nc = it.getNetworkCapabilities(network)

                    nc.let {
                        return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    }
                }
            }
        }
    }

    fun networkInfo(): NetworkInfo? {
        val cm = context()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var ni: NetworkInfo? = null
        try {
            val maybeNi = cm.activeNetworkInfo
            if (maybeNi != null && maybeNi.isConnected) {
                ni = maybeNi
            }
        } catch (e: SecurityException) {
            logger.e(TAG, "Security exception getting NetworkInfo: %s", e)
        }
        return ni
    }

    fun networkType(networkInfo: NetworkInfo?): String {
        var networkType = "offline"
        if (networkInfo != null) {
            val maybeNetworkType = networkInfo.typeName.toLowerCase()
            when (maybeNetworkType) {
                "mobile", "wifi" -> networkType = maybeNetworkType
                else -> {
                }
            }
        }
        return networkType
    }

    fun resolution(): String {
        var resolution = ""
        try {
            val wm = context()?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            resolution = metrics.widthPixels.toString() + "x" + metrics.heightPixels
        } catch (t: Throwable) {
            logger.d(TAG, "Device resolution cannot be determined")
        }

        return resolution
    }

    fun density(): String {
        val densityValue: String
        val density = context()?.resources?.displayMetrics?.densityDpi
        when (density) {
            DisplayMetrics.DENSITY_LOW -> densityValue = "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> densityValue = "MDPI"
            DisplayMetrics.DENSITY_TV -> densityValue = "TVDPI"
            DisplayMetrics.DENSITY_HIGH -> densityValue = "HDPI"
            DisplayMetrics.DENSITY_260 -> densityValue = "XHDPI"
            DisplayMetrics.DENSITY_280 -> densityValue = "XHDPI"
            DisplayMetrics.DENSITY_300 -> densityValue = "XHDPI"
            DisplayMetrics.DENSITY_XHIGH -> densityValue = "XHDPI"
            DisplayMetrics.DENSITY_340 -> densityValue = "XXHDPI"
            DisplayMetrics.DENSITY_360 -> densityValue = "XXHDPI"
            DisplayMetrics.DENSITY_400 -> densityValue = "XXHDPI"
            DisplayMetrics.DENSITY_420 -> densityValue = "XXHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> densityValue = "XXHDPI"
            DisplayMetrics.DENSITY_560 -> densityValue = "XXXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> densityValue = "XXXHDPI"
            else -> densityValue = "other"
        }
        return densityValue
    }

    fun recentLocation(): Location? {
        val locationManager = context()
            ?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Don't crash if the device does not have location services.

        // It's possible that the location service is running out of process
        // and the remote getProviders call fails. Handle null provider lists.
        var providers: List<String>? = null
        try {
            providers = locationManager.getProviders(true)
        } catch (e: SecurityException) {
            // failed to get providers list
            logger.e(TAG, "Failed to get most recent location", e)
        }

        if (providers == null) {
            return null
        }

        val locations = ArrayList<Location>()
        for (provider in providers) {
            var location: Location? = null
            try {
                context()?.let {
                    if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        location = locationManager.getLastKnownLocation(provider)
                    }
                }


            } catch (e: Exception) {
                logger.e(TAG, "Failed to get most recent location", e)
            }

            location?.apply {
                locations.add(this)
            }
        }

        var maximumTimestamp: Long = -1
        var bestLocation: Location? = null
        for (location in locations) {
            if (location.time > maximumTimestamp) {
                maximumTimestamp = location.time
                bestLocation = location
            }
        }

        return bestLocation
    }
}