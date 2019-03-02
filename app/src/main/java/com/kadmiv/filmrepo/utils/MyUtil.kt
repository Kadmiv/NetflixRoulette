package com.kadmiv.filmrepo.utils

import com.kadmiv.filmrepo.app.activity_search.BASE_IMAGE_PATH
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle
import com.kadmiv.filmrepo.utils.enums.SearchType
import com.kadmiv.filmrepo.utils.enums.SearchType.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun clearDate(release_date: String): String {
    if (release_date.isEmpty())
        return "-"
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    val date = formatter.parse(release_date) as Date
    return (date.year + 1900).toString()
}

fun calculateByTitleResultsData(items: FilmsByTitle): ArrayList<FilmModel> {
    val films = arrayListOf<FilmModel>()
    items.results.forEach { resultItem ->
        run {
            films.add(
                    FilmModel().apply {
                        id = resultItem.id.toLong()
                        title = resultItem.title
                        image = BASE_IMAGE_PATH + resultItem.poster_path
//                        director = resultItem.category = resultItem.c
                        releaseDate = clearDate(resultItem.release_date)
                        rating = resultItem.vote_average
                        overview = resultItem.overview
                    }
            )
        }
    }

    return films
}

fun setSearchType(searchTypeName: String?): SearchType {
    if (searchTypeName != null)
        when (searchTypeName) {
            SEARCH_BY_TITLE.name -> return SEARCH_BY_TITLE
            SEARCH_BY_DIRECTOR.name -> return SEARCH_BY_DIRECTOR
        }
    return EMPTY_TYPE
}