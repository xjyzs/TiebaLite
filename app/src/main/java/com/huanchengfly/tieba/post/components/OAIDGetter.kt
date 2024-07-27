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
        with(AppConfig) {
            if (!inited) {
                isOAIDSupported = DeviceID.supportedOAID(activity)
                if (isOAIDSupported) {
                    DeviceID.getOAID(activity, this@OAIDGetter)
                } else {
                    inited = true
                    statusCode = -200
                    isTrackLimited = false
                }
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onOAIDGetComplete(result: String) {
        AppConfig.apply {
            inited = true
            oaid = result
            encodedOAID = Base32.encode(result.encodeToByteArray())
            statusCode = 0
            isTrackLimited = false
        }
    }

    override fun onOAIDGetError(error: Exception?) {
        AppConfig.apply {
            inited = true
            statusCode = -100
            isTrackLimited = true
        }
    }
}