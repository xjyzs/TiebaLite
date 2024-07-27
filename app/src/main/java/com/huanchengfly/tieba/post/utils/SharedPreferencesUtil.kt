package com.huanchengfly.tieba.post.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringDef
import com.huanchengfly.tieba.post.App

object SharedPreferencesUtil {
    const val SP_APP_DATA: String = "appData"
    const val SP_DRAFT: String = "draft"
    const val SP_SETTINGS: String = "settings"
    const val SP_PERMISSION: String = "permission"
    const val SP_IGNORE_VERSIONS: String = "ignore_version"
    const val SP_WEBVIEW_INFO: String = "webview_info"
    const val SP_PLUGINS: String = "plugins"

    fun get(@Preferences name: String?): SharedPreferences {
        return get(App.INSTANCE, name)
    }

    @JvmStatic
    fun get(context: Context, @Preferences name: String?): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun put(sharedPreferences: SharedPreferences, key: String?, value: String?): Boolean {
        return sharedPreferences.edit().putString(key, value).commit()
    }

    fun put(sharedPreferences: SharedPreferences, key: String?, value: Boolean): Boolean {
        return sharedPreferences.edit().putBoolean(key, value).commit()
    }

    fun put(sharedPreferences: SharedPreferences, key: String?, value: Int): Boolean {
        return sharedPreferences.edit().putInt(key, value).commit()
    }

    fun put(
        context: Context,
        @Preferences preference: String?,
        key: String?,
        value: String?
    ): Boolean {
        return put(get(context, preference), key, value)
    }

    fun put(
        context: Context,
        @Preferences preference: String?,
        key: String?,
        value: Boolean
    ): Boolean {
        return put(get(context, preference), key, value)
    }

    fun put(context: Context, @Preferences preference: String?, key: String?, value: Int): Boolean {
        return put(get(context, preference), key, value)
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        SP_APP_DATA,
        SP_IGNORE_VERSIONS,
        SP_PERMISSION,
        SP_WEBVIEW_INFO,
        SP_DRAFT,
        SP_PLUGINS
    )
    annotation class Preferences
}
