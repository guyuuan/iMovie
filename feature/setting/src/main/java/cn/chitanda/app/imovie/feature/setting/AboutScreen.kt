@file:OptIn(ExperimentalMaterial3Api::class)

package cn.chitanda.app.imovie.feature.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import cn.chitanda.app.imovie.feature.setting.navigation.licenseNavigationRoute
import cn.chitanda.app.imovie.ui.ext.ListItem
import cn.chitanda.app.imovie.ui.navigation.LocalNavController
import cn.chitanda.app.imovie.core.common.R.string as RString

/**
 * @author: Chen
 * @createTime: 2023/3/17 11:30
 * @description:
 **/
@Composable
fun AboutScreen(navController: NavController = LocalNavController.current) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(RString.app_setting_about)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn(contentPadding = it) {
            item {
                ListItem(modifier = Modifier.fillMaxWidth(), title = "开源许可", onClick = {
                    navController.navigate(licenseNavigationRoute)
                })
            }
        }
    }
}

