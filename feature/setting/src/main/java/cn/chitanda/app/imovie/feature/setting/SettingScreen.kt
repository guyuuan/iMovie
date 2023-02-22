@file:OptIn(ExperimentalMaterial3Api::class)

package cn.chitanda.app.imovie.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.chitanda.app.imovie.core.common.R.color as CommonColor
import cn.chitanda.app.imovie.core.common.R.drawable as CommonDrawable

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:45
 * @description:
 **/
@Composable
fun SettingScreen(viewModel: SettingScreenViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uiState by viewModel.settingUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
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
            }, scrollBehavior = scrollBehavior)
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        ,
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)){
                        Text(text = uiState.appVersion.toString())
                        Text(text = uiState.appVersion.currentVersion)
                    }
                }
            }
        }
    }
}