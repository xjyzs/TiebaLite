package com.huanchengfly.tieba.post.ui.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.unit.Dp

@Composable
@ExperimentalMaterial3Api
fun rememberPullToRefreshState(
    refreshing: Boolean,
    onRefresh: () -> Unit = {},
    positionalThreshold: Dp = PullToRefreshDefaults.PositionalThreshold,
    enabled: () -> Boolean = { true },
): PullToRefreshState {
    val pullToRefreshState = rememberPullToRefreshState(positionalThreshold, enabled)
    val refreshingState by rememberUpdatedState(refreshing)

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            onRefresh()
        }
    }

    LaunchedEffect(refreshingState) {
        if (refreshingState) return@LaunchedEffect
        pullToRefreshState.endRefresh()
    }

    return pullToRefreshState
}

