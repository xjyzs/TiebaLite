package com.huanchengfly.tieba.post.network.appcenter

import android.app.Activity
import com.huanchengfly.tieba.post.BuildConfig
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.activities.BaseActivity
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.DistributeListener
import com.microsoft.appcenter.distribute.ReleaseDetails
import com.microsoft.appcenter.distribute.UpdateAction
import net.swiftzer.semver.SemVer

class MyDistributeListener : DistributeListener {
    override fun onReleaseAvailable(
        activity: Activity,
        releaseDetails: ReleaseDetails
    ): Boolean {
        val versionName = releaseDetails.shortVersion
        val newSemVer = SemVer.parse(versionName)
        val currentSemVer = SemVer.parse(BuildConfig.VERSION_NAME)
        if (newSemVer <= currentSemVer) {
            return true
        }
        val releaseNotes = releaseDetails.releaseNotes
        if (activity is BaseActivity) {
            activity.showDialog {
                setTitle(activity.getString(R.string.title_dialog_update, versionName))
                setMessage(releaseNotes)
                setCancelable(!releaseDetails.isMandatoryUpdate)
                setPositiveButton(R.string.appcenter_distribute_update_dialog_download) { _, _ ->
                    Distribute.notifyUpdateAction(UpdateAction.UPDATE)
                }
                if (!releaseDetails.isMandatoryUpdate) {
                    setNeutralButton(R.string.appcenter_distribute_update_dialog_postpone) { _, _ ->
                        Distribute.notifyUpdateAction(UpdateAction.POSTPONE)
                    }
                    setNegativeButton(R.string.button_next_time, null)
                }
            }
        }
        return true
    }

    override fun onNoReleaseAvailable(activity: Activity) {}
}