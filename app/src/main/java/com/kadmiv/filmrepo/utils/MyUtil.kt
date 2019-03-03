package com.kadmiv.filmrepo.utils

import com.kadmiv.filmrepo.app.search.BASE_IMAGE_PATH
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.repo.rest.models.more_info.MoreInfo
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle
import com.kadmiv.filmrepo.utils.enums.SearchType
import com.kadmiv.filmrepo.utils.enums.SearchType.*
import java.lang.Exception
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

fun createFilmItem(
    item: MoreInfo,
    favorites: List<FilmModel>
): FilmModel? {
    var model: FilmModel? = null
    try {
        model = FilmModel().apply {
            id = item.id.toLong()
            title = item.title
            releaseDate = clearDate(item.release_date)
            rating = item.vote_average
            overview = item.overview
            isFavorite = checkIsFavorite(id, favorites)

            // Get categories
            item.genres.forEachIndexed { index, genre ->
                if (index == item.genres.size - 1) {
                    category += genre.name.toLowerCase()
                } else {
                    category += "${genre.name.toLowerCase()}, "
                }
            }

            if (category.isEmpty())
                category = "-"

            image = if (item.poster_path == null || item.poster_path.isEmpty())
                "-"
            else
                BASE_IMAGE_PATH + item.poster_path

            if (item.overview.isEmpty())
                overview = "-"
        }
    } catch (ex: Exception) {
        ex.stackTrace
    }
    return model
}

fun checkIsFavorite(id: Long, favorites: List<FilmModel>): Boolean {
    favorites.forEach { item -> if (item.id == id) return true }
    return false
}