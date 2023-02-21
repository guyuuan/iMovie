package cn.chitanda.app.imovie.feature.setting

import androidx.lifecycle.ViewModel
import cn.chitanda.app.imovie.core.data.repository.AppVersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:55
 * @description:
 **/
@HiltViewModel
class SettingScreenViewModel @Inject constructor(private val appVersionRepository: AppVersionRepository) :
    ViewModel() {
}