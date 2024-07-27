package com.huanchengfly.tieba.post.ui.widgets.theme

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.ui.common.theme.interfaces.Tintable
import com.huanchengfly.tieba.post.ui.common.theme.utils.ColorStateListUtils
import com.kiral.himari.ext.android.content.getColorStateListCompat

class TintCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyleAttr), Tintable {
    private var textColorResId: Int = ResourcesCompat.ID_NULL

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.TintCollapsingToolbarLayout,
            defStyleAttr,
            0
        ) {
            textColorResId = getResourceId(
                R.styleable.TintCollapsingToolbarLayout_textColor,
                ResourcesCompat.ID_NULL
            )
        }
        tint()
    }

    override fun tint() {
        if (textColorResId == ResourcesCompat.ID_NULL) return

        val colorStateList = if (isInEditMode) {
            context.getColorStateListCompat(textColorResId)
        } else {
            ColorStateListUtils.createColorStateList(context, textColorResId)
        }
        setCollapsedTitleTextColor(colorStateList)
        setExpandedTitleTextColor(colorStateList)
    }

}