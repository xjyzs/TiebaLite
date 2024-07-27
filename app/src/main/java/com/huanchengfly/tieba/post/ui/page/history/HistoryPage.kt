package com.huanchengfly.tieba.post.ui.page.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.arch.emitGlobalEvent
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.ProvideNavigator
import com.huanchengfly.tieba.post.ui.page.history.list.HistoryListPage
import com.huanchengfly.tieba.post.ui.page.history.list.HistoryListUiEvent
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.PagerTabIndicator
import com.huanchengfly.tieba.post.ui.widgets.compose.TabRow
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.utils.HistoryUtil
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "tblite://history")
    ]
)
@Composable
fun HistoryPage(
    navigator: DestinationsNavigator
) {
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    MyScaffold(
        snackbarHostState = snackbarHostState,
        containerColor = Color.Transparent,
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_history),
                        fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            HistoryUtil.deleteAll()
                            emitGlobalEvent(HistoryListUiEvent.DeleteAll)
                            launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.toast_clear_success))
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(id = R.string.title_history_delete),
                            tint = ExtendedTheme.colorScheme.onTopBar
                        )
                    }
                },
                content = {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            PagerTabIndicator(
                                pagerState = pagerState,
                                tabPositions = tabPositions
                            )
                        },
                        divider = {},
                        backgroundColor = Color.Transparent,
                        contentColor = ExtendedTheme.colorScheme.primary,
                        modifier = Modifier
                            .width(100.dp * 2)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Tab(
                            text = {
                                Text(
                                    text = stringResource(id = R.string.title_history_thread),
                                    fontSize = 13.sp
                                )
                            },
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            selectedContentColor = ExtendedTheme.colorScheme.onTopBar,
                            unselectedContentColor = ExtendedTheme.colorScheme.onTopBarSecondary
                        )
                        Tab(
                            text = {
                                Text(
                                    text = stringResource(id = R.string.title_history_forum),
                                    fontSize = 13.sp
                                )
                            },
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            selectedContentColor = ExtendedTheme.colorScheme.onTopBar,
                            unselectedContentColor = ExtendedTheme.colorScheme.onTopBarSecondary
                        )
                    }
                }
            )
        }
    ) {
        ProvideNavigator(navigator = navigator) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { it },
                verticalAlignment = Alignment.Top,
                userScrollEnabled = true,
            ) {
                if (it == 0) {
                    HistoryListPage(type = HistoryUtil.TYPE_THREAD)
                } else {
                    HistoryListPage(type = HistoryUtil.TYPE_FORUM)
                }
            }
        }
    }
}