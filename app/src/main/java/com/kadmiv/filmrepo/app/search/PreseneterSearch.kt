package com.kadmiv.filmrepo.app.search

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
import com.kadmiv.filmrepo.utils.createFilmItem
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
    var inLoadingProcess = false
    lateinit var searchType: SearchType
    var oldList: ArrayList<FilmModel>? = arrayListOf()
    private var favorites: List<FilmModel>? = null
    private var itemsCount = 0
    private var processedItemsCount = 0

    override fun onStart() {
        mRepo!!.addListener(this)
        mView?.initRecyclerView(oldList!!)
        mRepo?.getFavorites()
    }

    override fun onReceivingFavorits(items: List<FilmModel>) {
        favorites = items
        mView?.setViewState(CONTENT_STATE.value)
    }

    override fun onStop() {
        mRepo!!.removeListener(this)
    }

    override fun onDestroy() {
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

    fun onSearchQueryChanged(newText: String) {
        if (newText.isEmpty())
            return
        if (mView!!.hasConnection())
            when (searchType) {
                SEARCH_BY_TITLE -> mRepo!!.getSuggestionsByTitle(newText)
                SEARCH_BY_DIRECTOR -> mRepo!!.getSuggestionsByPerson(newText)
            }
    }

    fun onSearchQueryEntered(queryText: String) {
        oldList?.clear()

        if (!mView!!.hasConnection())
            mView?.setViewState(NETWORK_ERROR_STATE.value)
        else
            when (searchType) {
                SEARCH_BY_TITLE -> mRepo!!.findFilmByTitle(queryText)
                SEARCH_BY_DIRECTOR -> mRepo!!.findFilmByPerson(queryText)
            }
    }

    override fun onStartLoading() {
        Log.d("12", "onStartLoading ")
        mView?.setViewState(LOADING_STATE.value)
        inLoadingProcess = true
    }

    override fun onResponseError(error: String) {
        Log.d("12", "ResponseError " + error)
        mView?.setViewState(ERROR_STATE.value)
        inLoadingProcess = false
    }

    override fun onConnectionError() {
        Log.d("12", "ConnectionError ")
        mView?.setViewState(NETWORK_ERROR_STATE.value)
        inLoadingProcess = false
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

        itemsCount = items.results.size
        processedItemsCount = 0
        items.results.forEach { result ->
            mRepo?.getMoreInfo(result.id.toLong())
        }
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
        // Get all persons
        items.results.forEach { result ->
            // Get all films per person
            result.known_for.forEach { film ->
                //Get all details for separate film
                mRepo?.getMoreInfo(film.id.toLong())
            }
        }
    }

    override fun onReceivingMoreDetailsResults(item: MoreInfo) {

        favorites ?: return
        val newItem = createFilmItem(item, favorites!!) ?: return

        oldList?.add(newItem)
        mView?.addNewData(oldList!!)

        processedItemsCount++
        Log.d("12", "processedItemsCount = $processedItemsCount allCount = $itemsCount")
        if (processedItemsCount >= itemsCount) {
            mView?.setViewState(CONTENT_STATE.value)
            inLoadingProcess = false
        }
    }

    fun isNeedLoadNewData(position: Int) {
        if (position >= oldList!!.size - 10 && !inLoadingProcess) {
            Log.d("12", "loadNextPage ")
            when (searchType) {
                SEARCH_BY_TITLE -> mRepo!!.getNextPageWithFilmByTitle()
                SEARCH_BY_DIRECTOR -> mRepo!!.getNextPageWithFilmByPerson()
            }
        }
    }

    override fun onItemClicked(item: FilmModel) {
        mView?.showItemDetails(item)
    }
}