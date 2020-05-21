package br.com.beblue.snitch.events

import android.support.annotation.StringDef


class Mobile {
    companion object {
        const val DEVICE = "device"
        const val DEVICE_ID = "deviceId"
        const val LANGUAGE = "language"
        const val NETWORK_STATUS = "status"
        const val NETWORK = "network"
        const val BRAND = "brand"
        const val DENSITY = "density"
        const val WINDOW_RESOLUTION = "resolution"


        const val CARRIER = "carrier"
        const val DEVICE_MODEL = "deviceModel"
        const val DEVICE_MANUFACTURER = "manufacturer"
        const val OS_VERSION = "deviceOs"
        const val OS_TYPE = "osType"
        const val NETWORK_TYPE = "type"
        const val BATTERY = "battery"

        const val OS_TYPE_VALUE = "android"
    }

    @StringDef(
        DEVICE_ID,
        LANGUAGE,
        NETWORK_STATUS,
        BRAND,
        DENSITY,
        WINDOW_RESOLUTION,
        CARRIER,
        DEVICE_MODEL,
        DEVICE_MANUFACTURER,
        OS_VERSION,
        OS_TYPE,
        NETWORK_TYPE,
        BATTERY
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class MobileKey

}

class Geo {
    companion object {
        const val LOCATION = "location"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }

    @StringDef(LOCATION, LATITUDE, LONGITUDE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class GeoKey

}

class UserProperties {
    companion object {
        const val USER = "user"
        const val CUSTOMER_ID = "customer_id"
        const val TAX_ID = "tax_id"
        const val CUSTOMER_NAME = "name"
        const val GENDER = "gender"

    }

    @StringDef(CUSTOMER_ID, TAX_ID, CUSTOMER_NAME, GENDER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class UserKey

}

class Keys {

    companion object {

        //base event
        const val EVENT_ID = "eventId"
        const val TIMESTAMP = "occurred"
        const val KEY_DATA = "parameters"
        const val KEY_NAME = "name"
        const val KEY_EVENT_TYPE = "recordType"
        const  val KEY_CUSTOMER_ID = "customerId"

        //names
        const val GENERIC_EVENT_NAME = "generic"
        const val DEVICE_EVENT_NAME = "device"
        const val SCREEN_EVENT_NAME = "screen"
        const val CLICK_EVENT_NAME = "click"
        const val PUSH_EVENT_NAME = "push"


        //recordTypes
        const val SCREEN = "SCREEN_VIEW"
        const val PUSH_RECEIVED = "PUSH_RECEIVED"
        const val PUSH_CLICKED = "PUSH_CLICKED"
        const val PUSH_DISMISSED = "PUSH_DISMISSED"
        const val GENERIC = "GENERIC"
        const val CLICK = "CLICK"
        const val DEVICE = "DEVICE"


        // Screen Context
        const val SCREEN_NAME = "screenName"

        //Click Context
        const val KEY_CATEGORY = "category"
        const val KEY_ACTION = "action"
        const val KEY_LABEL = "label"

        //Push Context
        const val NOTIFICATION_ID: String = "notificationId"
        const val PUSH_TYPE: String = "pushType"
        const val TITLE: String = "title"
        const val MESSAGE: String = "message"
        const val IMAGE: String = "image"
        const val DEEPLINK: String = "deepLink"
        const val EMOJI: String = "emoji"
    }
}