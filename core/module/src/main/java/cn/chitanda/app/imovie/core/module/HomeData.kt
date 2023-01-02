package cn.chitanda.app.imovie.core.module

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:18
 *@description:
 **/
data class HomeData(val movies: List<Movie>, val anime: List<Movie>, val drama: List<Movie>) {
    operator fun get(i: Int): List<Movie> {
        return when (i) {
            0 -> movies
            1 -> anime
            2 -> drama
            else -> throw  ArrayIndexOutOfBoundsException()
        }
    }
}
