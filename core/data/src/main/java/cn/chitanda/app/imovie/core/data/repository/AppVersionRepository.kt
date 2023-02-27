package cn.chitanda.app.imovie.core.data.repository

import cn.chitanda.app.imovie.core.DownloadState
import cn.chitanda.app.imovie.core.model.GithubRelease
import cn.chitanda.app.imovie.core.network.AppNetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2023/2/21 16:52
 * @description:
 **/
interface AppVersionRepository {
    suspend fun getAppLatestVersion(): GithubRelease

    fun downloadApk(url: String, savePath: String): Flow<DownloadState>

    suspend fun checkAppNeedUpdate(currentVersion: String): GithubRelease?
}

class AppVersionRepositoryImp @Inject constructor(private val dataSource: AppNetworkDataSource) :
    AppVersionRepository {
    override suspend fun getAppLatestVersion() = dataSource.getAppLatestVersion()

    override fun downloadApk(url: String, savePath: String): Flow<DownloadState> =
        dataSource.download(url, savePath)

    override suspend fun checkAppNeedUpdate(currentVersion: String): GithubRelease? {
        val release = getAppLatestVersion()
        val remoteVersion = release.tagName.replaceFirst("v", "")
        return if (currentVersion.asVersionCode() < remoteVersion.asVersionCode()) {
            release
        } else {
            null
        }
    }

    private fun String.asVersionCode() =
      try {
          replace(".", "").toLong()
      }catch (e:NumberFormatException){
          0L
      }

}