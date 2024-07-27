package com.huanchengfly.tieba.post.ui.page.hottopic.list

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.models.protos.topicList.NewTopicList
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.OrangeA700
import com.huanchengfly.tieba.post.ui.common.theme.compose.RedA700
import com.huanchengfly.tieba.post.ui.common.theme.compose.TiebaShapes
import com.huanchengfly.tieba.post.ui.common.theme.compose.White
import com.huanchengfly.tieba.post.ui.common.theme.compose.Yellow
import com.huanchengfly.tieba.post.ui.common.theme.compose.pullRefreshIndicator
import com.huanchengfly.tieba.post.ui.utils.rememberPullToRefreshState
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyLazyColumn
import com.huanchengfly.tieba.post.ui.widgets.compose.NetworkImage
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.utils.StringUtil.getShortNumString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
private fun TopicImage(
    index: Int,
    imageUri: String
) {
    val boxModifier = if (index < 3) {
        Modifier
            .fillMaxWidth()
            .aspectRatio(2.39f)
            .clip(TiebaShapes.medium)
    } else {
        Modifier
            .size(Sizes.Medium)
            .aspectRatio(1f)
            .clip(TiebaShapes.small)
    }
    Box(
        modifier = boxModifier
    ) {
        NetworkImage(
            imageUri = imageUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "${index + 1}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.background,
            fontFamily = FontFamily(
                Typeface.createFromAsset(
                    LocalContext.current.assets,
                    "bebas.ttf"
                )
            ),
            modifier = Modifier
                .background(
                    when (index) {
                        0 -> RedA700
                        1 -> OrangeA700
                        2 -> Yellow
                        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = MaterialTheme.colorScheme.onSurfaceVariant.alpha)
                    }
                )
                .padding(4.dp)
        )
    }
}

@Composable
private fun TopicBody(
    index: Int,
    item: NewTopicList
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.topic_name, style = MaterialTheme.typography.titleMedium)
            when (item.topic_tag) {
                2 -> Text(
                    text = stringResource(id = R.string.topic_tag_hot),
                    fontSize = 10.sp,
                    color = White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(RedA700)
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                )

                1 -> Text(
                    text = stringResource(id = R.string.topic_tag_new),
                    fontSize = 10.sp,
                    color = White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(OrangeA700)
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                )
            }
        }
        Text(
            text = item.topic_desc,
            maxLines = if (index < 3) 3 else 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(id = R.string.hot_num, item.discuss_num.getShortNumString()),
            style = MaterialTheme.typography.bodySmall,
            color = ExtendedTheme.colorScheme.textSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun HotTopicListPage(
    viewModel: HotTopicListViewModel = pageViewModel<HotTopicListUiIntent, HotTopicListViewModel>(
        listOf(HotTopicListUiIntent.Load)
    ),
    navigator: DestinationsNavigator,
) {
    val isRefreshing by viewModel.uiState.collectPartialAsState(
        prop1 = HotTopicListUiState::isRefreshing,
        initial = false
    )
    val topicList by viewModel.uiState.collectPartialAsState(
        prop1 = HotTopicListUiState::topicList,
        initial = emptyList()
    )
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_hot_message_list),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                }
            )
        },
    ) { contentPaddings ->
        val pullToRefreshState = rememberPullToRefreshState(
            refreshing = isRefreshing,
            onRefresh = { viewModel.send(HotTopicListUiIntent.Load) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddings)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            MyLazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                itemsIndexed(
                    items = topicList,
                    key = { _, item -> item.topic_id },
                ) { index, item ->
                    if (index < 3) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TopicImage(index = index, imageUri = item.topic_image)
                            TopicBody(index = index, item = item)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TopicImage(index = index, imageUri = item.topic_image)
                            TopicBody(index = index, item = item)
                        }
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