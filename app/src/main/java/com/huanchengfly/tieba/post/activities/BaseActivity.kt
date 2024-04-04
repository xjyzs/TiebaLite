package com.huanchengfly.tieba.post.activities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import butterknife.ButterKnife
import com.gyf.immersionbar.ImmersionBar
import com.huanchengfly.tieba.post.App
import com.huanchengfly.tieba.post.App.Companion.INSTANCE
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.ScreenInfo
import com.huanchengfly.tieba.post.ui.common.theme.interfaces.ExtraRefreshable
import com.huanchengfly.tieba.post.ui.common.theme.utils.ThemeUtils
import com.huanchengfly.tieba.post.ui.widgets.VoicePlayerView
import com.huanchengfly.tieba.post.ui.widgets.theme.TintToolbar
import com.huanchengfly.tieba.post.utils.AppPreferencesUtils
import com.huanchengfly.tieba.post.utils.DialogUtil
import com.huanchengfly.tieba.post.utils.HandleBackUtil
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.calcStatusBarColorInt

abstract class BaseActivity : AppCompatActivity(), ExtraRefreshable {
    private var mTintToolbar: TintToolbar? = null
    private var oldTheme: String = ""

    private var customStatusColor = -1
    private var statusBarTinted = false

    val appPreferences: AppPreferencesUtils by lazy { AppPreferencesUtils.getInstance(this) }

    private val isActivityResumed: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

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

    protected fun showDialog(dialog: Dialog): Boolean {
        if (isActivityResumed) {
            dialog.show()
            return true
        }
        return false
    }

    // TODO: optimize
    fun showDialog(builder: AlertDialog.Builder.() -> Unit): AlertDialog {
        val dialog = DialogUtil.build(this)
            .apply(builder)
            .create()

        if (isActivityResumed) {
            dialog.show()
        }
        return dialog
    }

    override fun onStop() {
        super.onStop()
        VoicePlayerView.Manager.release()
    }

    open val isNeedImmersionBar: Boolean = true
    open val isNeedFixBg: Boolean = true
    open val isNeedSetTheme: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isNeedFixBg) {
            fixBackground()
        }
        getDeviceDensity()
        if (isNeedSetTheme) {
            ThemeUtil.setTheme(this)
        }
        oldTheme = ThemeUtil.getRawTheme()
        if (isNeedImmersionBar) {
            refreshStatusBarColor()
        }
        if (getLayoutId() != -1) {
            setContentView(getLayoutId())
            ButterKnife.bind(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        getDeviceDensity()
    }

    private fun fixBackground() {
        val decor = window.decorView as ViewGroup
        val decorChild = decor.getChildAt(0) as ViewGroup
        decorChild.setBackgroundColor(Color.BLACK)
    }

    fun refreshUiIfNeed() {
        val rawTheme = ThemeUtil.getRawTheme()
        if (oldTheme.contentEquals(rawTheme) &&
            ThemeUtil.THEME_CUSTOM != rawTheme &&
            !ThemeUtil.isTranslucentTheme()
        ) {
            return
        }
        if (recreateIfNeed()) {
            return
        }
        ThemeUtils.refreshUI(this, this)
    }

    override fun onResume() {
        super.onResume()
        if (appPreferences.followSystemNight) {
            if (App.isSystemNight && !ThemeUtil.isNightMode()) {
                ThemeUtil.switchToNightMode(this, false)
            } else if (!App.isSystemNight && ThemeUtil.isNightMode()) {
                ThemeUtil.switchFromNightMode(this, false)
            }
        }
        refreshUiIfNeed()
    }

    fun exitApplication() {
        INSTANCE.removeAllActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (!HandleBackUtil.handleBackPress(this)) {
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mTintToolbar?.tint()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        mTintToolbar?.tint()
        return true
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        if (toolbar is TintToolbar) {
            mTintToolbar = toolbar
        }
    }

    // TODO: optimize
    override fun onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed()
        }
    }

    open fun setTitle(newTitle: String?) {}
    open fun setSubTitle(newTitle: String?) {}

    // TODO: optimize
    private fun getDeviceDensity() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        ScreenInfo.EXACT_SCREEN_HEIGHT = height
        ScreenInfo.EXACT_SCREEN_WIDTH = width
        val density = metrics.density
        ScreenInfo.DENSITY = metrics.density
        ScreenInfo.SCREEN_HEIGHT = (height / density).toInt()
        ScreenInfo.SCREEN_WIDTH = (width / density).toInt()
    }

    protected fun colorAnim(view: ImageView, vararg value: Int): ValueAnimator {
        return ObjectAnimator.ofArgb(ImageViewAnimWrapper(view), "tint", *value).apply {
            duration = 150
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    protected fun colorAnim(view: TextView, vararg value: Int): ValueAnimator {
        return ObjectAnimator.ofArgb(TextViewAnimWrapper(view), "textColor", *value).apply {
            duration = 150
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun setCustomStatusColor(customStatusColor: Int) {
        if (ThemeUtil.isTranslucentTheme()) {
            return
        }
        this.customStatusColor = customStatusColor
        refreshStatusBarColor()
    }

    open fun refreshStatusBarColor() {
        if (ThemeUtil.isTranslucentTheme()) {
            ImmersionBar.with(this)
                .transparentBar()
                .init()
        } else {
            ImmersionBar.with(this).apply {
                if (customStatusColor != -1) {
                    statusBarColorInt(customStatusColor)
                    autoStatusBarDarkModeEnable(true)
                } else {
                    statusBarColorInt(
                        calcStatusBarColorInt(
                            this@BaseActivity,
                            ThemeUtils.getColorByAttr(this@BaseActivity, R.attr.colorToolbar)
                        )
                    )
                    statusBarDarkFont(ThemeUtil.isStatusBarFontDark())
                }
                fitsSystemWindowsInt(
                    true,
                    ThemeUtils.getColorByAttr(this@BaseActivity, R.attr.colorBackground)
                )
                navigationBarColorInt(
                    ThemeUtils.getColorByAttr(
                        this@BaseActivity,
                        R.attr.colorNavBar
                    )
                )
                navigationBarDarkIcon(ThemeUtil.isNavigationBarFontDark())
            }.init()
        }
        if (!statusBarTinted) {
            statusBarTinted = true
        }
    }

    @CallSuper
    override fun refreshGlobal(activity: Activity) {
        if (isNeedImmersionBar) {
            refreshStatusBarColor()
        }
        oldTheme = ThemeUtil.getRawTheme()
    }

    private fun recreateIfNeed(): Boolean {
        if (ThemeUtil.isNightMode() && !ThemeUtil.isNightMode(oldTheme) ||
            !ThemeUtil.isNightMode() && ThemeUtil.isNightMode(oldTheme)
        ) {
            recreate()
            return true
        }
        if (oldTheme.contains(ThemeUtil.THEME_TRANSLUCENT) &&
            !ThemeUtil.isTranslucentTheme() || ThemeUtil.isTranslucentTheme() &&
            !oldTheme.contains(ThemeUtil.THEME_TRANSLUCENT)
        ) {
            recreate()
            return true
        }
        return false
    }

    override fun refreshSpecificView(view: View) {}

    @Keep
    protected class TextViewAnimWrapper(private val mTarget: TextView) {
        @get:ColorInt
        var textColor: Int
            get() = mTarget.currentTextColor
            set(color) {
                mTarget.setTextColor(color)
            }
    }

    @Keep
    protected class ImageViewAnimWrapper(private val mTarget: ImageView) {
        var tint: Int
            get() = if (mTarget.imageTintList != null) mTarget.imageTintList!!.defaultColor else 0x00000000
            set(color) {
                mTarget.imageTintList = ColorStateList.valueOf(color)
            }
    }

    open fun getLayoutId(): Int = -1
}