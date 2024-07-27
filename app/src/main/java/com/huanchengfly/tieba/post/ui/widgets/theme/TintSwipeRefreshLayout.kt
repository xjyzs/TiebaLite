package com.huanchengfly.tieba.post.ui.widgets.theme

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.ui.common.theme.interfaces.Tintable
import com.huanchengfly.tieba.post.ui.common.theme.utils.ThemeUtils

class TintSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs), Tintable {
    init {
        if (!isInEditMode) {
            applyTintColor()
        }
    }

    private fun applyTintColor() {
        setColorSchemeColors(ThemeUtils.getColorByAttr(context, R.attr.colorAccent))
        setProgressBackgroundColorSchemeColor(
            ThemeUtils.getColorByAttr(
                context,
                R.attr.colorIndicator
            )
        )
    }

    override fun tint() {
        applyTintColor()
    }
}
