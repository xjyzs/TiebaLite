package com.huanchengfly.tieba.post.utils

import androidx.core.content.edit

object JobServiceUtil {
    fun getJobId(): Int {
        val sp = SharedPreferencesUtil.get(SharedPreferencesUtil.SP_APP_DATA)
        var jobId = sp.getInt("jobId", -1)
        if (jobId == -1) {
            jobId = (Math.random() * (99999 + 1)).toInt()
            sp.edit {
                putInt("jobId", jobId)
            }
        }
        return jobId
    }
}
