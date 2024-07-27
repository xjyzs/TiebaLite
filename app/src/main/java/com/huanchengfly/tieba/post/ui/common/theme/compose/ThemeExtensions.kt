package com.huanchengfly.tieba.post.ui.common.theme.compose

import androidx.compose.ui.graphics.Color
import com.huanchengfly.tieba.post.utils.ThemeUtil

val ExtendedColorScheme.pullRefreshIndicator: Color
    get() = if (ThemeUtil.isTranslucentTheme(theme)) {
        windowBackground
    } else {
        indicator
    }

val ExtendedColorScheme.loadMoreIndicator: Color
    get() = if (ThemeUtil.isTranslucentTheme(theme)) {
        windowBackground
    } else {
        indicator
    }

val ExtendedColorScheme.threadBottomBar: Color
    get() = if (ThemeUtil.isTranslucentTheme(theme)) {
        windowBackground
    } else {
        bottomBar
    }

val ExtendedColorScheme.menuBackground: Color
    get() = if (ThemeUtil.isTranslucentTheme(theme)) {
        windowBackground
    } else {
        card
    }

val ExtendedColorScheme.invertChipBackground: Color
    get() = if (ThemeUtil.isNightMode(theme)) primary.copy(alpha = 0.3f) else primary

val ExtendedColorScheme.invertChipContent: Color
    get() = if (ThemeUtil.isNightMode(theme)) primary else onPrimary