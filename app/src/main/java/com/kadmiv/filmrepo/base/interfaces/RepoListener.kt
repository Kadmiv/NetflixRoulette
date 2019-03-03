package com.kadmiv.filmrepo.base.interfaces

import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.repo.rest.models.more_info.MoreInfo
import com.kadmiv.filmrepo.repo.rest.models.person_movie_model.FilmsByPerson
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle

interface RepoListener {

    fun onInsertSuccess() {}
    fun onDeletingSuccess() {}
    fun onResponseError(error: String) {}
    fun onConnectionError() {}
    fun onStartLoading() {}

    fun onReceivingFavorits(items: List<FilmModel>) {}

    fun onReceivingFindByTitleResults(items: FilmsByTitle) {}
    fun onReceivingFindByTitleSuggestions(items: FilmsByTitle) {}

    fun onReceivingFindByPersonResults(items: FilmsByPerson) {}
    fun onReceivingFindByPersonSuggestions(items: FilmsByPerson) {}

    fun onReceivingMoreDetailsResults(item: MoreInfo) {}

}