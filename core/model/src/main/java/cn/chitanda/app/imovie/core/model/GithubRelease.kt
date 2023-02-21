package cn.chitanda.app.imovie.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author: Chen
 * @createTime: 2023/2/21 16:41
 * @description:
 **/
@Serializable
data class GithubRelease(
    @SerialName("tag_name")
    val tagName: String,
    val assets: Assets,
) {
    @Serializable
    data class Assets(
        @SerialName("browser_download_url")
        val url: String,
        val name:String,
        val size:Long
    )
}
