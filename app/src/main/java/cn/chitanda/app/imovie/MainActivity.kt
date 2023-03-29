package cn.chitanda.app.imovie

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import cn.chitanda.app.imovie.core.design.theme.IMovieTheme
import cn.chitanda.app.imovie.feature.home.navigation.homeNavigationRoute
import cn.chitanda.app.imovie.feature.home.navigation.homeScreen
import cn.chitanda.app.imovie.feature.play.navigation.navigateToPlay
import cn.chitanda.app.imovie.feature.play.navigation.playScreen
import cn.chitanda.app.imovie.feature.setting.navigation.aboutScreen
import cn.chitanda.app.imovie.feature.setting.navigation.licenseScreen
import cn.chitanda.app.imovie.media.AppMediaControllerImpl
import cn.chitanda.app.imovie.ui.navigation.AppRouter
import cn.chitanda.app.imovie.ui.navigation.LocalMainViewModel
import cn.chitanda.app.imovie.ui.navigation.LocalNavigateToPlayScreen
import cn.chitanda.app.imovie.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mediaController: AppMediaControllerImpl

    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS)
        }
        super.onCreate(savedInstanceState)
        setContent {
            val focusManager = LocalFocusManager.current
            IMovieTheme(calculateWindowSizeClass(activity = this)) {
                val navController = rememberNavController()
                AppRouter(
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                        }
                    },
                    navController = navController,
                    LocalNavigateToPlayScreen provides { id, bool ->
                        navController.navigateToPlay(id, bool)
                    },
                    LocalMainViewModel provides viewModel,
                    startDestination = homeNavigationRoute
                ) {
                    homeScreen()
                    playScreen()
                    aboutScreen()
                    licenseScreen()
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (checkEnablePip()) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        }
    }

    private fun checkEnablePip(): Boolean {
        return mediaController.controller?.isPlaying == true && packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        ) && viewModel.checkEnablePip()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.onPictureInPictureModeChanged(isInPictureInPictureMode)
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