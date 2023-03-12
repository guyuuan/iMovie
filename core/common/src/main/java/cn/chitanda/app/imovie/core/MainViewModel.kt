package cn.chitanda.app.imovie.core

import androidx.lifecycle.ViewModel
import cn.chitanda.app.imovie.core.ext.safeLaunch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 *@author: Chen
 *@createTime: 2023/3/12 17:08
 *@description:
 **/
class MainViewModel : ViewModel() {
    private val _isInPictureInPictureMode = MutableStateFlow(false)

    val isInPictureInPictureMode: StateFlow<Boolean> get() = _isInPictureInPictureMode

    fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        safeLaunch {
            _isInPictureInPictureMode.emit(isInPictureInPictureMode)
        }
    }
}