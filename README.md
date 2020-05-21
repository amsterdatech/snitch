# Snitch Analytics Tracker

This library demonstrates how you could decouple analytics libraries from your business logic code, effectively allowing you to:
* Add & remove analytics services quickly
* Add & remove events quickly
* Change event names and parameters according to the required kit

## Quick Start Guide

### Add To Gradle
Add library to your gradle module


## Kotlin Implementation Example
Initiate analytics and send events

```kotlin
//init tracker
snitch(this) {
    http {
        url = BuildConfig.EVENT_STREAM
    }

    log {
        enable = true
        level = Log.VERBOSE
    }
}

//custom event
snitch(this).track {
   name("ProductPosDetails_view")
        .put("item_id", 424242L)
        .put("item_name", "")
        .put("price", "")
        .put("percentage", 10.0)
        .put("time_spent", Calendar.getInstance())
}

//screen event
snitch(this).screen {
    screenName(MainActivity::class.java.simpleName.snakeCase())
}
```

## SDK used as references

* [Countly](https://github.com/Countly/countly-sdk-android)

* [Segmentio](https://github.com/segmentio/analytics-android)
* [Snowplow](https://github.com/snowplow/snowplow-android-tracker)
* [Mixpanel](https://github.com/mixpanel/mixpanel-android)
* [Amplitude](https://github.com/amplitude/Amplitude-Android)

* [Delta DNA](https://github.com/deltaDNA/android-sdk)
* [Matomo aka Piwik](https://github.com/matomo-org/matomo-sdk-android (Piwik))
* [EAnalytics](https://github.com/EulerianTechnologies/eanalytics-android)
* [OneSignal](https://github.com/OneSignal/OneSignal-Android-SDK)

## Improvements

* Implementation of common tracker like Firebase, Facebook, Mixpanel, ComScore and such
* Room for Database  and removing SqliteOpenHelper
* WorkManager for background deferrable jobs and removing Alarms
* Coroutines for Async Code and removing executors service
* Koin/Dagger as DI instead of service locator/factory

## References



