package com.huanchengfly.tieba.post.api.models

import com.google.gson.annotations.SerializedName
import com.huanchengfly.tieba.post.AppConfig

data class OAID(
    @SerializedName("v")
    val encodedOAID: String = AppConfig.encodedOAID,
    @SerializedName("sc")
    val statusCode: Int = AppConfig.statusCode,
    @SerializedName("sup")
    val support: Int = if (AppConfig.isOAIDSupported) 1 else 0,
    val isTrackLimited: Int = if (AppConfig.isTrackLimited) 1 else 0
)
