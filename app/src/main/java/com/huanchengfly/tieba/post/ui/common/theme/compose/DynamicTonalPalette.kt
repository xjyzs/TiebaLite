package com.huanchengfly.tieba.post.ui.common.theme.compose

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.kiral.himari.ext.android.app.appContext

/** Dynamic colors in Material. */
@RequiresApi(31)
internal fun dynamicTonalPalette(context: Context): TonalPalette = TonalPalette(
    // The neutral tonal range from the generated dynamic color palette.
    neutral100 = getDynamicColor(android.R.color.system_neutral1_0),
    neutral99 = getDynamicColor(android.R.color.system_neutral1_10),
    neutral95 = getDynamicColor(android.R.color.system_neutral1_50),
    neutral90 = getDynamicColor(android.R.color.system_neutral1_100),
    neutral80 = getDynamicColor(android.R.color.system_neutral1_200),
    neutral70 = getDynamicColor(android.R.color.system_neutral1_300),
    neutral60 = getDynamicColor(android.R.color.system_neutral1_400),
    neutral50 = getDynamicColor(android.R.color.system_neutral1_500),
    neutral40 = getDynamicColor(android.R.color.system_neutral1_600),
    neutral30 = getDynamicColor(android.R.color.system_neutral1_700),
    neutral20 = getDynamicColor(android.R.color.system_neutral1_800),
    neutral10 = getDynamicColor(android.R.color.system_neutral1_900),
    neutral0 = getDynamicColor(android.R.color.system_neutral1_1000),

    // The neutral variant tonal range, sometimes called "neutral 2",  from the
    // generated dynamic color palette.
    neutralVariant100 = getDynamicColor(android.R.color.system_neutral2_0),
    neutralVariant99 = getDynamicColor(android.R.color.system_neutral2_10),
    neutralVariant95 = getDynamicColor(android.R.color.system_neutral2_50),
    neutralVariant90 = getDynamicColor(android.R.color.system_neutral2_100),
    neutralVariant80 = getDynamicColor(android.R.color.system_neutral2_200),
    neutralVariant70 = getDynamicColor(android.R.color.system_neutral2_300),
    neutralVariant60 = getDynamicColor(android.R.color.system_neutral2_400),
    neutralVariant50 = getDynamicColor(android.R.color.system_neutral2_500),
    neutralVariant40 = getDynamicColor(android.R.color.system_neutral2_600),
    neutralVariant30 = getDynamicColor(android.R.color.system_neutral2_700),
    neutralVariant20 = getDynamicColor(android.R.color.system_neutral2_800),
    neutralVariant10 = getDynamicColor(android.R.color.system_neutral2_900),
    neutralVariant0 = getDynamicColor(android.R.color.system_neutral2_1000),

    // The primary tonal range from the generated dynamic color palette.
    primary100 = getDynamicColor(android.R.color.system_accent1_0),
    primary99 = getDynamicColor(android.R.color.system_accent1_10),
    primary95 = getDynamicColor(android.R.color.system_accent1_50),
    primary90 = getDynamicColor(android.R.color.system_accent1_100),
    primary80 = getDynamicColor(android.R.color.system_accent1_200),
    primary70 = getDynamicColor(android.R.color.system_accent1_300),
    primary60 = getDynamicColor(android.R.color.system_accent1_400),
    primary50 = getDynamicColor(android.R.color.system_accent1_500),
    primary40 = getDynamicColor(android.R.color.system_accent1_600),
    primary30 = getDynamicColor(android.R.color.system_accent1_700),
    primary20 = getDynamicColor(android.R.color.system_accent1_800),
    primary10 = getDynamicColor(android.R.color.system_accent1_900),
    primary0 = getDynamicColor(android.R.color.system_accent1_1000),

    // The secondary tonal range from the generated dynamic color palette.
    secondary100 = getDynamicColor(android.R.color.system_accent2_0),
    secondary99 = getDynamicColor(android.R.color.system_accent2_10),
    secondary95 = getDynamicColor(android.R.color.system_accent2_50),
    secondary90 = getDynamicColor(android.R.color.system_accent2_100),
    secondary80 = getDynamicColor(android.R.color.system_accent2_200),
    secondary70 = getDynamicColor(android.R.color.system_accent2_300),
    secondary60 = getDynamicColor(android.R.color.system_accent2_400),
    secondary50 = getDynamicColor(android.R.color.system_accent2_500),
    secondary40 = getDynamicColor(android.R.color.system_accent2_600),
    secondary30 = getDynamicColor(android.R.color.system_accent2_700),
    secondary20 = getDynamicColor(android.R.color.system_accent2_800),
    secondary10 = getDynamicColor(android.R.color.system_accent2_900),
    secondary0 = getDynamicColor(android.R.color.system_accent2_1000),

    // The tertiary tonal range from the generated dynamic color palette.
    tertiary100 = getDynamicColor(android.R.color.system_accent3_0),
    tertiary99 = getDynamicColor(android.R.color.system_accent3_10),
    tertiary95 = getDynamicColor(android.R.color.system_accent3_50),
    tertiary90 = getDynamicColor(android.R.color.system_accent3_100),
    tertiary80 = getDynamicColor(android.R.color.system_accent3_200),
    tertiary70 = getDynamicColor(android.R.color.system_accent3_300),
    tertiary60 = getDynamicColor(android.R.color.system_accent3_400),
    tertiary50 = getDynamicColor(android.R.color.system_accent3_500),
    tertiary40 = getDynamicColor(android.R.color.system_accent3_600),
    tertiary30 = getDynamicColor(android.R.color.system_accent3_700),
    tertiary20 = getDynamicColor(android.R.color.system_accent3_800),
    tertiary10 = getDynamicColor(android.R.color.system_accent3_900),
    tertiary0 = getDynamicColor(android.R.color.system_accent3_1000),
)

@RequiresApi(23)
private fun getDynamicColor(@ColorRes id: Int) =
    Color(appContext.resources.getColor(id, appContext.theme))
