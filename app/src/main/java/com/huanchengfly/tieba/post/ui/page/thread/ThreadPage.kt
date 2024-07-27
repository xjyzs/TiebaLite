package com.huanchengfly.tieba.post.ui.page.thread

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ChromeReaderMode
import androidx.compose.material.icons.automirrored.rounded.ChromeReaderMode
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.AlignVerticalTop
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Face6
import androidx.compose.material.icons.rounded.FaceRetouchingOff
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.TiebaApi
import com.huanchengfly.tieba.post.api.booleanToString
import com.huanchengfly.tieba.post.api.models.protos.Post
import com.huanchengfly.tieba.post.api.models.protos.SimpleForum
import com.huanchengfly.tieba.post.api.models.protos.SubPostList
import com.huanchengfly.tieba.post.api.models.protos.ThreadInfo
import com.huanchengfly.tieba.post.api.models.protos.User
import com.huanchengfly.tieba.post.api.models.protos.bawuType
import com.huanchengfly.tieba.post.api.models.protos.plainText
import com.huanchengfly.tieba.post.api.retrofit.exception.getErrorMessage
import com.huanchengfly.tieba.post.arch.GlobalEvent
import com.huanchengfly.tieba.post.arch.ImmutableHolder
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.onEvent
import com.huanchengfly.tieba.post.arch.onGlobalEvent
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.arch.wrapImmutable
import com.huanchengfly.tieba.post.ext.toJson
import com.huanchengfly.tieba.post.ext.toastShort
import com.huanchengfly.tieba.post.models.ThreadHistoryInfoBean
import com.huanchengfly.tieba.post.models.database.History
import com.huanchengfly.tieba.post.ui.common.PbContentRender
import com.huanchengfly.tieba.post.ui.common.PbContentText
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.invertChipBackground
import com.huanchengfly.tieba.post.ui.common.theme.compose.invertChipContent
import com.huanchengfly.tieba.post.ui.common.theme.compose.loadMoreIndicator
import com.huanchengfly.tieba.post.ui.common.theme.compose.pullRefreshIndicator
import com.huanchengfly.tieba.post.ui.common.theme.compose.threadBottomBar
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.ProvideNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.CopyTextDialogPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.ForumPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.ReplyPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.SubPostsSheetPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.ThreadPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.UserProfilePageDestination
import com.huanchengfly.tieba.post.ui.utils.rememberPullToRefreshState
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.BlockTip
import com.huanchengfly.tieba.post.ui.widgets.compose.BlockableContent
import com.huanchengfly.tieba.post.ui.widgets.compose.Card
import com.huanchengfly.tieba.post.ui.widgets.compose.Chip
import com.huanchengfly.tieba.post.ui.widgets.compose.ConfirmDialog
import com.huanchengfly.tieba.post.ui.widgets.compose.Container
import com.huanchengfly.tieba.post.ui.widgets.compose.DefaultButton
import com.huanchengfly.tieba.post.ui.widgets.compose.ErrorScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.HorizontalDivider
import com.huanchengfly.tieba.post.ui.widgets.compose.LazyLoad
import com.huanchengfly.tieba.post.ui.widgets.compose.ListMenuItem
import com.huanchengfly.tieba.post.ui.widgets.compose.LoadMoreLayout
import com.huanchengfly.tieba.post.ui.widgets.compose.LongClickMenu
import com.huanchengfly.tieba.post.ui.widgets.compose.MyBackHandler
import com.huanchengfly.tieba.post.ui.widgets.compose.MyLazyColumn
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.OriginThreadCard
import com.huanchengfly.tieba.post.ui.widgets.compose.PromptDialog
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.TextWithMinWidth
import com.huanchengfly.tieba.post.ui.widgets.compose.TipScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.UserHeader
import com.huanchengfly.tieba.post.ui.widgets.compose.VerticalDivider
import com.huanchengfly.tieba.post.ui.widgets.compose.VerticalGrid
import com.huanchengfly.tieba.post.ui.widgets.compose.buildChipInlineContent
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberDialogState
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberMenuState
import com.huanchengfly.tieba.post.ui.widgets.compose.states.StateScreen
import com.huanchengfly.tieba.post.utils.DateTimeUtils.getRelativeTimeString
import com.huanchengfly.tieba.post.utils.HistoryUtil
import com.huanchengfly.tieba.post.utils.StringUtil
import com.huanchengfly.tieba.post.utils.StringUtil.getShortNumString
import com.huanchengfly.tieba.post.utils.TiebaUtil
import com.huanchengfly.tieba.post.utils.Util.getIconColorByLevel
import com.huanchengfly.tieba.post.utils.appPreferences
import com.kiral.himari.ext.android.app.appContext
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.max

private fun getDescText(
    time: Long?,
    floor: Int,
    ipAddress: String?
): String {
    val texts = listOfNotNull(
        time?.let { getRelativeTimeString(appContext, it) },
        if (floor > 1) {
            appContext.getString(R.string.tip_post_floor, floor)
        } else {
            null
        },
        if (ipAddress.isNullOrEmpty()) {
            null
        } else {
            appContext.getString(R.string.text_ip_location, ipAddress)
        }
    )
    if (texts.isEmpty()) {
        return ""
    }
    return texts.joinToString(" · ")
}

@Composable
fun PostAgreeBtn(
    hasAgreed: Boolean,
    agreeNum: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (hasAgreed) {
            ExtendedTheme.colorScheme.accent
        } else {
            ExtendedTheme.colorScheme.textSecondary
        },
        label = "postAgreeBtnColor"
    )
    DefaultButton(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ExtendedTheme.colorScheme.background,
            contentColor = animatedColor
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (hasAgreed) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = stringResource(id = R.string.title_agree),
                tint = animatedColor,
                modifier = Modifier.size(16.dp)
            )
            if (agreeNum > 0) {
                Text(
                    text = agreeNum.getShortNumString(),
                    color = animatedColor,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BottomBarAgreeBtn(
    hasAgreed: Boolean,
    agreeNum: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color =
        if (hasAgreed) ExtendedTheme.colorScheme.accent else ExtendedTheme.colorScheme.textSecondary
    val animatedColor by animateColorAsState(color, label = "agreeBtnColor")

    DefaultButton(
        onClick = onClick,
        shape = RoundedCornerShape(0),
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ExtendedTheme.colorScheme.bottomBar,
            contentColor = animatedColor
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterVertically),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (hasAgreed) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = stringResource(id = R.string.title_agree),
                tint = animatedColor
            )
            if (agreeNum > 0) {
                Text(
                    text = agreeNum.getShortNumString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = animatedColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun BottomBarPlaceholder() {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .background(ExtendedTheme.colorScheme.bottomBar)
            // 拦截点击事件
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(ExtendedTheme.colorScheme.bottomBarSurface)
                .padding(8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.tip_reply_thread),
                style = MaterialTheme.typography.bodySmall,
                color = ExtendedTheme.colorScheme.onBottomBarSurface,
            )
        }

        BottomBarAgreeBtn(
            hasAgreed = false,
            agreeNum = 1,
            onClick = {},
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(id = R.string.btn_more),
                tint = ExtendedTheme.colorScheme.textSecondary,
            )
        }
    }
}

@Composable
private fun ToggleButton(
    text: @Composable (() -> Unit),
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = ExtendedTheme.colorScheme.chip,
    contentColor: Color = ExtendedTheme.colorScheme.text,
    selectedBackgroundColor: Color = ExtendedTheme.colorScheme.invertChipBackground,
    selectedContentColor: Color = ExtendedTheme.colorScheme.invertChipContent,
) {
    val animatedColor by animateColorAsState(
        if (checked) selectedContentColor else contentColor,
        label = "toggleBtnColor"
    )
    val animatedBackgroundColor by animateColorAsState(
        if (checked) selectedBackgroundColor else backgroundColor,
        label = "toggleBtnBackgroundColor"
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = true,
        shape = RoundedCornerShape(6.dp),
        color = animatedBackgroundColor,
        contentColor = animatedColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (icon != null) {
                    icon()
                }
                ProvideTextStyle(
                    value = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                ) {
                    text()
                }
            }
        }
    }
}

@Composable
private fun ThreadLoadMoreIndicator(
    isLoading: Boolean,
    loadMoreEnd: Boolean,
    willLoad: Boolean,
    hasMore: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(100),
        color = ExtendedTheme.colorScheme.loadMoreIndicator,
        contentColor = ExtendedTheme.colorScheme.text,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(10.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp,
                            color = ExtendedTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.text_loading),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    loadMoreEnd -> {
                        Text(
                            text = stringResource(id = R.string.no_more),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    hasMore -> {
                        Text(
                            text = if (willLoad) {
                                stringResource(R.string.release_to_load)
                            } else {
                                stringResource(R.string.pull_to_load)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    else -> {
                        Text(
                            text = if (willLoad) {
                                stringResource(R.string.release_to_load_latest_posts)
                            } else {
                                stringResource(R.string.pull_to_load_latest_posts)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class
)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "tblite://thread/{threadId}"),
    ]
)
@Composable
fun ThreadPage(
    threadId: Long,
    navigator: DestinationsNavigator,
    forumId: Long? = null,
    postId: Long = 0,
    seeLz: Boolean = false,
    sortType: Int = 0,
    from: String = "",
    extra: ThreadPageExtra? = null,
    threadInfo: ThreadInfo? = null,
    scrollToReply: Boolean = false,
    viewModel: ThreadViewModel = pageViewModel(),
) {
    LazyLoad(loaded = viewModel.initialized) {
        viewModel.send(ThreadUiIntent.Init(threadId, forumId, postId, threadInfo, seeLz, sortType))
        viewModel.send(
            ThreadUiIntent.Load(
                threadId,
                page = 0,
                postId = postId,
                forumId = forumId,
                seeLz = seeLz,
                sortType = sortType,
                from = from
            )
        )
        viewModel.initialized = true
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val data by viewModel.uiState.collectPartialAsState(ThreadUiState::data, persistentListOf())
    val author by viewModel.uiState.collectPartialAsState(ThreadUiState::author, null)
    val thread by viewModel.uiState.collectPartialAsState(ThreadUiState::threadInfo, null)
    val firstPost by viewModel.uiState.collectPartialAsState(ThreadUiState::firstPost, null)
    val forum by viewModel.uiState.collectPartialAsState(ThreadUiState::forum, null)
    val user by viewModel.uiState.collectPartialAsState(ThreadUiState::user, wrapImmutable(User()))
    val anti by viewModel.uiState.collectPartialAsState(ThreadUiState::anti, null)
    val firstPostContentRenders by viewModel.uiState.collectPartialAsState(
        ThreadUiState::firstPostContentRenders,
        persistentListOf()
    )
    val isRefreshing by viewModel.uiState.collectPartialAsState(ThreadUiState::isRefreshing, false)
    val isLoadingMore by viewModel.uiState.collectPartialAsState(
        ThreadUiState::isLoadingMore,
        false
    )
    val isError by viewModel.uiState.collectPartialAsState(ThreadUiState::isError, false)
    val error by viewModel.uiState.collectPartialAsState(ThreadUiState::error, null)
    val hasMore by viewModel.uiState.collectPartialAsState(ThreadUiState::hasMore, true)
    val nextPagePostId by viewModel.uiState.collectPartialAsState(ThreadUiState::nextPagePostId, 0L)
    val hasPrevious by viewModel.uiState.collectPartialAsState(ThreadUiState::hasPrevious, true)
    val currentPageMax by viewModel.uiState.collectPartialAsState(ThreadUiState::currentPageMax, 0)
    val totalPage by viewModel.uiState.collectPartialAsState(ThreadUiState::totalPage, 0)
    val isSeeLz by viewModel.uiState.collectPartialAsState(ThreadUiState::seeLz, seeLz)
    val curSortType by viewModel.uiState.collectPartialAsState(ThreadUiState::sortType, sortType)
    val isImmersiveMode by viewModel.uiState.collectPartialAsState(
        ThreadUiState::isImmersiveMode,
        false
    )
    val latestPosts by viewModel.uiState.collectPartialAsState(
        ThreadUiState::latestPosts,
        persistentListOf()
    )

    val isEmpty by remember {
        derivedStateOf { data.isEmpty() && firstPost == null }
    }
    val enablePullRefresh by remember {
        derivedStateOf {
            hasPrevious || curSortType == ThreadSortType.SORT_TYPE_DESC
        }
    }
    val loadMoreEnd by remember {
        derivedStateOf {
            !hasMore && curSortType == ThreadSortType.SORT_TYPE_DESC
        }
    }
    val loadMorePreloadCount by remember {
        derivedStateOf {
            if (hasMore) {
                1
            } else {
                0
            }
        }
    }
    val isCollected = remember(thread) {
        thread?.get { collectStatus != 0 } == true
    }
    val hasThreadAgreed = remember(thread) {
        thread?.get { agree?.hasAgree == 1 } == true
    }
    val threadAgreeNum = remember(thread) {
        thread?.get { agree?.diffAgreeNum } ?: 0L
    }
    val threadTitle = remember(thread) {
        thread?.get { title } ?: ""
    }
    val curForumId = remember(forumId, forum) {
        forumId ?: forum?.get { id }
    }
    val curForumName = remember(forum) { forum?.get { name } }
    val curTbs = remember(anti) { anti?.get { tbs } }
    var waitLoadSuccessAndScrollToFirstReply by remember { mutableStateOf(scrollToReply) }

    val lazyListState = rememberLazyListState()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val lastVisibilityPost by remember {
        derivedStateOf {
            // TODO: 这一坨是什么？
            data.firstOrNull { (post) ->
                val lastPostKey = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull { info ->
                    info.key is String && (info.key as String).startsWith("Post_")
                }?.key as String?
                lastPostKey?.endsWith(post.get { id }.toString()) == true
            }?.post ?: firstPost
        }
    }
    val lastVisibilityPostId by remember {
        derivedStateOf { lastVisibilityPost?.get { id } ?: 0L }
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val openBottomSheet = {
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }
    val closeBottomSheet = {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }

    MyBackHandler(
        enabled = bottomSheetState.isVisible,
        currentScreen = ThreadPageDestination
    ) {
        closeBottomSheet()
    }

    viewModel.onEvent<ThreadUiEvent.ScrollToFirstReply> {
        lazyListState.animateScrollToItem(1)
    }
    viewModel.onEvent<ThreadUiEvent.ScrollToLatestReply> {
        val index = if (curSortType != ThreadSortType.SORT_TYPE_DESC) {
            2 + data.size
        } else {
            1
        }
        lazyListState.animateScrollToItem(index)
    }
    viewModel.onEvent<ThreadUiEvent.LoadSuccess> {
        if (it.page > 1 || waitLoadSuccessAndScrollToFirstReply) {
            waitLoadSuccessAndScrollToFirstReply = false
            lazyListState.animateScrollToItem(1)
        }
    }
    viewModel.onEvent<ThreadUiEvent.AddFavoriteSuccess> {
        snackbarHostState.showSnackbar(
            context.getString(
                R.string.message_add_favorite_success,
                it.floor
            )
        )
    }
    viewModel.onEvent<ThreadUiEvent.RemoveFavoriteSuccess> {
        snackbarHostState.showSnackbar(context.getString(R.string.message_remove_favorite_success))
    }

    onGlobalEvent<GlobalEvent.ReplySuccess>(
        filter = { it.threadId == threadId }
    ) { event ->
        viewModel.send(
            ThreadUiIntent.LoadMyLatestReply(
                threadId = threadId,
                postId = event.newPostId,
                forumId = curForumId,
                isDesc = curSortType == ThreadSortType.SORT_TYPE_DESC,
                curLatestPostFloor = if (curSortType == ThreadSortType.SORT_TYPE_DESC) {
                    data.firstOrNull()?.post?.get { floor } ?: 1
                } else {
                    data.lastOrNull()?.post?.get { floor } ?: 1
                },
                curPostIds = data.map { it.post.get { id } },
            )
        )
    }

    val updateCollectMarkDialogState = rememberDialogState()
    var readFloorBeforeBack by remember {
        mutableIntStateOf(1)
    }
    ConfirmDialog(
        dialogState = updateCollectMarkDialogState,
        onConfirm = {
            navigator.navigateUp()
            if (lastVisibilityPostId != 0L) {
                TiebaApi.getInstance()
                    .addStoreFlow(threadId, lastVisibilityPostId)
                    .catch {
                        context.toastShort(
                            R.string.message_update_collect_mark_failed,
                            it.getErrorMessage()
                        )
                    }
                    .onEach {
                        context.toastShort(R.string.message_update_collect_mark_success)
                    }
                    .launchIn(coroutineScope)
            }
        },
        onCancel = {
            navigator.navigateUp()
        }
    ) {
        Text(text = stringResource(R.string.message_update_collect_mark, readFloorBeforeBack))
    }
    MyBackHandler(
        enabled = isCollected && !bottomSheetState.isVisible,
        currentScreen = ThreadPageDestination
    ) {
        readFloorBeforeBack = lastVisibilityPost?.get { floor } ?: 0
        if (readFloorBeforeBack != 0) {
            updateCollectMarkDialogState.show()
        } else {
            navigator.navigateUp()
        }
    }

    val confirmDeleteDialogState = rememberDialogState()
    var deletePost by remember { mutableStateOf<ImmutableHolder<Post>?>(null) }
    ConfirmDialog(
        dialogState = confirmDeleteDialogState,
        onConfirm = {
            curForumId ?: return@ConfirmDialog
            if (deletePost == null) {
                val isSelfThread = author?.get { id } == user.get { id }
                viewModel.send(
                    ThreadUiIntent.DeleteThread(
                        forumId = curForumId,
                        forumName = curForumName.orEmpty(),
                        threadId = threadId,
                        deleteMyThread = isSelfThread,
                        tbs = curTbs
                    )
                )
            } else {
                val isSelfPost = deletePost!!.get { author_id } == user.get { id }
                viewModel.send(
                    ThreadUiIntent.DeletePost(
                        forumId = curForumId,
                        forumName = curForumName.orEmpty(),
                        threadId = threadId,
                        postId = deletePost!!.get { id },
                        deleteMyPost = isSelfPost,
                        tbs = curTbs
                    )
                )
            }
        }
    ) {
        val textRes = if (deletePost == null) {
            stringResource(R.string.this_thread)
        } else {
            stringResource(R.string.tip_post_floor, deletePost!!.get { floor })
        }

        Text(text = stringResource(R.string.message_confirm_delete, textRes))
    }

    val jumpToPageDialogState = rememberDialogState()
    PromptDialog(
        onConfirm = {
            viewModel.send(
                ThreadUiIntent.Load(
                    threadId = threadId,
                    forumId = forum?.get { id } ?: forumId,
                    page = it.toInt(),
                    seeLz = isSeeLz,
                    sortType = curSortType
                )
            )
        },
        dialogState = jumpToPageDialogState,
        onValueChange = { newVal, _ -> "^[0-9]*$".toRegex().matches(newVal) },
        title = { Text(text = stringResource(R.string.title_jump_page)) },
        content = {
            Text(
                text = stringResource(R.string.tip_jump_page, currentPageMax, totalPage)
            )
        }
    )

    LaunchedEffect(Unit) {
        if (from == ThreadPageFrom.FROM_STORE && extra is ThreadPageFromStoreExtra && extra.maxPid != postId) {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.message_store_thread_update, extra.maxFloor),
                actionLabel = context.getString(R.string.button_load_new),
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.send(
                    ThreadUiIntent.Load(
                        threadId = threadId,
                        page = 0,
                        postId = extra.maxPid,
                        forumId = forumId,
                        seeLz = seeLz,
                        sortType = sortType
                    )
                )
            }
        }
    }

    var savedHistory by remember { mutableStateOf(false) }
    LaunchedEffect(threadId, threadTitle, author, lastVisibilityPostId) {
        if (!savedHistory || lastVisibilityPostId != 0L) {
            if (threadTitle.isNotBlank()) {
                HistoryUtil.saveHistory(
                    History(
                        title = threadTitle,
                        data = threadId.toString(),
                        type = HistoryUtil.TYPE_THREAD,
                        extras = ThreadHistoryInfoBean(
                            isSeeLz = isSeeLz,
                            pid = lastVisibilityPostId.toString(),
                            forumName = forum?.get { name },
                            floor = lastVisibilityPost?.get { floor }?.toString()
                        ).toJson(),
                        avatar = StringUtil.getAvatarUrl(author?.get { portrait }),
                        username = author?.get { nameShow }
                    ),
                    async = true
                )
                savedHistory = true
                Log.i("ThreadPage", "saveHistory $lastVisibilityPostId")
            }
        }
    }

    val pullToRefreshState = rememberPullToRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            viewModel.send(
                ThreadUiIntent.LoadFirstPage(
                    threadId,
                    forumId,
                    isSeeLz,
                    curSortType
                )
            )
        }
    )

    @Composable
    fun PostCard(
        item: ImmutableHolder<Post>,
        contentRenders: ImmutableList<PbContentRender>,
        subPosts: ImmutableList<SubPostItemData>,
        blocked: Boolean,
    ) {
        PostCard(
            postHolder = item,
            contentRenders = contentRenders,
            subPosts = subPosts,
            threadAuthorId = author?.get { id } ?: 0L,
            blocked = blocked,
            canDelete = { it.author_id == user.get { id } },
            immersiveMode = isImmersiveMode,
            isCollected = { it.id == thread?.get { collectMarkPid.toLongOrNull() } },
            onUserClick = { navigator.navigate(UserProfilePageDestination(it.id)) },
            onAgree = {
                val postHasAgreed = item.get { agree?.hasAgree == 1 }
                viewModel.send(ThreadUiIntent.AgreePost(threadId, item.get { id }, !postHasAgreed))
            },
            onReplyClick = {
                navigator.navigate(
                    ReplyPageDestination(
                        forumId = curForumId ?: 0,
                        forumName = forum?.get { name } ?: "",
                        threadId = threadId,
                        postId = it.id,
                        replyUserId = it.author?.id ?: it.author_id,
                        replyUserName = it.author?.nameShow.takeIf { name -> !name.isNullOrEmpty() }
                            ?: it.author?.name,
                        replyUserPortrait = it.author?.portrait,
                    )
                )
            },
            onSubPostReplyClick = { post, subPost ->
                navigator.navigate(
                    ReplyPageDestination(
                        forumId = curForumId ?: 0,
                        forumName = forum?.get { name } ?: "",
                        threadId = threadId,
                        postId = post.id,
                        subPostId = subPost.id,
                        replyUserId = subPost.author?.id ?: subPost.author_id,
                        replyUserName = subPost.author?.nameShow.takeIf { name -> !name.isNullOrEmpty() }
                            ?: subPost.author?.name,
                        replyUserPortrait = subPost.author?.portrait,
                    )
                )
            },
            onOpenSubPosts = { subPostId ->
                if (curForumId != null) {
                    navigator.navigate(
                        SubPostsSheetPageDestination(
                            curForumId,
                            threadId,
                            item.get { id },
                            subPostId,
                            false
                        )
                    )
                }
            },
            onMenuCopyClick = {
                navigator.navigate(CopyTextDialogPageDestination(it))
            },
            onMenuFavoriteClick = { post ->
                val isPostCollected = post.id == thread?.get { collectMarkPid.toLongOrNull() }
                val fid = forum?.get { id } ?: forumId
                val tbs = anti?.get { tbs }
                if (fid != null) {
                    val threadUiEvent = if (isPostCollected) {
                        ThreadUiIntent.RemoveFavorite(threadId, fid, tbs)
                    } else {
                        ThreadUiIntent.AddFavorite(threadId, post.id, post.floor)
                    }
                    viewModel.send(threadUiEvent)
                }
            },
            onMenuDeleteClick = {
                deletePost = it.wrapImmutable()
                confirmDeleteDialogState.show()
            },
        )
    }

    fun LazyListScope.latestPosts(desc: Boolean) {
        if (latestPosts.isNotEmpty()) {
            if (!desc) {
                item("LatestPostsTip") {
                    Container {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VerticalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = stringResource(id = R.string.below_is_latest_post),
                                color = ExtendedTheme.colorScheme.textSecondary,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            VerticalDivider(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            items(
                items = latestPosts,
                key = { (item) -> "LatestPost_${item.get { id }}" }
            ) { (item, blocked, renders, subPosts) ->
                Container {
                    PostCard(item, renders, subPosts, blocked)
                }
            }
            if (desc) {
                item("LatestPostsTip") {
                    Container {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VerticalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = stringResource(id = R.string.above_is_latest_post),
                                color = ExtendedTheme.colorScheme.textSecondary,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            VerticalDivider(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    ProvideNavigator(navigator = navigator) {
        StateScreen(
            modifier = Modifier.fillMaxSize(),
            isEmpty = isEmpty,
            isError = isError,
            isLoading = isRefreshing,
            errorScreen = {
                error?.let {
                    val (e) = it
                    ErrorScreen(error = e)
                }
            },
            onReload = {
                viewModel.send(
                    ThreadUiIntent.Load(
                        threadId = threadId,
                        page = 0,
                        postId = postId,
                        forumId = forumId,
                        seeLz = seeLz,
                        sortType = sortType
                    )
                )
            }
        ) {
            MyScaffold(
                snackbarHostState = snackbarHostState,
                topBar = {
                    TopBar(
                        forum = forum,
                        onBack = { navigator.navigateUp() },
                        onForumClick = {
                            val forumName = forum?.get { name }
                            if (forumName != null) {
                                navigator.navigate(ForumPageDestination(forumName))
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomBar(
                        user = user,
                        onClickReply = {
                            navigator.navigate(
                                ReplyPageDestination(
                                    forumId = curForumId ?: 0,
                                    forumName = forum?.get { name }.orEmpty(),
                                    threadId = threadId,
                                )
                            )
                        },
                        onAgree = {
                            val firstPostId = thread?.get { firstPostId }.takeIf { it != 0L }
                                ?: firstPost?.get { id } ?: 0L
                            if (firstPostId != 0L) {
                                viewModel.send(
                                    ThreadUiIntent.AgreeThread(
                                        threadId,
                                        firstPostId,
                                        !hasThreadAgreed
                                    )
                                )
                            }
                        },
                        onClickMore = {
                            if (bottomSheetState.isVisible) {
                                closeBottomSheet()
                            } else {
                                openBottomSheet()
                            }
                        },
                        hasAgreed = hasThreadAgreed,
                        agreeNum = threadAgreeNum,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            )
                    )
                },
            ) { paddingValues ->
                ModalBottomSheetLayout(
                    sheetState = bottomSheetState,
                    sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    sheetBackgroundColor = ExtendedTheme.colorScheme.windowBackground,
                    sheetContent = {
                        ThreadMenu(
                            isSeeLz = isSeeLz,
                            isCollected = isCollected,
                            isImmersiveMode = isImmersiveMode,
                            isDesc = curSortType == ThreadSortType.SORT_TYPE_DESC,
                            canDelete = { author?.get { id } == user.get { id } },
                            onSeeLzClick = {
                                viewModel.send(
                                    ThreadUiIntent.LoadFirstPage(
                                        threadId,
                                        forumId,
                                        !isSeeLz,
                                        curSortType
                                    )
                                )
                                closeBottomSheet()
                            },
                            onCollectClick = {
                                if (isCollected) {
                                    val fid = forum?.get { id } ?: forumId
                                    val tbs = anti?.get { tbs }
                                    if (fid != null) {
                                        viewModel.send(
                                            ThreadUiIntent.RemoveFavorite(
                                                threadId,
                                                fid,
                                                tbs
                                            )
                                        )
                                    }
                                } else {
                                    val readItem = lastVisibilityPost
                                    if (readItem != null) {
                                        viewModel.send(
                                            ThreadUiIntent.AddFavorite(
                                                threadId,
                                                readItem.get { id },
                                                readItem.get { floor })
                                        )
                                    }
                                }
                                closeBottomSheet()
                            },
                            onImmersiveModeClick = {
                                if (!isImmersiveMode && !isSeeLz) {
                                    viewModel.send(
                                        ThreadUiIntent.LoadFirstPage(
                                            threadId,
                                            forumId,
                                            true,
                                            curSortType
                                        )
                                    )
                                }
                                viewModel.send(ThreadUiIntent.ToggleImmersiveMode(!isImmersiveMode))
                                closeBottomSheet()
                            },
                            onDescClick = {
                                val type = if (curSortType != ThreadSortType.SORT_TYPE_DESC) {
                                    ThreadSortType.SORT_TYPE_DESC
                                } else {
                                    ThreadSortType.SORT_TYPE_DEFAULT
                                }
                                viewModel.send(
                                    ThreadUiIntent.LoadFirstPage(threadId, forumId, isSeeLz, type)
                                )
                                closeBottomSheet()
                            },
                            onJumpPageClick = {
                                closeBottomSheet()
                                jumpToPageDialogState.show()
                            },
                            onShareClick = {
                                TiebaUtil.shareText(
                                    context,
                                    "https://tieba.baidu.com/p/$threadId",
                                    threadTitle
                                )
                            },
                            onCopyLinkClick = {
                                TiebaUtil.copyText("https://tieba.baidu.com/p/$threadId?see_lz=${isSeeLz.booleanToString()}")
                            },
                            onReportClick = {
                                val firstPostId = thread?.get { firstPostId }.takeIf { it != 0L }
                                    ?: firstPost?.get { id } ?: 0L
                                coroutineScope.launch {
                                    TiebaUtil.reportPost(context, navigator, firstPostId.toString())
                                }
                            },
                            onDeleteClick = {
                                deletePost = null
                                confirmDeleteDialogState.show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .defaultMinSize(minHeight = 1.dp)
                        )
                    },
                    scrimColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val modifier = if (enablePullRefresh) {
                        Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)
                    } else {
                        Modifier
                    }

                    Box(
                        modifier = modifier
                    ) {
                        LoadMoreLayout(
                            isLoading = isLoadingMore,
                            onLoadMore = {
                                if (hasMore) {
                                    viewModel.send(
                                        ThreadUiIntent.LoadMore(
                                            threadId = threadId,
                                            page = if (curSortType == ThreadSortType.SORT_TYPE_DESC) {
                                                totalPage - currentPageMax
                                            } else {
                                                currentPageMax + 1
                                            },
                                            forumId = forumId,
                                            postId = nextPagePostId,
                                            seeLz = isSeeLz,
                                            sortType = curSortType,
                                            postIds = data.map { it.post.get { id } }
                                        )
                                    )
                                } else if (data.isNotEmpty() && curSortType != ThreadSortType.SORT_TYPE_DESC) {
                                    viewModel.send(
                                        ThreadUiIntent.LoadLatestPosts(
                                            threadId = threadId,
                                            curLatestPostId = data.last().post.get { id },
                                            forumId = curForumId,
                                            seeLz = isSeeLz,
                                            sortType = curSortType
                                        )
                                    )
                                }
                            },
                            loadEnd = loadMoreEnd,
                            indicator = { isLoading, loadMoreEnd, willLoad ->
                                ThreadLoadMoreIndicator(isLoading, loadMoreEnd, willLoad, hasMore)
                            },
                            lazyListState = lazyListState,
                            isEmpty = data.isEmpty(),
                            preloadCount = loadMorePreloadCount,
                        ) {
                            MyLazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item(key = "FirstPost") {
                                    if (firstPost != null) {
                                        Container {
                                            Column {
                                                PostCard(
                                                    postHolder = firstPost!!,
                                                    contentRenders = firstPostContentRenders,
                                                    canDelete = { it.author_id == user.get { id } },
                                                    immersiveMode = isImmersiveMode,
                                                    isCollected = {
                                                        it.id == thread?.get { collectMarkPid }
                                                            ?.toLongOrNull()
                                                    },
                                                    showSubPosts = false,
                                                    onUserClick = {
                                                        navigator.navigate(
                                                            UserProfilePageDestination(it.id)
                                                        )
                                                    },
                                                    onReplyClick = {
                                                        navigator.navigate(
                                                            ReplyPageDestination(
                                                                forumId = curForumId ?: 0,
                                                                forumName = forum?.get { name }
                                                                    .orEmpty(),
                                                                threadId = threadId,
                                                            )
                                                        )
                                                    },
                                                    onMenuCopyClick = {
                                                        navigator.navigate(
                                                            CopyTextDialogPageDestination(it)
                                                        )
                                                    },
                                                    onMenuFavoriteClick = {
                                                        viewModel.send(
                                                            ThreadUiIntent.AddFavorite(
                                                                threadId,
                                                                it.id,
                                                                it.floor
                                                            )
                                                        )
                                                    },
                                                ) {
                                                    deletePost = null
                                                    confirmDeleteDialogState.show()
                                                }

                                                val originThreadInfo =
                                                    thread?.getNullableImmutable { origin_thread_info }
                                                    .takeIf { thread?.get { is_share_thread } == 1 }

                                                originThreadInfo?.let {
                                                    OriginThreadCard(
                                                        originThreadInfo = it,
                                                        modifier = Modifier
                                                            .padding(horizontal = 16.dp)
                                                            .padding(bottom = 16.dp)
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(ExtendedTheme.colorScheme.floorCard)
                                                            .clickable {
                                                                navigator.navigate(
                                                                    ThreadPageDestination(it.get { tid.toLong() },
                                                                        it.get { fid })
                                                                )
                                                            }
                                                            .padding(16.dp)
                                                    )
                                                }

                                                VerticalDivider(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .padding(bottom = 8.dp),
                                                    thickness = 2.dp
                                                )
                                            }
                                        }
                                    }
                                }
                                stickyHeader(key = "ThreadHeader") {
                                    Container {
                                        Row(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.background)
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(
                                                    R.string.title_thread_header,
                                                    "${thread?.get { replyNum - 1 } ?: 0}"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ExtendedTheme.colorScheme.text,
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.height(IntrinsicSize.Min)
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.text_all),
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .clickable(
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = null,
                                                            enabled = isSeeLz
                                                        ) {
                                                            if (isSeeLz) {
                                                                viewModel.send(
                                                                    ThreadUiIntent.LoadFirstPage(
                                                                        threadId = threadId,
                                                                        forumId = forumId,
                                                                        seeLz = false,
                                                                        sortType = curSortType
                                                                    )
                                                                )
                                                            }
                                                        },
                                                    fontSize = 13.sp,
                                                    fontWeight = if (!isSeeLz) {
                                                        FontWeight.SemiBold
                                                    } else {
                                                        FontWeight.Normal
                                                    },
                                                    color = if (!isSeeLz) {
                                                        ExtendedTheme.colorScheme.text
                                                    } else {
                                                        ExtendedTheme.colorScheme.textSecondary
                                                    },
                                                )
                                                HorizontalDivider()
                                                Text(
                                                    text = stringResource(R.string.title_see_lz),
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .clickable(
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = null,
                                                            enabled = !isSeeLz
                                                        ) {
                                                            if (!isSeeLz) {
                                                                viewModel.send(
                                                                    ThreadUiIntent.LoadFirstPage(
                                                                        threadId = threadId,
                                                                        forumId = forumId,
                                                                        seeLz = true,
                                                                        sortType = curSortType
                                                                    )
                                                                )
                                                            }
                                                        },
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSeeLz) {
                                                        FontWeight.SemiBold
                                                    } else {
                                                        FontWeight.Normal
                                                    },
                                                    color = if (isSeeLz) {
                                                        ExtendedTheme.colorScheme.text
                                                    } else {
                                                        ExtendedTheme.colorScheme.textSecondary
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                                if (curSortType == ThreadSortType.SORT_TYPE_DESC) {
                                    latestPosts(true)
                                }
                                item(key = "LoadPreviousBtn") {
                                    if (hasPrevious) {
                                        Container {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.send(
                                                            ThreadUiIntent.LoadPrevious(
                                                                threadId,
                                                                max(currentPageMax - 1, 1),
                                                                forumId,
                                                                postId = data
                                                                    .first()
                                                                    .post
                                                                    .get { id },
                                                                seeLz = isSeeLz,
                                                                sortType = curSortType,
                                                                postIds = data.map { it.post.get { id } }
                                                            )
                                                        )
                                                    }
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.AlignVerticalTop,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = stringResource(id = R.string.btn_load_previous),
                                                    color = ExtendedTheme.colorScheme.text,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                if (!isRefreshing && data.isEmpty()) {
                                    item(key = "EmptyTip") {
                                        Container {
                                            TipScreen(
                                                title = { Text(text = stringResource(id = R.string.title_empty)) },
                                                image = {
                                                    val composition by rememberLottieComposition(
                                                        LottieCompositionSpec.RawRes(R.raw.lottie_empty_box)
                                                    )
                                                    LottieAnimation(
                                                        composition = composition,
                                                        iterations = LottieConstants.IterateForever,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .aspectRatio(2f)
                                                    )
                                                },
                                                actions = {
                                                    if (canReload) {
                                                        DefaultButton(onClick = { reload() }) {
                                                            Text(text = stringResource(id = R.string.btn_refresh))
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxSize(),
                                                scrollable = false
                                            )
                                        }
                                    }
                                } else {
                                    items(
                                        items = data,
                                        key = { (item) -> "Post_${item.get { id }}" }
                                    ) { (item, blocked, renders, subPosts) ->
                                        Container {
                                            PostCard(item, renders, subPosts, blocked)
                                        }
                                    }
                                }
                                if (curSortType != ThreadSortType.SORT_TYPE_DESC) {
                                    latestPosts(false)
                                }
                            }
                        }

                        PullToRefreshContainer(
                            state = pullToRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter),
                            containerColor = ExtendedTheme.colorScheme.pullRefreshIndicator,
                            contentColor = ExtendedTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    forum: ImmutableHolder<SimpleForum>?,
    onBack: () -> Unit,
    onForumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TitleCentredToolbar(
        title = {
            forum?.let {
                if (forum.get { name }.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 48.dp)
                            .height(IntrinsicSize.Min)
                            .clip(RoundedCornerShape(100))
                            .background(ExtendedTheme.colorScheme.chip)
                            .clickable(onClick = onForumClick)
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(
                            data = forum.get { avatar },
                            contentDescription = it.get { name },
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                        )

                        Text(
                            text = stringResource(id = R.string.title_forum, it.get { name }),
                            fontSize = 14.sp,
                            color = ExtendedTheme.colorScheme.text,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            BackNavigationIcon(onBack)
        },
        modifier = modifier
    )
}

@Composable
private fun BottomBar(
    user: ImmutableHolder<User>,
    onClickReply: () -> Unit,
    onAgree: () -> Unit,
    onClickMore: () -> Unit,
    modifier: Modifier = Modifier,
    hasAgreed: Boolean = false,
    agreeNum: Long = 0,
) {
    Column(
        modifier = Modifier.background(ExtendedTheme.colorScheme.threadBottomBar)
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .then(modifier)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (user.get { is_login } == 1 && !LocalContext.current.appPreferences.hideReply) {
                Avatar(
                    data = StringUtil.getAvatarUrl(user.get { portrait }),
                    size = Sizes.Tiny,
                    contentDescription = user.get { name },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ExtendedTheme.colorScheme.bottomBarSurface)
                        .clickable(onClick = onClickReply)
                        .padding(8.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.tip_reply_thread),
                        style = MaterialTheme.typography.bodySmall,
                        color = ExtendedTheme.colorScheme.onBottomBarSurface,
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )
            }

            BottomBarAgreeBtn(
                hasAgreed = hasAgreed,
                agreeNum = agreeNum,
                onClick = onAgree,
                modifier = Modifier.fillMaxHeight()
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(onClick = onClickMore)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(id = R.string.btn_more),
                    tint = ExtendedTheme.colorScheme.textSecondary,
                )
            }
        }

        Box(
            modifier = Modifier
                .requiredHeightIn(min = if (LocalContext.current.appPreferences.liftUpBottomBar) 16.dp else 0.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }
    }
}

@Composable
fun PostCard(
    postHolder: ImmutableHolder<Post>,
    contentRenders: ImmutableList<PbContentRender>,
    subPosts: ImmutableList<SubPostItemData> = persistentListOf(),
    threadAuthorId: Long = 0L,
    blocked: Boolean = false,
    canDelete: (Post) -> Boolean = { false },
    immersiveMode: Boolean = false,
    isCollected: (Post) -> Boolean = { false },
    showSubPosts: Boolean = true,
    onUserClick: (User) -> Unit = {},
    onAgree: () -> Unit = {},
    onReplyClick: (Post) -> Unit = {},
    onSubPostReplyClick: ((Post, SubPostList) -> Unit)? = null,
    onOpenSubPosts: (subPostId: Long) -> Unit = {},
    onMenuCopyClick: ((String) -> Unit)? = null,
    onMenuFavoriteClick: ((Post) -> Unit)? = null,
    onMenuDeleteClick: ((Post) -> Unit)? = null,
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val post = remember(postHolder) { postHolder.get() }
    val hasPadding = remember(key1 = postHolder, key2 = immersiveMode) {
        postHolder.get { floor > 1 } && !immersiveMode
    }
    val paddingModifier = Modifier.padding(start = if (hasPadding) Sizes.Small + 8.dp else 0.dp)
    val author = postHolder.get { author!! }
    val showTitle = remember(postHolder) {
        post.title.isNotBlank() && post.floor <= 1 && post.is_ntitle != 1
    }
    val hasAgreed = remember(postHolder) {
        post.agree?.hasAgree == 1
    }
    val agreeNum = remember(postHolder) {
        post.agree?.diffAgreeNum ?: 0L
    }
    val menuState = rememberMenuState()
    BlockableContent(
        blocked = blocked,
        blockedTip = {
            BlockTip {
                Text(
                    text = stringResource(id = R.string.tip_blocked_post, postHolder.get { floor }),
                )
            }
        },
        hideBlockedContent = context.appPreferences.hideBlockedContent || immersiveMode,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        LongClickMenu(
            menuState = menuState,
            indication = null,
            onClick = {
                onReplyClick(post)
            }.takeIf { !context.appPreferences.hideReply },
            menuContent = {
                if (!context.appPreferences.hideReply) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.btn_reply))
                        },
                        onClick = {
                            onReplyClick(post)
                            menuState.expanded = false
                        }
                    )
                }
                if (onMenuCopyClick != null) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.menu_copy))
                        },
                        onClick = {
                            onMenuCopyClick(post.content.plainText)
                            menuState.expanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.title_report))
                    },
                    onClick = {
                        coroutineScope.launch {
                            TiebaUtil.reportPost(context, navigator, post.id.toString())
                        }
                        menuState.expanded = false
                    }
                )
                if (onMenuFavoriteClick != null) {
                    DropdownMenuItem(
                        text = {
                            if (isCollected(post)) {
                                Text(text = stringResource(id = R.string.title_collect_on))
                            } else {
                                Text(text = stringResource(id = R.string.title_collect_floor))
                            }
                        },
                        onClick = {
                            onMenuFavoriteClick(post)
                            menuState.expanded = false
                        }
                    )
                }
                if (canDelete(post) && onMenuDeleteClick != null) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.title_delete))
                        },
                        onClick = {
                            onMenuDeleteClick(post)
                            menuState.expanded = false
                        }
                    )
                }
            }
        ) {
            Card(
                header = {
                    if (!immersiveMode) {
                        UserHeader(
                            avatar = {
                                Avatar(
                                    data = StringUtil.getAvatarUrl(author.portrait),
                                    size = Sizes.Small,
                                    contentDescription = null
                                )
                            },
                            name = {
                                UserNameText(
                                    userName = StringUtil.getUsernameAnnotatedString(
                                        LocalContext.current,
                                        author.name,
                                        author.nameShow
                                    ),
                                    userLevel = author.level_id,
                                    isLz = author.id == threadAuthorId,
                                    bawuType = author.bawuType,
                                )
                            },
                            desc = {
                                Text(
                                    text = getDescText(
                                        post.time.toLong(),
                                        post.floor,
                                        author.ip_address
                                    )
                                )
                            },
                            onClick = {
                                onUserClick(author)
                            }
                        ) {
                            if (post.floor > 1) {
                                PostAgreeBtn(
                                    hasAgreed = hasAgreed,
                                    agreeNum = agreeNum,
                                    onClick = onAgree
                                )
                            }
                        }
                    }
                },
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = paddingModifier
                            .fillMaxWidth()
                    ) {
                        if (showTitle) {
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp
                            )
                        }

                        if (isCollected(post)) {
                            Chip(
                                text = stringResource(id = R.string.title_collected_floor),
                                invertColor = true,
                                prefixIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }

                        contentRenders.fastForEach { it.Render() }
                    }

                    if (showSubPosts && post.sub_post_number > 0 && subPosts.isNotEmpty() && !immersiveMode) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(paddingModifier)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ExtendedTheme.colorScheme.floorCard)
                                .padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            subPosts.fastForEach { item ->
                                BlockableContent(
                                    blocked = item.blocked,
                                    blockedTip = {
                                        Text(
                                            text = stringResource(id = R.string.tip_blocked_sub_post),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = ExtendedTheme.colorScheme.textDisabled,
                                                fontSize = 13.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                    },
                                ) {
                                    SubPostItem(
                                        subPostList = item.subPost,
                                        subPostContent = item.subPostContent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp),
                                        onReplyClick = {
                                            onSubPostReplyClick?.invoke(post, it)
                                        },
                                        onOpenSubPosts = onOpenSubPosts,
                                        onMenuCopyClick = {
                                            onMenuCopyClick?.invoke(it.content.plainText)
                                        }
                                    )
                                }
                            }

                            if (post.sub_post_number > subPosts.size) {
                                Text(
                                    text = stringResource(
                                        id = R.string.open_all_sub_posts,
                                        post.sub_post_number
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 13.sp,
                                    color = ExtendedTheme.colorScheme.accent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp)
                                        .clickable {
                                            onOpenSubPosts(0)
                                        }
                                        .padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SubPostItem(
    subPostList: ImmutableHolder<SubPostList>,
    subPostContent: AnnotatedString,
    modifier: Modifier = Modifier,
    onReplyClick: ((SubPostList) -> Unit)?,
    onOpenSubPosts: (Long) -> Unit,
    onMenuCopyClick: ((SubPostList) -> Unit)?,
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val menuState = rememberMenuState()
    LongClickMenu(
        menuState = menuState,
        menuContent = {
            if (!context.appPreferences.hideReply) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.title_reply))
                    },
                    onClick = {
                        onReplyClick?.invoke(subPostList.get())
                        menuState.expanded = false
                    }
                )
            }
            if (onMenuCopyClick != null) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(id = R.string.menu_copy))
                    },
                    onClick = {
                        onMenuCopyClick(subPostList.get())
                        menuState.expanded = false
                    }
                )
            }
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.title_report))
                },
                onClick = {
                    coroutineScope.launch {
                        TiebaUtil.reportPost(context, navigator, subPostList.get { id }.toString())
                    }
                    menuState.expanded = false
                }
            )
        },
        shape = RoundedCornerShape(0),
        onClick = {
            onOpenSubPosts(subPostList.get { id })
        }
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium.copy(color = ExtendedTheme.colorScheme.text)) {
            PbContentText(
                text = subPostContent,
                modifier = modifier,
                fontSize = 13.sp,
                emoticonSize = 0.9f,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                lineSpacing = 0.4.sp,
                inlineContent = mapOf(
                    "Lz" to buildChipInlineContent(
                        stringResource(id = R.string.tip_lz),
                        backgroundColor = ExtendedTheme.colorScheme.textSecondary.copy(alpha = 0.1f),
                        color = ExtendedTheme.colorScheme.textSecondary
                    ),
                )
            )
        }
    }
}

@Composable
fun UserNameText(
    userName: AnnotatedString,
    userLevel: Int,
    modifier: Modifier = Modifier,
    isLz: Boolean = false,
    bawuType: String? = null,
) {
    val text = buildAnnotatedString {
        append(userName)
        append(" ")
        if (userLevel > 0) appendInlineContent("Level", alternateText = "$userLevel")
        if (!bawuType.isNullOrBlank()) {
            append(" ")
            appendInlineContent("Bawu", alternateText = bawuType)
        }
        if (isLz) {
            append(" ")
            appendInlineContent("Lz")
        }
    }
    Text(
        text = text,
        inlineContent = mapOf(
            "Level" to buildChipInlineContent(
                "18",
                color = Color(getIconColorByLevel("$userLevel")),
                backgroundColor = Color(getIconColorByLevel("$userLevel")).copy(alpha = 0.25f)
            ),
            "Bawu" to buildChipInlineContent(
                bawuType ?: "",
                color = ExtendedTheme.colorScheme.primary,
                backgroundColor = ExtendedTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            "Lz" to buildChipInlineContent(stringResource(id = R.string.tip_lz)),
        ),
        modifier = modifier
    )
}

@Composable
private fun ThreadMenu(
    isSeeLz: Boolean,
    isCollected: Boolean,
    isImmersiveMode: Boolean,
    isDesc: Boolean,
    canDelete: () -> Boolean,
    onSeeLzClick: () -> Unit,
    onCollectClick: () -> Unit,
    onImmersiveModeClick: () -> Unit,
    onDescClick: () -> Unit,
    onJumpPageClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(4.dp)
                .fillMaxWidth(0.25f)
                .clip(RoundedCornerShape(100))
                .background(ExtendedTheme.colorScheme.chip)
        )
        VerticalGrid(
            column = 2,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            rowModifier = Modifier.height(IntrinsicSize.Min),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            item {
                ToggleButton(
                    text = {
                        TextWithMinWidth(
                            text = stringResource(id = R.string.title_see_lz),
                            minLength = 4
                        )
                    },
                    checked = isSeeLz,
                    onClick = onSeeLzClick,
                    icon = {
                        Icon(
                            imageVector = if (isSeeLz) {
                                Icons.Rounded.Face6
                            } else {
                                Icons.Rounded.FaceRetouchingOff
                            },
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            item {
                ToggleButton(
                    text = {
                        TextWithMinWidth(
                            text = stringResource(
                                id = if (isCollected) {
                                    R.string.title_collected
                                } else {
                                    R.string.title_uncollected
                                }
                            ),
                            minLength = 4
                        )
                    },
                    checked = isCollected,
                    onClick = onCollectClick,
                    icon = {
                        Icon(
                            imageVector = if (isCollected) {
                                Icons.Rounded.Star
                            } else {
                                Icons.Rounded.StarBorder
                            },
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            item {
                ToggleButton(
                    text = {
                        TextWithMinWidth(
                            text = stringResource(R.string.title_pure_read),
                            minLength = 4
                        )
                    },
                    checked = isImmersiveMode,
                    onClick = onImmersiveModeClick,
                    icon = {
                        Icon(
                            imageVector = if (isImmersiveMode) {
                                Icons.AutoMirrored.Rounded.ChromeReaderMode
                            } else {
                                Icons.AutoMirrored.Outlined.ChromeReaderMode
                            },
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            item {
                ToggleButton(
                    text = {
                        TextWithMinWidth(
                            text = stringResource(
                                id = R.string.title_sort
                            ),
                            minLength = 4
                        )
                    },
                    checked = isDesc,
                    onClick = onDescClick,
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Sort,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Column {
            ListMenuItem(
                icon = Icons.Rounded.RocketLaunch,
                text = stringResource(id = R.string.title_jump_page),
                iconColor = ExtendedTheme.colorScheme.text,
                onClick = onJumpPageClick,
                modifier = Modifier.fillMaxWidth(),
            )
            ListMenuItem(
                icon = Icons.Rounded.Share,
                text = stringResource(id = R.string.title_share),
                iconColor = ExtendedTheme.colorScheme.text,
                onClick = onShareClick,
                modifier = Modifier.fillMaxWidth(),
            )
            ListMenuItem(
                icon = Icons.Rounded.ContentCopy,
                text = stringResource(id = R.string.title_copy_link),
                iconColor = ExtendedTheme.colorScheme.text,
                onClick = onCopyLinkClick,
                modifier = Modifier.fillMaxWidth(),
            )
            ListMenuItem(
                icon = Icons.Rounded.Report,
                text = stringResource(id = R.string.title_report),
                iconColor = ExtendedTheme.colorScheme.text,
                onClick = onReportClick,
                modifier = Modifier.fillMaxWidth(),
            )
            if (canDelete()) {
                ListMenuItem(
                    icon = Icons.Rounded.Delete,
                    text = stringResource(id = R.string.title_delete),
                    iconColor = ExtendedTheme.colorScheme.text,
                    onClick = onDeleteClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
