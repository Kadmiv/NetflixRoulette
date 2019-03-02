package com.kadmiv.filmrepo.app.activity_main

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
import com.kadmiv.filmrepo.utils.enums.SearchType.*

class PresenterMain(var mView: IView?) : BasePresenter(),
    RepoListener,
    ItemListener<FilmModel>,
    NavigationView.OnNavigationItemSelectedListener,
    AppBarListener<FilmModel> {

    var mRepo: Repo? = null

    override fun onStart() {
        mRepo = Repo
        mRepo?.addListener(this)
        mRepo?.getFavorites()
//        mRepo.getDBSample()
    }


    override fun onStop() {
        mRepo?.removeListener(this)
    }


    override fun onDestroy() {
        mView = null
        mRepo = null
    }

    override fun onReceivingSaved(items: List<FilmModel>) {
        mView?.initRecyclerView(items)
    }

    override fun onItemClicked(item: FilmModel) {
        mView?.showItemDetails(item)
    }

    override fun onToolbarButtonClicked() {
        mView?.showDrawer()
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.nav_saved -> {
                Log.d("12", "nav_saved")
                mView?.closeDrawer()
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

    companion object {
        private var instance: PresenterMain? = null
        fun getInstance(mView: IView?): PresenterMain {
            if (instance == null)
                instance = PresenterMain(mView!!)
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