@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package cn.chitanda.app.imovie.feature.setting

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.chitanda.app.imovie.core.common.R.color as CommonColor
import cn.chitanda.app.imovie.core.common.R.drawable as CommonDrawable
import cn.chitanda.app.imovie.core.common.R.string as CommonString

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:45
 * @description:
 **/
@Composable
fun SettingScreen(viewModel: SettingScreenViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uiState by viewModel.settingUiState.collectAsStateWithLifecycle()
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = {
            Text("iMovie")
        }, navigationIcon = {
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
        }, scrollBehavior = scrollBehavior
        )
    }) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                AppVersionItem(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    viewModel = viewModel,
                    version = uiState.appVersion,
                    contentPadding = PaddingValues(vertical = 18.dp, horizontal = 24.dp)
                )
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
                    imageVector = icon, contentDescription = "", modifier = Modifier.size(32.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimatedContent(targetState = when (version) {
                    is SettingUiState.AppVersion.NeedUpdate -> stringResource(
                        id = CommonString.setting_have_new_version, version.release.tagName
                    )

                    else -> stringResource(id = CommonString.setting_is_newest)
                }, transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()).using(
                        SizeTransform(clip = false)
                    )
                }) { text ->
                    Text(
                        text = text, style = MaterialTheme.typography.titleLarge
                    )
                }
                Text(
                    text = stringResource(
                        id = CommonString.setting_current_version,
                        version.currentVersion
                    )
                )
            }
        }
    }
}