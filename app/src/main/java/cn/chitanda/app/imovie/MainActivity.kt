package cn.chitanda.app.imovie

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.chitanda.app.imovie.core.design.theme.IMovieTheme
import cn.chitanda.app.imovie.core.design.windowsize.LocalWindowSizeClass
import cn.chitanda.app.imovie.feature.home.navigation.homeNavigationRoute
import cn.chitanda.app.imovie.feature.home.navigation.homeScreen
import cn.chitanda.app.imovie.feature.play.navigation.navigateToPlay
import cn.chitanda.app.imovie.feature.play.navigation.playScreen
import cn.chitanda.app.imovie.media.AppMediaControllerImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mediaController: AppMediaControllerImpl

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            IMovieTheme(calculateWindowSizeClass(activity = this)) {
                Scaffold {
                    NavHost(
                        navController = navController,
                        startDestination = homeNavigationRoute
//                        startDestination = "/"
                    ) {
                        homeScreen { id ->
                            navController.navigateToPlay(id)
                        }
                        playScreen()
                        composable("/") {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = LocalWindowSizeClass.current.widthSizeClass.toString())
                            }
                        }
                    }
                }

            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onStart() {
        super.onStart()
        mediaController.setActivity(this)
    }

    override fun onStop() {
        mediaController.release()
        super.onStop()
    }
}