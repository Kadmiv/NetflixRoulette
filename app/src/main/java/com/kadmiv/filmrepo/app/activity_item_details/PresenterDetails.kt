package com.kadmiv.filmrepo.app.activity_item_details

import android.support.v4.view.ViewPager
import android.util.Log
import com.kadmiv.filmrepo.base.BasePresenter
import com.kadmiv.filmrepo.base.interfaces.RepoListener
import com.kadmiv.filmrepo.repo.Repo
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.repo.rest.models.person_movie_model.FilmsByPerson
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle
import com.kadmiv.filmrepo.utils.calculateByTitleResultsData
import com.kadmiv.filmrepo.utils.enums.SearchType
import com.kadmiv.filmrepo.utils.enums.SearchType.*
import com.kadmiv.filmrepo.utils.enums.ViewState.*
import java.util.*

class PresenterDetails(var mView: IView?) : BasePresenter(), RepoListener, ViewPager.OnPageChangeListener {

    var mRepo: Repo? = null
    var oldList: ArrayList<FilmModel>? = null
    private lateinit var favorite: List<FilmModel>
    lateinit var searchType: SearchType

    override fun onStart() {
        mRepo = Repo
        mRepo?.addListener(this)
        mRepo?.getFavorites()
    }

    override fun onStop() {
        mRepo?.removeListener(this)
    }

    override fun onDestroy() {
        mRepo = null
        mView = null
    }

    fun onBackButtonPressed() {
        mView?.closeActivity()
    }

    override fun onReceivingSaved(items: List<FilmModel>) {
        favorite = items
        checkForFavorite(oldList!!)
        mView?.setViewState(CONTENT_STATE.value)
    }

    fun onFavoriteSwitchClicked(currentItem: FilmModel) {
        if (currentItem.isFavorite) {
            mRepo?.deleteItem(currentItem)
            currentItem.isFavorite = false
            Log.d("12", "Item was delete ")
        } else {
            currentItem.isFavorite = true
            mRepo?.addItem(currentItem)
            Log.d("12", "Item was saved ")
        }
        mView?.changeItem(currentItemPosition, currentItem)
    }

    override fun onInsertSuccess() {
        Log.d("12", "addItem is done")
    }

    override fun onDeletingSuccess() {
        Log.d("12", "deleteItem is done")
    }

    override fun onPageScrollStateChanged(position: Int) {}

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

    override fun onPageSelected(position: Int) {
        Log.d("12", "Max ${oldList!!.size} current $position")
        mView?.putBindingModel(position)
        mView?.saveItemPosition(position)
        isNeedLoadNewData(position)
    }

    var currentItemPosition = 0
    private fun isNeedLoadNewData(position: Int) {
        if (searchType == EMPTY_TYPE)
            return
        currentItemPosition = position
        if (position * 1.0 / oldList!!.size > 0.8) {
            Log.d("12", "loadNextPage ")
            mRepo?.getNextPageWithFilmByTitle()
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

    override fun onReceivingFindByTitleResults(items: FilmsByTitle) {

        if (items.total_pages == 0 || items.results.isEmpty()) {
            mView?.setViewState(EMPTY_STATE.value)
            return
        }

        if (oldList == null) {
            oldList = arrayListOf()
        }

        val newItems = calculateByTitleResultsData(items)

        newItems.forEach { item -> item.isFavorite = checkIsFavorite(item.id) }
        oldList?.addAll(newItems)
        mView?.setViewState(CONTENT_STATE.value)
        mView?.addNewItems(newItems)
    }

    private fun checkIsFavorite(id: Long): Boolean {
        favorite.forEach { item -> if (item.id == id) return true }
        return false
    }

    override fun onReceivingFindByPersonResults(items: FilmsByPerson) {
        if (items.total_pages == 0 || items.results.isEmpty()) {
            mView?.setViewState(EMPTY_STATE.value)
            return
        }
        mView?.setViewState(CONTENT_STATE.value)
    }

    private fun checkForFavorite(mValues: ArrayList<FilmModel>) {
        mValues.forEach { item ->
            if (!item.isFavorite)
                item.isFavorite = checkIsFavorite(item.id)
        }
    }

    companion object {
        private var instance: PresenterDetails? = null
        fun getInstance(mView: IView?): PresenterDetails {
            if (instance == null)
                instance = PresenterDetails(mView!!)
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