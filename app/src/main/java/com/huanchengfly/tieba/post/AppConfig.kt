package com.huanchengfly.tieba.post

import android.content.Context
import android.webkit.WebSettings
import com.github.gzuliyujiang.oaid.DeviceID
import com.huanchengfly.tieba.post.components.OAIDGetter
import com.huanchengfly.tieba.post.utils.packageInfo

object AppConfig {
    var inited: Boolean = false

    var isOAIDSupported: Boolean = false
    var statusCode: Int = -200
    var oaid: String = ""
    var encodedOAID: String = ""
    var isTrackLimited: Boolean = false
    var userAgent: String? = null
    var appFirstInstallTime: Long = 0L
    var appLastUpdateTime: Long = 0L

    fun init(context: Context) {
        if (!inited) {
            isOAIDSupported = DeviceID.supportedOAID(context)
            if (isOAIDSupported) {
                DeviceID.getOAID(context, OAIDGetter)
            } else {
                statusCode = -200
                isTrackLimited = false
            }
            userAgent = WebSettings.getDefaultUserAgent(context)
            appFirstInstallTime = context.packageInfo.firstInstallTime
            appLastUpdateTime = context.packageInfo.lastUpdateTime
            inited = true
        }
    }
}