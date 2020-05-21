package br.com.beblue.snitch.common

import android.support.annotation.StringDef

class ConfigKeys {
    companion object {
        const val BASE_URL = "http://tracker-test.beblue.com.br/generic_stream"
        const val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
    }


    @StringDef(BASE_URL, MEDIA_TYPE_JSON)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ConfigKey
}