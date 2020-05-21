package br.com.beblue.snitch.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(val context: Context) {

    companion object {
        const val PREFERENCES_NAME = "beblue"
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    fun add(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */

    fun <T : Any> get(key: String, defaultValue: T): T {
        val applicationContext = context.applicationContext
        return when (defaultValue::class) {
            String::class -> applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getString(
                key,
                defaultValue as? String
            ) as T

            Int::class -> applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(
                key, defaultValue as? Int
                    ?: -1
            ) as T

            Boolean::class -> applicationContext.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
            ).getBoolean(key, defaultValue as Boolean) as T

            Float::class -> applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getFloat(
                key, defaultValue as? Float
                    ?: -1f
            ) as T

            Long::class -> applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getLong(
                key, defaultValue as? Long
                    ?: -1
            ) as T

            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    fun clearAll() {
        edit {
            it.clear()
        }
    }

    fun remove(key: String) {
        edit {
            it.remove(key)
        }
    }

    private fun edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        operation(editor)
        editor.apply()
    }
}