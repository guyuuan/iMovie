@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
)

package cn.chitanda.app.imovie.feature.setting

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import cn.chitanda.app.imovie.core.DownloadState
import cn.chitanda.app.imovie.ui.ext.collectPartAsState
import java.io.File
import cn.chitanda.app.imovie.core.common.R.color as CommonColor
import cn.chitanda.app.imovie.core.common.R.drawable as CommonDrawable
import cn.chitanda.app.imovie.core.common.R.string as CommonString

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:45
 * @description:
 **/
private const val TAG = "SettingScreen"

@Composable
fun SettingScreen(viewModel: SettingScreenViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val appVersion by viewModel.settingUiState.collectPartAsState(part = SettingUiState::appVersion)
    val settings by viewModel.settingUiState.collectPartAsState(part = SettingUiState::settings)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("iMovie")
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = colorResource(id = CommonColor.ic_launcher_background),
                                    shape = CircleShape
                                ),
                            painter = painterResource(id = CommonDrawable.ic_launcher_foreground),
                            tint = colorResource(id = CommonColor.ic_launcher_foreground),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "version_header", contentType = "AppVersionItem") {
                AppVersionItem(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    viewModel = viewModel,
                    version = appVersion,
                    contentPadding = PaddingValues(vertical = 18.dp, horizontal = 24.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(settings * 30, contentType = { it::class }) {
                AppSettingItem(setting = it, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

private operator fun <E> List<E>.times(i: Int): List<E> {
    return List(size * i) {
        this[it % size]
    }
}

@Composable
private fun AppSettingItem(setting: AppSetting, modifier: Modifier = Modifier) {
    when (setting) {
        is AppSetting.Switcher -> {
            Box(modifier) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = setting.title)
                    Switch(checked = setting.state, onCheckedChange = setting.onChange)
                }
            }

        }

        is AppSetting.NavigationItem -> {
            Box(Modifier.clickable { } then modifier) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = setting.title)
                    Icon(Icons.Default.ChevronRight, contentDescription = "")
                }
            }
        }

    }
}

@Composable
private fun AppVersionItem(
    modifier: Modifier = Modifier,
    viewModel: SettingScreenViewModel,
    version: SettingUiState.AppVersion,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Surface(
        modifier = modifier, shape = MaterialTheme.shapes.large, tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = when (version) {
                is SettingUiState.AppVersion.NeedUpdate -> Icons.Default.Update
                is SettingUiState.AppVersion.Downloading -> Icons.Default.Downloading
                else -> Icons.Default.CheckCircle
            }, transitionSpec = {
                (scaleIn() + fadeIn() with scaleOut() + fadeOut()).using(
                    SizeTransform(clip = false)
                )
            }) { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnimatedContent(targetState = when (version) {
                    is SettingUiState.AppVersion.NeedUpdate -> stringResource(
                        id = CommonString.setting_have_new_version, version.release.tagName
                    )

                    is SettingUiState.AppVersion.Downloading -> {
                        when (val downloadState = version.state) {
                            is DownloadState.Downloading -> {
                                stringResource(
                                    id = CommonString.setting_is_downloading,
                                    version.release.assets.first().name
                                )
                            }

                            is DownloadState.Finish -> {
                                stringResource(id = CommonString.setting_downloading_finish)
                            }

                            is DownloadState.Failed -> {
                                stringResource(id = CommonString.setting_downloading_failed) + " ${downloadState.error}"
                            }
                        }
                    }

                    else -> stringResource(id = CommonString.setting_is_newest)
                }, transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()).using(
                        SizeTransform(clip = false)
                    )
                }) { text ->
                    Text(
                        text = text, style = MaterialTheme.typography.titleLarge, maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = stringResource(
                        id = CommonString.setting_current_version, version.currentVersion
                    )
                )
            }


            when (version) {
                is SettingUiState.AppVersion.NeedUpdate -> {
                    TextButton(onClick = {
                        version.release.assets.firstOrNull()
                            ?.let { viewModel.startDownload(it.url) }
                    }) {
                        Text(text = stringResource(id = CommonString.setting_click_to_downloading))
                    }
                }

                is SettingUiState.AppVersion.Downloading -> {
                    when (val downloadState = version.state) {
                        is DownloadState.Downloading -> {
                            Text(text = "${downloadState.progress}%")
                        }

                        is DownloadState.Finish -> {
                            val cxt = LocalContext.current
                            TextButton(onClick = {
                                installApk(context = cxt, downloadState.file)
                            }) {
                                Text(text = stringResource(id = CommonString.setting_click_to_install))
                            }

                        }

                        else -> {
                            IconButton(onClick = {
                                version.release.assets.firstOrNull()
                                    ?.let { viewModel.startDownload(it.url) }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                else -> {}
            }
        }

    }
}

private fun installApk(context: Context, file: String) {
    val apk = File(file)
    if (apk.exists()) {

        val uri =
            FileProvider.getUriForFile(context, "cn.chitanda.app.imovie.file_provider", apk)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}