package com.kadmiv.filmrepo.repo.rest.models.person_movie_model

data class FilmsByPerson(
    val page: Int,
    val results: List<ResultX>,
    val total_pages: Int,
    val total_results: Int
)