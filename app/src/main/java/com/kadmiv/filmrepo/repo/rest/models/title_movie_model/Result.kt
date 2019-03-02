package com.kadmiv.filmrepo.repo.rest.models.title_movie_model

data class Result(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Any>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Double
)