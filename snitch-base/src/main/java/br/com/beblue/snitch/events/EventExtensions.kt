package br.com.beblue.snitch.events

import br.com.beblue.snitch.data.DeviceInfo

fun Event.enrichDevice(enable: Boolean = true, deviceInfo: DeviceInfo): Event {
    if (enable) {
        payload().apply {
            this[Mobile.DEVICE] = mutableMapOf<String, Any>()
                .apply {
                    this[Mobile.BRAND] = deviceInfo.brand()
                    this[Mobile.DENSITY] = deviceInfo.density()
                    this[Mobile.WINDOW_RESOLUTION] = deviceInfo.resolution()
                    this[Mobile.OS_VERSION] = deviceInfo.osVersion()
                    this[Mobile.OS_TYPE] = Mobile.OS_TYPE_VALUE
                    this[Mobile.DEVICE_MANUFACTURER] = deviceInfo.manufacturer()
                    this[Mobile.DEVICE_MODEL] = deviceInfo.model()
                }
        }
    }
    return this
}

fun Event.enrichMobile(enable: Boolean = true, deviceInfo: DeviceInfo): Event {
    if (enable) {
        val mobileData = mutableMapOf<String, Any>()
            .apply {
                this[Mobile.DEVICE_ID] = deviceInfo.deviceId()
                this[Mobile.LANGUAGE] = deviceInfo.language()
                this[Mobile.BATTERY] = deviceInfo.batteryPercent()

                return@apply
            }

        payload().putAll(mobileData)
    }
    return this
}

fun Event.enrichNetwork(enable: Boolean = true, deviceInfo: DeviceInfo): Event {
    val info = deviceInfo.networkInfo()

    payload()
        .apply {
            this[Mobile.NETWORK] = mutableMapOf(
                Pair(Mobile.NETWORK_TYPE, deviceInfo.networkType(info)),
                Pair(Mobile.NETWORK_STATUS, if (deviceInfo.online()) "online" else "offline")
            )
        }
    return this
}

fun Event.enrichGeo(enable: Boolean = true, deviceInfo: DeviceInfo): Event {
    if (enable) {
        val geoData = mutableMapOf<String, Any>().apply {
            deviceInfo.recentLocation()?.let { location ->
                this[Geo.LOCATION] = GeoPayload(
                    location.latitude,
                    location.longitude
                ).payload()
                return@apply
            }
        }

        this.payload().putAll(geoData)
    }
    return this
}