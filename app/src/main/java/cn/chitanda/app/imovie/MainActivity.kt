package cn.chitanda.app.imovie

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import cn.chitanda.app.imovie.core.design.theme.IMovieTheme
import cn.chitanda.app.imovie.feature.home.navigation.homeNavigationRoute
import cn.chitanda.app.imovie.feature.home.navigation.homeScreen
import cn.chitanda.app.imovie.feature.play.navigation.navigateToPlay
import cn.chitanda.app.imovie.feature.play.navigation.playScreen
import cn.chitanda.app.imovie.media.AppMediaControllerImpl
import cn.chitanda.app.imovie.ui.navigation.AppRouter
import cn.chitanda.app.imovie.ui.navigation.NavigationRegistry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mediaController: AppMediaControllerImpl

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IMovieTheme(calculateWindowSizeClass(activity = this)) {
                val navController = rememberNavController()
                NavigationRegistry(navigationToPlay = { navController.navigateToPlay(it) }) {
                    AppRouter(
                        navController = navController, startDestination = homeNavigationRoute
                    ) {
                        homeScreen()
                        playScreen()
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