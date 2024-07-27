package com.huanchengfly.tieba.post.ui.page.thread

import kotlinx.serialization.Serializable

object ThreadPageFrom {
    const val FROM_FORUM = "forum"

    // 收藏
    const val FROM_STORE = "store_thread"
    const val FROM_PERSONALIZED = "personalized"
    const val FROM_HISTORY = "history"
}

@Serializable
sealed interface ThreadPageExtra

@Serializable
data object ThreadPageNoExtra : ThreadPageExtra

@Serializable
data class ThreadPageFromStoreExtra(
    val maxPid: Long,
    val maxFloor: Int,
) : ThreadPageExtra
