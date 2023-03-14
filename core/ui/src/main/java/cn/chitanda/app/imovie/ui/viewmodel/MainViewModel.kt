package cn.chitanda.app.imovie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.chitanda.app.imovie.core.datastore.AppPreferencesDataSource
import cn.chitanda.app.imovie.core.ext.safeLaunch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2023/3/12 17:08
 *@description:
 **/
@HiltViewModel
class MainViewModel @Inject constructor(
    appPreferencesDataSource: AppPreferencesDataSource
) : ViewModel() {
    private val _isInPictureInPictureMode = MutableStateFlow(false)
    private val enablePip = appPreferencesDataSource.enablePip.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )
    val isInPictureInPictureMode: StateFlow<Boolean> get() = _isInPictureInPictureMode

    fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        safeLaunch {
            _isInPictureInPictureMode.emit(isInPictureInPictureMode)
        }
    }

    fun checkEnablePip(): Boolean {
        return enablePip.value
    }
}