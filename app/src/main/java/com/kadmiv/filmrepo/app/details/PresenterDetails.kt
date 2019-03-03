package com.kadmiv.filmrepo.app.details

import android.support.v4.view.ViewPager
import android.util.Log
import com.kadmiv.filmrepo.base.BasePresenter
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

class PresenterDetails(var mView: IView?) : BasePresenter(), RepoListener, ViewPager.OnPageChangeListener {

    var mRepo: Repo? = Repo
    var oldList: ArrayList<FilmModel> = arrayListOf()
    var inLoadingProcess = false
    private var favorites: List<FilmModel>? = null
    lateinit var searchType: SearchType
    var currentItemPosition = 0
    private var rewritingItemsCount = 0
    private var processedItemsCount = 0

    override fun onStart() {
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
        mView?.closeActivity(oldList, currentItemPosition)
    }

    override fun onReceivingFavorits(items: List<FilmModel>) {
        favorites = items
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
        Log.d("12", "Max ${oldList.size} current $position")
        mView?.putBindingModel(oldList[position])
        isNeedLoadNewData(position)
        currentItemPosition = position
    }

    private fun isNeedLoadNewData(position: Int) {
        if (searchType == EMPTY_TYPE)
            return

        if (position >= oldList.size - 10 && !inLoadingProcess) {
            if (!mView!!.hasConnection())
                mView?.setViewState(NETWORK_ERROR_STATE.value)
            else {
                Log.d("12", "loadNextPage ")
                mRepo?.getNextPageWithFilmByTitle()
            }
        }
    }

    override fun onStartLoading() {
        Log.d("12", "onStartLoading ")
        mView?.setViewState(LOADING_STATE.value)
        inLoadingProcess = true
    }

    override fun onResponseError(error: String) {
        Log.d("12", "ResponseError $error")
        mView?.setViewState(ERROR_STATE.value)
        inLoadingProcess = false
    }

    override fun onConnectionError() {
        Log.d("12", "ConnectionError ")
        mView?.setViewState(NETWORK_ERROR_STATE.value)
        inLoadingProcess = false
    }

    override fun onReceivingFindByPersonResults(items: FilmsByPerson) {
    }

    override fun onReceivingFindByTitleResults(items: FilmsByTitle) {

        if (items.total_pages == 0 || items.results.isEmpty()) {
            mView?.setViewState(EMPTY_STATE.value)
            return
        }

        rewritingItemsCount = items.results.size
        processedItemsCount = 0
        items.results.forEach { result ->
            mRepo?.getMoreInfo(result.id.toLong())
        }
    }

    override fun onReceivingMoreDetailsResults(item: MoreInfo) {

        favorites ?: return
        val newItem = createFilmItem(item, favorites!!) ?: return

        oldList.add(newItem)
        mView?.addNewItems(arrayListOf(newItem))

        processedItemsCount++
        Log.d("12", "processedItemsCount = $processedItemsCount allCount = $rewritingItemsCount")
        if (processedItemsCount >= rewritingItemsCount) {
            mView?.setViewState(CONTENT_STATE.value)
            inLoadingProcess = false
        }
    }
}