@file:OptIn(ExperimentalMaterial3Api::class)

package cn.chitanda.app.imovie.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.chitanda.app.imovie.core.common.R.drawable as CommonDrawable
import cn.chitanda.app.imovie.core.common.R.color as CommonColor

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:45
 * @description:
 **/
@Composable
fun SettingScreen(viewModel: SettingScreenViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
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
            })
        }
    ) {
        Box(modifier = Modifier.padding(it))
    }
}