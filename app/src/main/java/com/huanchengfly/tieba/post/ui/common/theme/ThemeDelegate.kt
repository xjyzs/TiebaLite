package com.huanchengfly.tieba.post.ui.common.theme

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import com.huanchengfly.tieba.post.App
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.ui.common.theme.compose.dynamicTonalPalette
import com.huanchengfly.tieba.post.ui.common.theme.interfaces.ThemeSwitcher
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.Util
import com.huanchengfly.tieba.post.utils.appPreferences
import com.kiral.himari.ext.android.content.getColorCompat

object ThemeDelegate : ThemeSwitcher {
    private fun getDefaultColorResId(attrId: Int): Int {
        return when (attrId) {
            R.attr.colorPrimary -> R.color.default_color_primary
            R.attr.colorNewPrimary -> R.color.default_color_primary
            R.attr.colorAccent -> R.color.default_color_accent
            R.attr.colorOnAccent -> R.color.default_color_on_accent
            R.attr.colorToolbar -> R.color.default_color_toolbar
            R.attr.colorToolbarItem -> R.color.default_color_toolbar_item
            R.attr.colorToolbarItemSecondary -> R.color.default_color_toolbar_item_secondary
            R.attr.colorToolbarItemActive -> R.color.default_color_toolbar_item_active
            R.attr.colorToolbarSurface -> R.color.default_color_toolbar_bar
            R.attr.colorOnToolbarSurface -> R.color.default_color_on_toolbar_bar
            R.attr.colorText -> R.color.default_color_text
            R.attr.colorTextSecondary -> R.color.default_color_text_secondary
            R.attr.colorTextOnPrimary -> R.color.default_color_text_on_primary
            R.attr.color_text_disabled -> R.color.default_color_text_disabled
            R.attr.colorBackground -> R.color.default_color_background
            R.attr.colorWindowBackground -> R.color.default_color_window_background
            R.attr.colorChip -> R.color.default_color_chip
            R.attr.colorOnChip -> R.color.default_color_on_chip
            R.attr.colorUnselected -> R.color.default_color_unselected
            R.attr.colorNavBar -> R.color.default_color_nav
            R.attr.colorNavBarSurface -> R.color.default_color_nav_bar_surface
            R.attr.colorOnNavBarSurface -> R.color.default_color_on_nav_bar_surface
            R.attr.colorCard -> R.color.default_color_card
            R.attr.colorFloorCard -> R.color.default_color_floor_card
            R.attr.colorDivider -> R.color.default_color_divider
            R.attr.shadow_color -> R.color.default_color_shadow
            R.attr.colorIndicator -> R.color.default_color_swipe_refresh_view_background
            R.attr.colorPlaceholder -> R.color.default_color_placeholder
            else -> R.color.transparent
        }
    }

    @SuppressLint("DiscouragedApi")
    fun getColorByAttr(context: Context, attrId: Int, theme: String): Int {
        if (!App.isInitialized) return context.getColorCompat(getDefaultColorResId(attrId))
        val resources = context.resources
        when (attrId) {
            R.attr.colorPrimary -> {
                if (ThemeUtil.isDynamicTheme(theme) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val dynamicTonalPalette = dynamicTonalPalette(context)
                    return if (ThemeUtil.isNightMode(theme)) {
                        dynamicTonalPalette.primary80.toArgb()
                    } else {
                        dynamicTonalPalette.primary40.toArgb()
                    }
                } else if (ThemeUtil.THEME_CUSTOM == theme) {
                    val customPrimaryColorStr = context.appPreferences.customPrimaryColor
                    return if (customPrimaryColorStr != null) {
                        Color.parseColor(customPrimaryColorStr)
                    } else {
                        getColorByAttr(context, attrId, ThemeUtil.THEME_DEFAULT)
                    }
                } else if (ThemeUtil.isTranslucentTheme(theme)) {
                    val primaryColorStr = context.appPreferences.translucentPrimaryColor
                    return if (primaryColorStr != null) {
                        Color.parseColor(primaryColorStr)
                    } else {
                        getColorByAttr(context, attrId, ThemeUtil.THEME_DEFAULT)
                    }
                }
                return context.getColorCompat(
                    resources.getIdentifier(
                        "theme_color_primary_$theme",
                        "color",
                        App.INSTANCE.packageName
                    )
                )
            }

            R.attr.colorNewPrimary -> {
                return if (ThemeUtil.isDynamicTheme(theme) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val dynamicTonalPalette = dynamicTonalPalette(context)
                    if (ThemeUtil.isNightMode(theme)) {
                        dynamicTonalPalette.primary80.toArgb()
                    } else {
                        dynamicTonalPalette.primary40.toArgb()
                    }
                } else if (ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(R.color.theme_color_new_primary_night)
                } else if (theme == ThemeUtil.THEME_DEFAULT) {
                    context.getColorCompat(
                        R.color.theme_color_new_primary_light
                    )
                } else {
                    getColorByAttr(context, R.attr.colorPrimary, theme)
                }
            }

            R.attr.colorAccent -> {
                return if (ThemeUtil.isDynamicTheme(theme) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val dynamicTonalPalette = dynamicTonalPalette(context)
                    if (ThemeUtil.isNightMode(theme)) {
                        dynamicTonalPalette.secondary80.toArgb()
                    } else {
                        dynamicTonalPalette.secondary40.toArgb()
                    }
                } else if (ThemeUtil.THEME_CUSTOM == theme || ThemeUtil.isTranslucentTheme(theme)) {
                    getColorByAttr(context, R.attr.colorPrimary, theme)
                } else {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_accent_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                }
            }

            R.attr.colorOnAccent -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_on_accent_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_on_accent_light)
                }
            }

            R.attr.colorToolbar -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_toolbar_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    val isPrimaryColor = context.appPreferences.toolbarPrimaryColor
                    if (isPrimaryColor) {
                        getColorByAttr(context, R.attr.colorPrimary, theme)
                    } else {
                        context.getColorCompat(R.color.white)
                    }
                }
            }

            R.attr.colorText -> {
                return if (ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "color_text_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(
                        if (ThemeUtil.isNightMode(theme)) R.color.color_text_night else R.color.color_text
                    )
                }
            }

            R.attr.color_text_disabled -> {
                return if (ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "color_text_disabled_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(
                        if (ThemeUtil.isNightMode(
                                theme
                            )
                        ) R.color.color_text_disabled_night else R.color.color_text_disabled
                    )
                }
            }

            R.attr.colorTextSecondary -> {
                return if (ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "color_text_secondary_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(
                        if (ThemeUtil.isNightMode(
                                theme
                            )
                        ) R.color.color_text_secondary_night else R.color.color_text_secondary
                    )
                }
            }

            R.attr.colorTextOnPrimary -> {
                return if (ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(R.color.white)
                } else {
                    getColorByAttr(context, R.attr.colorBackground, theme)
                }
            }

            R.attr.colorBackground -> {
                if (ThemeUtil.isTranslucentTheme(theme)) {
                    return context.getColorCompat(R.color.transparent)
                }
                return if (ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_background_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_background_light)
                }
            }

            R.attr.colorWindowBackground -> {
                return if (ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_window_background_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else if (ThemeUtil.isNightMode()) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_background_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_background_light)
                }
            }

            R.attr.colorChip -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_chip_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_chip_light)
                }
            }

            R.attr.colorOnChip -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_on_chip_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_on_chip_light)
                }
            }

            R.attr.colorUnselected -> {
                return context.getColorCompat(
                    if (ThemeUtil.isNightMode(theme)) {
                        resources.getIdentifier(
                            "theme_color_unselected_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    } else {
                        R.color.theme_color_unselected_day
                    }
                )
            }

            R.attr.colorNavBar -> {
                val color = if (ThemeUtil.isTranslucentTheme(theme)) {
                    R.color.transparent
                } else if (ThemeUtil.isNightMode(theme)) {
                    resources.getIdentifier(
                        "theme_color_nav_$theme",
                        "color",
                        App.INSTANCE.packageName
                    )
                } else {
                    R.color.theme_color_nav_light
                }
                return context.getColorCompat(color)
            }

            R.attr.colorFloorCard -> {
                val color =
                    if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                        resources.getIdentifier(
                            "theme_color_floor_card_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    } else {
                        R.color.theme_color_floor_card_light
                    }
                return context.getColorCompat(color)
            }

            R.attr.colorCard -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_card_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_card_light)
                }
            }

            R.attr.colorDivider -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_divider_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_divider_light)
                }
            }

            R.attr.shadow_color -> {
                return if (ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(R.color.transparent)
                } else {
                    context.getColorCompat(
                        if (ThemeUtil.isNightMode(
                                theme
                            )
                        ) R.color.theme_color_shadow_night else R.color.theme_color_shadow_day
                    )
                }
            }

            R.attr.colorToolbarItem -> {
                if (ThemeUtil.isTranslucentTheme(theme)) {
                    return context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_toolbar_item_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                }
                return if (ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(R.color.theme_color_toolbar_item_night)
                } else {
                    context.getColorCompat(
                        if (ThemeUtil.isStatusBarFontDark()) R.color.theme_color_toolbar_item_light else R.color.theme_color_toolbar_item_dark
                    )
                }
            }

            R.attr.colorToolbarItemActive -> {
                if (ThemeUtil.isTranslucentTheme(theme)) {
                    return context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_toolbar_item_active_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else if (ThemeUtil.isNightMode(theme)) {
                    return getColorByAttr(context, R.attr.colorAccent, theme)
                }
                return context.getColorCompat(
                    if (ThemeUtil.isStatusBarFontDark()) R.color.theme_color_toolbar_item_light else R.color.theme_color_toolbar_item_dark
                )
            }

            R.attr.colorToolbarItemSecondary -> {
                return if (
                    ThemeUtil.isNightMode(theme) ||
                    ThemeUtil.isTranslucentTheme(theme)
                ) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_toolbar_item_secondary_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(
                        if (ThemeUtil.isStatusBarFontDark()) R.color.theme_color_toolbar_item_secondary_white else R.color.theme_color_toolbar_item_secondary_light
                    )
                }
            }

            R.attr.colorIndicator -> {
                return if (ThemeUtil.isNightMode(theme) || ThemeUtil.isTranslucentTheme(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_indicator_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_indicator_light)
                }
            }

            R.attr.colorToolbarSurface -> {
                return if (ThemeUtil.isTranslucentTheme(theme) || ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_toolbar_surface_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_toolbar_surface_light)
                }
            }

            R.attr.colorOnToolbarSurface -> {
                return if (ThemeUtil.isTranslucentTheme(theme) || ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_on_toolbar_surface_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_on_toolbar_surface_light)
                }
            }

            R.attr.colorNavBarSurface -> {
                return if (ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_nav_bar_surface_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_nav_bar_surface_light)
                }
            }

            R.attr.colorOnNavBarSurface -> {
                return if (ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(R.color.theme_color_on_nav_bar_surface_dark)
                } else {
                    context.getColorCompat(R.color.theme_color_on_nav_bar_surface_light)
                }
            }

            R.attr.colorPlaceholder -> {
                return if (ThemeUtil.isTranslucentTheme(theme) || ThemeUtil.isNightMode(theme)) {
                    context.getColorCompat(
                        resources.getIdentifier(
                            "theme_color_placeholder_$theme",
                            "color",
                            App.INSTANCE.packageName
                        )
                    )
                } else {
                    context.getColorCompat(R.color.theme_color_placeholder_light)
                }
            }
        }
        return Util.getColorByAttr(context, attrId, R.color.transparent)
    }

    override fun getColorByAttr(context: Context, attrId: Int): Int {
        return when (attrId) {
            R.attr.colorPrimary, R.attr.colorNewPrimary, R.attr.colorAccent -> {
                getColorByAttr(context, attrId, ThemeUtil.getCurrentTheme(checkDynamic = true))
            }

            else -> getColorByAttr(context, attrId, ThemeUtil.getCurrentTheme())
        }
    }

    override fun getColorById(context: Context, colorId: Int): Int {
//            if (!isInitialized) {
//                return context.getColorCompat(colorId)
//            }
        when (colorId) {
            R.color.default_color_primary -> return getColorByAttr(context, R.attr.colorPrimary)
            R.color.default_color_accent -> return getColorByAttr(context, R.attr.colorAccent)
            R.color.default_color_on_accent -> return getColorByAttr(
                context,
                R.attr.colorOnAccent
            )

            R.color.default_color_chip -> return getColorByAttr(
                context,
                R.attr.colorChip
            )

            R.color.default_color_background -> return getColorByAttr(
                context,
                R.attr.colorBackground
            )

            R.color.default_color_window_background -> return getColorByAttr(
                context,
                R.attr.colorWindowBackground
            )

            R.color.default_color_toolbar -> return getColorByAttr(context, R.attr.colorToolbar)
            R.color.default_color_toolbar_item -> return getColorByAttr(
                context,
                R.attr.colorToolbarItem
            )

            R.color.default_color_toolbar_item_active -> return getColorByAttr(
                context,
                R.attr.colorToolbarItemActive
            )

            R.color.default_color_toolbar_item_secondary -> return getColorByAttr(
                context,
                R.attr.colorToolbarItemSecondary
            )

            R.color.default_color_toolbar_bar -> return getColorByAttr(
                context,
                R.attr.colorToolbarSurface
            )

            R.color.default_color_on_toolbar_bar -> return getColorByAttr(
                context,
                R.attr.colorOnToolbarSurface
            )

            R.color.default_color_nav_bar_surface -> return getColorByAttr(
                context,
                R.attr.colorNavBarSurface
            )

            R.color.default_color_on_nav_bar_surface -> return getColorByAttr(
                context,
                R.attr.colorOnNavBarSurface
            )

            R.color.default_color_card -> return getColorByAttr(context, R.attr.colorCard)
            R.color.default_color_floor_card -> return getColorByAttr(
                context,
                R.attr.colorFloorCard
            )

            R.color.default_color_nav -> return getColorByAttr(context, R.attr.colorNavBar)
            R.color.default_color_shadow -> return getColorByAttr(context, R.attr.shadow_color)
            R.color.default_color_unselected -> return getColorByAttr(
                context,
                R.attr.colorUnselected
            )

            R.color.default_color_text -> return getColorByAttr(context, R.attr.colorText)
            R.color.default_color_text_on_primary -> return getColorByAttr(
                context,
                R.attr.colorTextOnPrimary
            )

            R.color.default_color_text_secondary -> return getColorByAttr(
                context,
                R.attr.colorTextSecondary
            )

            R.color.default_color_text_disabled -> return getColorByAttr(
                context,
                R.attr.color_text_disabled
            )

            R.color.default_color_divider -> return getColorByAttr(context, R.attr.colorDivider)
            R.color.default_color_swipe_refresh_view_background -> return getColorByAttr(
                context,
                R.attr.colorIndicator
            )
        }
        return context.getColorCompat(colorId)
    }
}
