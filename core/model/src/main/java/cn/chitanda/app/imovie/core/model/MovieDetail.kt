package cn.chitanda.app.imovie.core.model

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:48
 *@description:
 **/
data class MovieDetail(
    val id: Long,
    val name: String,
    val pic: String,
    val actor: String,
    val director: String,
    val duration: String,
    val description: String,
    val playSets: List<PlaysSet>,
)

data class PlaysSet(
    val name: String,
    val url: String,
    val mediaId: String,
    val movieId: Long,
    val index: Int,
)

fun Movie.asMovieDetail(): MovieDetail {

    val urls = checkNotNull(url).split("#").filter {
        it.isNotEmpty()
    }
    val playSet = List(urls.size) {
        val splits = urls[it].split("$")
        PlaysSet(
            name = splits[0],
            url = splits[1],
            mediaId = "$id-${splits[0]}",
            movieId = id,
            index = it
        )
    }
    return MovieDetail(
        id = id,
        name = name,
        pic = pic,
        actor = actor,
        director = director,
        description = description,
        duration = duration,
        playSets = playSet
    )
}
