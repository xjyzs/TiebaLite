package com.huanchengfly.tieba.post

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatDelegate
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.SketchFactory
import com.github.panpf.sketch.decode.GifAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.GifMovieDrawableDecoder
import com.github.panpf.sketch.decode.HeifAnimatedDrawableDecoder
import com.github.panpf.sketch.decode.WebpAnimatedDrawableDecoder
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDrawableDecodeInterceptor
import com.huanchengfly.tieba.post.components.ClipBoardLinkDetector
import com.huanchengfly.tieba.post.components.OAIDGetter
import com.huanchengfly.tieba.post.ext.initAppContext
import com.huanchengfly.tieba.post.ext.progressName
import com.huanchengfly.tieba.post.network.appcenter.MyDistributeListener
import com.huanchengfly.tieba.post.ui.common.theme.ThemeDelegate
import com.huanchengfly.tieba.post.ui.common.theme.utils.ThemeUtils
import com.huanchengfly.tieba.post.utils.AccountUtil
import com.huanchengfly.tieba.post.utils.AppIconUtil
import com.huanchengfly.tieba.post.utils.BlockManager
import com.huanchengfly.tieba.post.utils.ClientUtils
import com.huanchengfly.tieba.post.utils.EmoticonManager
import com.huanchengfly.tieba.post.utils.SharedPreferencesUtil
import com.huanchengfly.tieba.post.utils.appPreferences
import com.huanchengfly.tieba.post.utils.applicationMetaData
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.UpdateTrack
import dagger.hilt.android.HiltAndroidApp
import org.litepal.LitePal
import kotlin.concurrent.thread

@HiltAndroidApp
class App : Application(), SketchFactory {
    private val mActivityList = mutableListOf<Activity>()

    private fun setWebViewPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = progressName ?: return
            if (applicationContext.packageName != processName) { // 判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    override fun onCreate() {
        initAppContext(this)
        super.onCreate()
        ClientUtils.init(this)
        setWebViewPath()
        LitePal.initialize(this)
        AccountUtil.init(this)
        AppConfig.init(this)
        initAppCenter()
        AppIconUtil.setIcon()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        ThemeUtils.init(ThemeDelegate)
        registerActivityLifecycleCallbacks(ClipBoardLinkDetector)
        registerActivityLifecycleCallbacks(OAIDGetter)
        registerActivityMonitor()
        thread {
            BlockManager.init()
            EmoticonManager.init(this@App)
        }
    }

    // 解决魅族 Flyme 系统夜间模式强制反色
    @Keep
    fun mzNightModeUseOf(): Int = 2

    // 禁止app字体大小跟随系统字体大小调节
    override fun getResources(): Resources {
        val fontScale = appPreferences.fontScale
        val resources = super.getResources()
        if (resources.configuration.fontScale != fontScale) {
            val configuration = resources.configuration
            configuration.fontScale = fontScale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        return resources
    }

    /**
     * 添加Activity
     */
    private fun addActivity(activity: Activity) {
        if (activity !in mActivityList) {
            mActivityList += activity
        }
    }

    /**
     * 销毁单个Activity
     */
    private fun removeActivity(activity: Activity) {
        if (activity in mActivityList) {
            mActivityList -= activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    fun removeAllActivity() {
        for (activity in mActivityList) {
            activity.finish()
        }
    }

    private fun initAppCenter() {
        val isSelfBuild = applicationMetaData.getBoolean("is_self_build")
        if (!isSelfBuild) {
            Distribute.setUpdateTrack(if (appPreferences.checkCIUpdate) UpdateTrack.PRIVATE else UpdateTrack.PUBLIC)
            Distribute.setListener(MyDistributeListener())
            AppCenter.start(
                this, "b56debcc-264b-4368-a2cd-8c20213f6433",
                Analytics::class.java, Crashes::class.java, Distribute::class.java
            )
        }
    }

    private fun registerActivityMonitor() {
        registerActivityLifecycleCallbacks(object : DefaultActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                addActivity(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                removeActivity(activity)
            }
        })
    }

    companion object {
        const val TAG = "App"

        @JvmStatic
        var translucentBackground: Drawable? = null

        @JvmStatic
        lateinit var INSTANCE: App
            private set

        val isInitialized: Boolean
            get() = this::INSTANCE.isInitialized

        val isSystemNight: Boolean
            get() = nightMode == Configuration.UI_MODE_NIGHT_YES

        val isFirstRun: Boolean
            get() = SharedPreferencesUtil.get(SharedPreferencesUtil.SP_APP_DATA)
                .getBoolean("first", true)

        private val nightMode: Int
            get() = INSTANCE.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    }

    override fun createSketch(): Sketch = Sketch.Builder(this).apply {
        httpStack(OkHttpStack.Builder().apply {
            userAgent(System.getProperty("http.agent"))
        }.build())
        components {
            addDrawableDecodeInterceptor(PauseLoadWhenScrollingDrawableDecodeInterceptor())
            addDrawableDecoder(
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> GifAnimatedDrawableDecoder.Factory()
                    else -> GifMovieDrawableDecoder.Factory()
                }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                addDrawableDecoder(WebpAnimatedDrawableDecoder.Factory())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                addDrawableDecoder(HeifAnimatedDrawableDecoder.Factory())
            }
        }
    }.build()
}