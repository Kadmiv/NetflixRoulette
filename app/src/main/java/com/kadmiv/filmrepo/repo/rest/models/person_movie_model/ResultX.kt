package com.kadmiv.filmrepo.repo.rest.models.person_movie_model

data class ResultX(
    val adult: Boolean,
    val id: Int,
    val known_for: List<KnownFor>,
    val name: String,
    val popularity: Double,
    val profile_path: Any
)