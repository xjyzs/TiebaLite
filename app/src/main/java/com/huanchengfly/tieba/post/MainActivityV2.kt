package com.huanchengfly.tieba.post

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.os.postDelayed
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.fetch.newFileUri
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.huanchengfly.tieba.post.api.retrofit.exception.getErrorMessage
import com.huanchengfly.tieba.post.arch.BaseComposeActivity
import com.huanchengfly.tieba.post.arch.GlobalEvent
import com.huanchengfly.tieba.post.arch.emitGlobalEvent
import com.huanchengfly.tieba.post.arch.onGlobalEvent
import com.huanchengfly.tieba.post.components.ClipBoardForumLink
import com.huanchengfly.tieba.post.components.ClipBoardLink
import com.huanchengfly.tieba.post.components.ClipBoardLinkDetector
import com.huanchengfly.tieba.post.components.ClipBoardThreadLink
import com.huanchengfly.tieba.post.ext.toastShort
import com.huanchengfly.tieba.post.services.NotifyJobService
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.NavGraphs
import com.huanchengfly.tieba.post.ui.page.destinations.ForumPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.ThreadPageDestination
import com.huanchengfly.tieba.post.ui.utils.DevicePosture
import com.huanchengfly.tieba.post.ui.utils.isBookPosture
import com.huanchengfly.tieba.post.ui.utils.isSeparating
import com.huanchengfly.tieba.post.ui.widgets.compose.AlertDialog
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.AvatarIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.Dialog
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogNegativeButton
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogPositiveButton
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberDialogState
import com.huanchengfly.tieba.post.utils.AccountUtil
import com.huanchengfly.tieba.post.utils.ClientUtils
import com.huanchengfly.tieba.post.utils.JobServiceUtil
import com.huanchengfly.tieba.post.utils.PermissionUtils
import com.huanchengfly.tieba.post.utils.PickMediasRequest
import com.huanchengfly.tieba.post.utils.QuickPreviewUtil
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.TiebaUtil
import com.huanchengfly.tieba.post.utils.compose.LaunchActivityForResult
import com.huanchengfly.tieba.post.utils.compose.LaunchActivityRequest
import com.huanchengfly.tieba.post.utils.isIgnoringBatteryOptimizations
import com.huanchengfly.tieba.post.utils.newIntentFilter
import com.huanchengfly.tieba.post.utils.registerPickMediasLauncher
import com.huanchengfly.tieba.post.utils.requestIgnoreBatteryOptimizations
import com.huanchengfly.tieba.post.utils.requestPermission
import com.kiral.himari.ext.android.content.registerReceiverCompat
import com.kiral.himari.ext.android.os.handlerOf
import com.microsoft.appcenter.analytics.Analytics
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.currentDestinationFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

val LocalNotificationCountFlow =
    staticCompositionLocalOf<Flow<Int>> { throw IllegalStateException("not allowed here!") }
val LocalDevicePosture =
    staticCompositionLocalOf<State<DevicePosture>> { throw IllegalStateException("not allowed here!") }
val LocalNavController =
    staticCompositionLocalOf<NavHostController> { throw IllegalStateException("not allowed here!") }
val LocalDestination = compositionLocalOf<DestinationSpec<*>?> { null }

@AndroidEntryPoint
class MainActivityV2 : BaseComposeActivity() {
    // TODO: 替换
    private val handler = handlerOf()
    private val newMessageReceiver: BroadcastReceiver = NewMessageReceiver()

    private val notificationCountFlow: MutableSharedFlow<Int> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val pickMediasLauncher =
        registerPickMediasLauncher {
            lifecycleScope.emitGlobalEvent(GlobalEvent.SelectedImages(it.id, it.uris))
        }

    private val launchActivityForResultLauncher = registerForActivityResult(
        LaunchActivityForResult()
    ) {
        lifecycleScope.emitGlobalEvent(GlobalEvent.ActivityResult(it.requesterId, it.resultCode, it.intent))
    }

    private val devicePostureFlow: StateFlow<DevicePosture> by lazy {
        WindowInfoTracker.getOrCreate(this)
            .windowLayoutInfo(this)
            .flowWithLifecycle(lifecycle)
            .map { layoutInfo ->
                val foldingFeature = layoutInfo.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull()
                when {
                    isBookPosture(foldingFeature) -> DevicePosture.BookPosture(foldingFeature.bounds)
                    isSeparating(foldingFeature) -> DevicePosture.Separating(
                        foldingFeature.bounds,
                        foldingFeature.orientation
                    )
                    else -> DevicePosture.NormalPosture
                }
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = DevicePosture.NormalPosture
            )
    }

    private var direction: Direction? = null

    @Volatile
    private var waitingNavCollectorToNavigate = false

    private var myNavController: NavHostController? = null
        set(value) {
            field = value
            if (value != null && waitingNavCollectorToNavigate && direction != null) {
                value.currentDestinationFlow
                    .take(1)
                    .onEach {
                        if (waitingNavCollectorToNavigate && direction != null) {
                            value.navigate(direction!!)
                            waitingNavCollectorToNavigate = false
                            direction = null
                        }
                    }
                    .launchIn(lifecycleScope)
            }
        }

    private fun navigate(direction: Direction) {
        if (myNavController == null) {
            waitingNavCollectorToNavigate = true
            this.direction = direction
        } else {
            myNavController?.navigate(direction)
        }
    }

    private fun checkIntent(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        val uriPath = uri.path ?: return false

        if (uri.scheme == "com.baidu.tieba" && uri.host == "unidispatch") {
            when (uriPath.lowercase()) {
                "/frs" -> {
                    val forumName = uri.getQueryParameter("kw") ?: return true
                    navigate(ForumPageDestination(forumName))
                }

                "/pb" -> {
                    val threadId = uri.getQueryParameter("tid")?.toLongOrNull() ?: return true
                    navigate(ThreadPageDestination(threadId))
                }
            }
            return true
        } else {
            return false
        }
    }

    private fun fetchAccount() {
        if (AccountUtil.isLoggedIn()) {
            AccountUtil.fetchAccountFlow(AccountUtil.getLoginInfo()!!)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    toastShort(e.getErrorMessage())
                    e.printStackTrace()
                }
                .launchIn(lifecycleScope)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && AccountUtil.isLoggedIn()) {
            requestPermission {
                permissions = listOf(PermissionUtils.POST_NOTIFICATIONS)
                description = getString(R.string.desc_permission_post_notifications)
            }
        }
    }

    private fun initAutoSign() {
        runCatching {
            TiebaUtil.initAutoSign()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(0)
        window.setBackgroundDrawable(ColorDrawable(0))
        lifecycleScope.launch {
            ClientUtils.setActiveTimestamp()
        }
        intent?.let { checkIntent(it) }
    }

    override fun onStart() {
        super.onStart()
        runCatching {
            startNotifyJobService()
            scheduleNotifyJob()
        }
        handler.postDelayed(100) {
            requestNotificationPermission()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            if (!checkIntent(it)) {
                myNavController?.handleDeepLink(it)
            }
        }
    }

    override fun onCreateContent() {
        fetchAccount()
        initAutoSign()
    }

    private fun startNotifyJobService() {
        registerReceiverCompat(
            receiver = newMessageReceiver,
            filter = newIntentFilter(NotifyJobService.ACTION_NEW_MESSAGE),
            flags = ContextCompat.RECEIVER_NOT_EXPORTED
        )
        startService(Intent(this, NotifyJobService::class.java))
    }

    private fun scheduleNotifyJob() {
        val jobInfo = JobInfo.Builder(
            JobServiceUtil.getJobId(this),
            ComponentName(this, NotifyJobService::class.java)
        )
            .setPersisted(true)
            .setPeriodic(30 * 60 * 1000L)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
        val jobScheduler = requireNotNull(getSystemService<JobScheduler>())
        jobScheduler.schedule(jobInfo)
    }

    private fun openClipBoardLink(link: ClipBoardLink) {
        when (link) {
            is ClipBoardThreadLink -> {
                myNavController?.navigate(Uri.parse("tblite://thread/${link.threadId}"))
            }

            is ClipBoardForumLink -> {
                myNavController?.navigate(Uri.parse("tblite://forum/${link.forumName}"))
            }

            else -> {
//                launchUrl(this, link.url)
            }
        }
    }

    @Composable
    private fun ClipBoardDetectDialog() {
        val previewInfo by ClipBoardLinkDetector.previewInfoStateFlow.collectAsState()
        val dialogState = rememberDialogState()

        LaunchedEffect(previewInfo) {
            if (previewInfo != null) {
                dialogState.show()
            }
        }

        Dialog(
            dialogState = dialogState,
            title = {
                Text(text = stringResource(id = R.string.title_dialog_clip_board_tieba_url))
            },
            buttons = {
                DialogPositiveButton(text = stringResource(id = R.string.button_open)) {
                    previewInfo?.let {
                        openClipBoardLink(it.clipBoardLink)
                    }
                }
                DialogNegativeButton(text = stringResource(id = R.string.btn_close))
            },
        ) {
            previewInfo?.let {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    border = BorderStroke(1.dp, ExtendedTheme.colorScheme.divider),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        it.icon?.let { icon ->
                            if (icon.type == QuickPreviewUtil.Icon.TYPE_DRAWABLE_RES) {
                                AvatarIcon(
                                    resId = icon.res,
                                    size = Sizes.Medium,
                                    contentDescription = null
                                )
                            } else {
                                Avatar(
                                    data = icon.url,
                                    size = Sizes.Medium,
                                    contentDescription = null
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            it.title?.let { title ->
                                Text(text = title, style = MaterialTheme.typography.titleMedium)
                            }
                            it.subtitle?.let { subtitle ->
                                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialNavigationApi::class)
    @Composable
    override fun Content() {
        val okSignAlertDialogState = rememberDialogState()
        ClipBoardDetectDialog()
        AlertDialog(
            dialogState = okSignAlertDialogState,
            title = { Text(text = stringResource(id = R.string.title_dialog_oksign_battery_optimization)) },
            content = { Text(text = stringResource(id = R.string.message_dialog_oksign_battery_optimization)) },
            buttons = {
                DialogPositiveButton(
                    text = stringResource(id = R.string.button_go_to_ignore_battery_optimization),
                    onClick = {
                        requestIgnoreBatteryOptimizations()
                    }
                )
                DialogNegativeButton(
                    text = stringResource(id = R.string.button_cancel)
                )
                DialogNegativeButton(
                    text = stringResource(id = R.string.button_dont_remind_again),
                    onClick = {
                        appPreferences.ignoreBatteryOptimizationsDialog = true
                    }
                )
            },
        )
        if (appPreferences.autoSign && !isIgnoringBatteryOptimizations() && !appPreferences.ignoreBatteryOptimizationsDialog) {
            LaunchedEffect(Unit) {
                okSignAlertDialogState.show()
            }
        }
        onGlobalEvent<GlobalEvent.StartSelectImages> {
            pickMediasLauncher.launch(PickMediasRequest(it.id, it.maxCount, it.mediaType))
        }
        onGlobalEvent<GlobalEvent.StartActivityForResult> {
            launchActivityForResultLauncher.launch(LaunchActivityRequest(it.requesterId, it.intent))
        }
        TiebaLiteLocalProvider {
            TranslucentThemeBackground {
                val navController = rememberNavController()
                val engine = TiebaNavHostDefaults.rememberNavHostEngine()
                val navigator = rememberBottomSheetNavigator()
                val currentDestination by navController.currentDestinationAsState()

                navController.navigatorProvider += navigator

                LaunchedEffect(currentDestination) {
                    val curDest = currentDestination
                    if (curDest != null) {
                        Analytics.trackEvent(
                            "PageChanged",
                            mapOf("page" to curDest.route)
                        )
                    }
                }

                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalDestination provides currentDestination,
                ) {
                    ModalBottomSheetLayout(
                        bottomSheetNavigator = navigator,
                        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        sheetBackgroundColor = ExtendedTheme.colorScheme.windowBackground,
                        scrimColor = Color.Black.copy(alpha = 0.32f),
                    ) {
                        DestinationsNavHost(
                            navController = navController,
                            navGraph = NavGraphs.root,
                            engine = engine,
                        )
                    }
                }

                SideEffect {
                    myNavController = navController
                }
            }
        }
    }

    @Composable
    private fun TranslucentThemeBackground(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        Surface(
            color = ExtendedTheme.colorScheme.background,
            modifier = modifier,
        ) {
            if (ThemeUtil.isTranslucentTheme(ExtendedTheme.colorScheme.theme)) {
                val backgroundPath by rememberPreferenceAsMutableState(
                    key = stringPreferencesKey("translucent_theme_background_path"),
                    defaultValue = ""
                )
                val backgroundUri by remember { derivedStateOf { newFileUri(backgroundPath) } }
                AsyncImage(
                    imageUri = backgroundUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            content()
        }
    }

    @Composable
    private fun TiebaLiteLocalProvider(
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(
            LocalNotificationCountFlow provides notificationCountFlow,
            LocalDevicePosture provides devicePostureFlow.collectAsState(),
        ) {
            content()
        }
    }

    private inner class NewMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != NotifyJobService.ACTION_NEW_MESSAGE) return
            val channel = intent.getStringExtra(NotifyJobService.EXTRA_CHANNEL) ?: return
            val count = intent.getIntExtra(NotifyJobService.EXTRA_COUNT, 0)
            if (channel == NotifyJobService.CHANNEL_TOTAL) {
                lifecycleScope.launch {
                    notificationCountFlow.emit(count)
                }
            }
        }
    }
}

private object TiebaNavHostDefaults {
    private val AnimationSpec = spring(
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    )

    @Composable
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    fun rememberNavHostEngine() = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopStart,
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = AnimationSpec,
                    initialOffset = { it },
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = AnimationSpec,
                    targetOffset = { -it },
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = AnimationSpec,
                    initialOffset = { -it },
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = AnimationSpec,
                    targetOffset = { it },
                )
            },
        ),
    )

}