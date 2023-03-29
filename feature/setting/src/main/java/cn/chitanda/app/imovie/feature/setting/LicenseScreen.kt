@file:OptIn(ExperimentalMaterial3Api::class)

package cn.chitanda.app.imovie.feature.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import cn.chitanda.app.imovie.ui.ext.ListItem
import cn.chitanda.app.imovie.ui.navigation.LocalNavController
import cn.chitanda.app.imovie.core.common.R.string as RString

/**
 * @author: Chen
 * @createTime: 2023/3/29 16:07
 * @description:
 **/
@Composable
fun LicenseScreen(navController: NavController = LocalNavController.current) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = { Text(text = stringResource(RString.open_source_license)) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            })
    }) {
        LazyColumn(contentPadding = it) {
            items(license) { license ->
                ListItem(modifier = Modifier.fillMaxWidth(),
                    title = license.name,
                    subTitle = license.license,
                    onClick = { })
            }
        }
    }
}

private val license = listOf(
    License(
        "Accompanist",
        "https://github.com/google/accompanist/blob/main/LICENSE",
        "Apache License 2.0",
    ),
    License(
        "AndroidX", "https://developer.android.com/jetpack/androidx", "Apache License 2.0"
    ),
    License(
        "AndroidX DataStore",
        "https://developer.android.com/jetpack/androidx/releases/datastore",
        "Apache License 2.0"
    ),
    License(
        "AndroidX Lifecycle",
        "https://developer.android.com/jetpack/androidx/releases/lifecycle",
        "Apache License 2.0"
    ),
    License(
        "AndroidX Compose",
        "https://developer.android.com/jetpack/androidx/releases/compose",
        "Apache License 2.0"
    ),
    License(
        "AndroidX Compose Material",
        "https://developer.android.com/jetpack/androidx/releases/compose-material",
        "Apache License 2.0"
    ),
    License(
        "Coil", "https://github.com/coil-kt/coil/blob/main/LICENSE.txt", "Apache License 2.0"
    ),
    License(
        "Kotlin", "https://github.com/JetBrains/kotlin", "Apache License 2.0"
    ),
    License(
        "kotlinx.serialization",
        "https://github.com/Kotlin/kotlinx.serialization/blob/master/LICENSE.txt",
        "Apache License 2.0"
    ),
    License(
        "Retrofit",
        "https://github.com/square/retrofit/blob/master/LICENSE.txt",
        "Apache License 2.0"
    ),
    License(
        "OkHttp", "https://github.com/square/okhttp/blob/master/LICENSE.txt", "Apache License 2.0"
    ),

    )

data class License(
    val name: String,
    val url: String,
    val license: String,
)