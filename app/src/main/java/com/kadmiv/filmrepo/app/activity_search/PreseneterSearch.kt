package com.kadmiv.filmrepo.app.activity_search

import android.support.design.widget.NavigationView
import android.util.Log
import android.view.MenuItem
import com.kadmiv.filmrepo.R
import com.kadmiv.filmrepo.base.BasePresenter
import com.kadmiv.filmrepo.base.interfaces.AppBarListener
import com.kadmiv.filmrepo.base.interfaces.ItemListener
import com.kadmiv.filmrepo.base.interfaces.RepoListener
import com.kadmiv.filmrepo.repo.Repo
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.repo.rest.models.more_info.MoreInfo
import com.kadmiv.filmrepo.repo.rest.models.person_movie_model.FilmsByPerson
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle
import com.kadmiv.filmrepo.utils.clearDate
import com.kadmiv.filmrepo.utils.enums.SearchType
import com.kadmiv.filmrepo.utils.enums.SearchType.*
import com.kadmiv.filmrepo.utils.enums.ViewState.*
import java.util.*


const val BASE_IMAGE_PATH = "https://image.tmdb.org/t/p/w500"

class PresenterSearch(var mView: IView?) : BasePresenter(),
    RepoListener,
    ItemListener<FilmModel>,
    NavigationView.OnNavigationItemSelectedListener,
    AppBarListener<FilmModel> {

    var mRepo: Repo? = Repo
    lateinit var searchType: SearchType
    var oldList: ArrayList<FilmModel>? = null
    private var itemsCount = 0
    private var processedItemsCount = 0

    override fun onStart() {
        mRepo!!.addListener(this)
        if (oldList != null) {
            mView?.initRecyclerView(oldList!!)
        }
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        mRepo?.removeListener(this)
        mRepo = null
        mView = null
    }

    override fun onToolbarButtonClicked() {
        mView?.showDrawer()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.nav_saved -> {
                Log.d("12", "nav_saved")
                mView?.showMainActivity()
            }
            R.id.nav_search_title -> {
                Log.d("12", "nav_search_title")
                mView?.showSearchView(SEARCH_BY_TITLE)
            }
            R.id.nav_search_person -> {
                Log.d("12", "nav_search_person")
                mView?.showSearchView(SEARCH_BY_DIRECTOR)
            }
        }

        return true
    }

    override fun onSearchButtonClicked() {
        mView?.showSearchView()
    }

    fun onSearchQueryChange(newText: String) {
        if (newText.isEmpty())
            return

        when (searchType) {
            SEARCH_BY_TITLE -> mRepo!!.getSuggestionsByTitle(newText)
            SEARCH_BY_DIRECTOR -> mRepo!!.getSuggestionsByPerson(newText)
        }
    }

    fun onEnteredSearchQuery(queryText: String) {
        if (oldList != null) {
            oldList?.clear()
        }

        when (searchType) {
            SEARCH_BY_TITLE -> mRepo!!.findFilmByTitle(queryText)
            SEARCH_BY_DIRECTOR -> mRepo!!.findFilmByPerson(queryText)
        }
    }

    override fun onStartLoading() {
        Log.d("12", "onStartLoading ")
        mView?.setViewState(LOADING_STATE.value)
    }

    override fun onResponseError(error: String) {
        Log.d("12", "ResponseError " + error)
        mView?.setViewState(ERROR_STATE.value)
    }

    override fun onConnectionError() {
        Log.d("12", "ConnectionError ")
        mView?.setViewState(NETWORK_ERROR_STATE.value)
    }


    override fun onReceivingFindByTitleSuggestions(items: FilmsByTitle) {
        val suggestionsList = arrayListOf<String>()
        items.results.forEach { result ->
            suggestionsList.add(result.title)
        }

        val array = arrayOfNulls<String>(suggestionsList.size)
        suggestionsList.toArray(array)

        mView?.setSuggestions(array)
    }

    override fun onReceivingFindByTitleResults(items: FilmsByTitle) {

        if (items.total_pages == 0 || items.results.isEmpty()) {
            mView?.setViewState(EMPTY_STATE.value)
            return
        }

        if (oldList == null) {
            oldList = arrayListOf()
        }

        itemsCount = items.results.size
        processedItemsCount = 0
        items.results.forEach { result ->
            mRepo?.getMoreInfo(result.id.toLong())
        }
//
//        oldList?.addAll(calculateByTitleResultsData(items))
//        mView?.initRecyclerView(oldList!!)
//        mView?.setViewState(CONTENT_STATE.value)
    }

    override fun onReceivingFindByPersonSuggestions(items: FilmsByPerson) {
        val suggestionsList = arrayListOf<String>()
        items.results.forEach { result ->
            suggestionsList.add(result.name)
        }

        val array = arrayOfNulls<String>(suggestionsList.size)
        suggestionsList.toArray(array)

        mView?.setSuggestions(array)
    }

    override fun onReceivingFindByPersonResults(items: FilmsByPerson) {
        if (items.total_pages == 0 || items.results.isEmpty()) {
            mView?.setViewState(EMPTY_STATE.value)
            return
        }

        // Get all directors
        items.results.forEach { result ->
            // Get all films for director
            result.known_for.forEach { film ->
                //Get all details for separate film
                mRepo?.getMoreInfo(film.id.toLong())
            }
        }
    }

    fun isNeedLoadNewData(position: Int) {
        if (position * 1.0 / oldList!!.size > 0.7) {
            Log.d("12", "loadNextPage ")
            when (searchType) {
                SEARCH_BY_TITLE -> mRepo!!.getNextPageWithFilmByTitle()
                SEARCH_BY_DIRECTOR -> mRepo!!.getNextPageWithFilmByPerson()
            }

        }
    }

    override fun onReceivingMoreDetailsResults(item: MoreInfo) {
        val newItem = FilmModel().apply {
            id = item.id.toLong()
            title = item.title
            image = BASE_IMAGE_PATH + item.poster_path
//                        director = item.category = item.c
            releaseDate = clearDate(item.release_date)

            // Get categories
            item.genres.forEachIndexed { index, genre ->
                if (index == item.genres.size - 1) {
                    category += genre.name.toLowerCase()
                } else {
                    category += "${genre.name.toLowerCase()}, "
                }
            }
            rating = item.vote_average
            overview = item.overview

            if (category.isEmpty())
                category = "-"
            if (item.poster_path == null)
                image = "-"
        }

        if (oldList == null)
            oldList = arrayListOf()

        oldList?.add(newItem)
        mView?.initRecyclerView(oldList!!)

        processedItemsCount++
        Log.d("12", "processedItemsCount = $processedItemsCount allCount = $itemsCount")
        if (processedItemsCount >= itemsCount) {
            mView?.setViewState(CONTENT_STATE.value)
        }
    }

    override fun onItemClicked(item: FilmModel) {
        mView?.showItemDetails(item)
    }

    companion object {
        private var instance: PresenterSearch? = null
        fun getInstance(mView: IView?): PresenterSearch {
            if (instance == null)
                instance = PresenterSearch(mView!!)
            else
                if (mView != null)
                    instance?.mView = mView
            return instance!!
        }

        fun setInstanceNull() {
            instance = null
        }
    }

}