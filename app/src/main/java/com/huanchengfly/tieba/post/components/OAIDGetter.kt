package com.huanchengfly.tieba.post.components

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.gzuliyujiang.oaid.IGetter
import com.huanchengfly.tieba.post.AppConfig
import com.huanchengfly.tieba.post.utils.helios.Base32

object OAIDGetter : Application.ActivityLifecycleCallbacks, IGetter {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        if (!AppConfig.inited) {
            AppConfig.isOAIDSupported = DeviceID.supportedOAID(activity)
            if (AppConfig.isOAIDSupported) {
                DeviceID.getOAID(activity, this)
            } else {
                AppConfig.inited = true
                AppConfig.statusCode = -200
                AppConfig.isTrackLimited = false
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onOAIDGetComplete(result: String) {
        AppConfig.inited = true
        AppConfig.oaid = result
        AppConfig.encodedOAID = Base32.encode(result.encodeToByteArray())
        AppConfig.statusCode = 0
        AppConfig.isTrackLimited = false
    }

    override fun onOAIDGetError(error: Exception?) {
        AppConfig.inited = true
        AppConfig.statusCode = -100
        AppConfig.isTrackLimited = true
    }
}