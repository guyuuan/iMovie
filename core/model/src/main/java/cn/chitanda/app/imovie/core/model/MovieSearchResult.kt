package cn.chitanda.app.imovie.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieSearchResult(val movies: List<Movie>, val pgCount: Int)