package com.huanchengfly.tieba.post.ext

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import androidx.core.content.getSystemService

val Context.progressName: String?
    get() {
        val manager = this.getSystemService<ActivityManager>() ?: return null
        val processInfo = manager.runningAppProcesses.find {
            it.pid == Process.myPid()
        }

        return processInfo?.processName
    }
