package com.kadmiv.filmrepo.repo.rest.models.title_movie_model

data class FilmsByTitle(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)