package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// TODO: 抽屉大改
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissSnackbarHost(hostState: SnackbarHostState) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.StartToEnd) {
                hostState.currentSnackbarData?.dismiss()
                true
            } else {
                false
            }
        }
    )
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.StartToEnd) {
            dismissState.reset()
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {}
    ) {
        SnackbarHost(hostState = hostState)
    }
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("no scaffold here!") }

@Composable
fun MyScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit
) {
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            content = content,
        )
    }
}

// Drawer
// @Composable
// fun NewScaffold(
//     modifier: Modifier = Modifier,
//     snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
//     topBar: @Composable () -> Unit = {},
//     bottomBar: @Composable () -> Unit = {},
//     snackbarHost: @Composable () -> Unit = { SnackbarHost(hostState = snackbarHostState)},
//     floatingActionButton: @Composable () -> Unit = {},
//     floatingActionButtonPosition: FabPosition = FabPosition.End,
//     drawerContent: @Composable (ColumnScope.() -> Unit) = {},
//     drawerGesturesEnabled: Boolean = true,
//     drawerShape: Shape = MaterialTheme.shapes.large,
//     drawerElevation: Dp = DrawerDefaults.ModalDrawerElevation,
//     drawerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
//     drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
//     drawerScrimColor: Color = DrawerDefaults.scrimColor,
//     containerColor: Color = MaterialTheme.colorScheme.background,
//     contentColor: Color = contentColorFor(containerColor),
//     content: @Composable (PaddingValues) -> Unit
// ) {
//     CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
//         ModalNavigationDrawer(
//             drawerContent = {
//                 ModalDrawerSheet(
//                     drawerShape = drawerShape,
//                     drawerContainerColor = drawerBackgroundColor,
//                     drawerContentColor = drawerContentColor,
//                     drawerTonalElevation = drawerElevation,
//                     content = drawerContent
//                 )
//             },
//             gesturesEnabled = drawerGesturesEnabled,
//             scrimColor = drawerScrimColor
//         ) {
//             Scaffold(
//                 modifier = modifier,
//                 topBar = topBar,
//                 bottomBar = bottomBar,
//                 snackbarHost = snackbarHost,
//                 floatingActionButton = floatingActionButton,
//                 floatingActionButtonPosition = floatingActionButtonPosition,
//                 containerColor = containerColor,
//                 contentColor = contentColor,
//                 content = content,
//             )
//         }
//
//     }
// }