package com.huanchengfly.tieba.post.ext

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.core.content.getSystemService

val Context.progressName: String?
    get() {
        if (Build.VERSION.SDK_INT >= 28) {
            return Application.getProcessName()
        }
        val activityManager = this.getSystemService<ActivityManager>()!!
        val myPid = Process.myPid()
        val processInfo = activityManager.runningAppProcesses.find {
            it.pid == myPid
        }
        return processInfo?.processName
    }
