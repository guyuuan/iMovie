package cn.chitanda.app.imovie.core.model

import kotlinx.serialization.Serializable

/**
 *@author: Chen
 *@createTime: 2022/11/19 14:21
 *@description:
 **/
@Serializable
data class Movie(
    val id: Long,
    val name: String,
    val pic: String,
    val actor: String,
    val director: String,
    val duration: String,
    val description: String,
    val url: String?
)