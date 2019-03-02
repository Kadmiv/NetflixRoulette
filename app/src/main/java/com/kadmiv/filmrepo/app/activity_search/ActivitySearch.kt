package com.kadmiv.filmrepo.app.activity_search

import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.balysv.materialmenu.MaterialMenuDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kadmiv.filmrepo.R
import com.kadmiv.filmrepo.app.activity_item_details.ActivityItemDetails
import com.kadmiv.filmrepo.app.activity_item_details.PresenterDetails
import com.kadmiv.filmrepo.app.activity_main.*
import com.kadmiv.filmrepo.base.adaptes.normal_adapter.BaseNormalAdapter
import com.kadmiv.filmrepo.base.dialogs.BaseDialog
import com.kadmiv.filmrepo.databinding.ActivitySearchBinding
import com.kadmiv.filmrepo.repo.InfoModel
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.utils.enums.SearchType
import com.kadmiv.filmrepo.utils.enums.SearchType.*
import com.kadmiv.filmrepo.utils.enums.ViewState
import com.kadmiv.filmrepo.utils.setSearchType
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_bar_activity_search.*
import kotlinx.android.synthetic.main.content_activity.*
import java.lang.Exception


interface IView {
    fun initRecyclerView() {}
    fun initRecyclerView(items: List<FilmModel>)
    fun showItemDetails(item: FilmModel)
    fun showSearchView(searchType: SearchType)
    fun showMainActivity()
    fun showDrawer()
    fun showSearchView()
    fun setViewState(viewState: Int)
    fun setSuggestions(suggestions: Array<String?>)
}

class ActivitySearch : AppCompatActivity(), IView {

    lateinit var mPresenter: PresenterSearch
    private var recyclerState: Parcelable? = null
    private var adapter: BaseNormalAdapter<FilmModel, PresenterSearch>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = PresenterSearch.getInstance(this)
        mPresenter.searchType = setSearchType(intent.getStringExtra(EXTRAS_SEARCH_TYPE))

        val binding = DataBindingUtil.setContentView<ActivitySearchBinding>(this, R.layout.activity_search)
        binding.listener = mPresenter

        initOtherComponents(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(EXTRAS_RECYCLER_STATE, itemRecycler.layoutManager?.onSaveInstanceState())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                val position = data.getIntExtra(EXTRAS_SELECTED_ITEM, 0)
                itemRecycler.scrollToPosition(position)
            }
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mPresenter.onDestroy()
            PresenterDetails.setInstanceNull()
            super.onBackPressed()
        }
    }

    private fun initOtherComponents(savedInstanceState: Bundle?) {
        when (mPresenter.searchType) {
            SEARCH_BY_TITLE -> searchView.setHint(resources.getString(R.string.search_by_title_hint))
            SEARCH_BY_DIRECTOR -> searchView.setHint(resources.getString(R.string.search_by_director_hint))
        }

        nav_view_part.setNavigationItemSelectedListener(mPresenter)

        searchView.setOnQueryTextListener(
            object : MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d("12", queryText)
                    mPresenter.onEnteredSearchQuery(queryText)
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (mPresenter.searchType == SEARCH_BY_DIRECTOR) {
                        mPresenter.onSearchQueryChange(newText)
                    }
                    return true
                }
            })
        onRestoreState(savedInstanceState)
    }

    private fun onRestoreState(savedInstanceState: Bundle?) {
        Log.d("12", "onRestoreInstanceState")
        if (savedInstanceState != null) {
            recyclerState = savedInstanceState.getParcelable(EXTRAS_RECYCLER_STATE)
        }
    }


    override fun initRecyclerView(items: List<FilmModel>) {
        var recyclerView = itemRecycler
        var data = arrayListOf<FilmModel>()
        data.addAll(items)

        recyclerView.visibility = View.VISIBLE
        if (data.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            return
        }

        // Check adapter
        if (adapter == null) {
            adapter = BaseNormalAdapter(mPresenter!!, R.layout.item_film)
            adapter?.mValues?.addAll(data)
            recyclerView.adapter = adapter
        } else {
            adapter?.onNewData(data)
        }

        if (recyclerView.layoutManager == null) {
            var spanCount = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> LANDSCAPE_COLUMN_COUNT
                else -> PORTRAIT_COLUMN_COUNT
            }

            val mLayoutManager = GridLayoutManager(this, spanCount)
            recyclerView.layoutManager = mLayoutManager

            if (recyclerState != null)
                recyclerView.layoutManager!!.onRestoreInstanceState(recyclerState)
        }


        recyclerView.clearOnScrollListeners()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                if (firstVisible == RecyclerView.NO_POSITION) {
                    return;
                }

                mPresenter.isNeedLoadNewData(firstVisible)
                Log.d("12", "Recycler position $firstVisible")
            }
        })
    }

    override fun showDrawer() {
        material_menu_button.animateIconState(MaterialMenuDrawable.IconState.X)
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(p0: Int) {}
            override fun onDrawerSlide(p0: View, p1: Float) {}

            override fun onDrawerClosed(p0: View) {
                material_menu_button.animateIconState(MaterialMenuDrawable.IconState.BURGER)
            }

            override fun onDrawerOpened(p0: View) {
                material_menu_button.animateIconState(MaterialMenuDrawable.IconState.X)
            }
        })
        drawerLayout.postDelayed({
            drawerLayout.openDrawer(GravityCompat.START)
        }, 200)
    }

    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun showMainActivity() {
        val intent = Intent(this, ActivityMain::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        closeDrawer()
    }

    override fun showSearchView(searchType: SearchType) {
        val intent = Intent(this, ActivitySearch::class.java)
        intent.putExtra(EXTRAS_SEARCH_TYPE, searchType.name)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        closeDrawer()
    }

    override fun showItemDetails(item: FilmModel) {
        val intent = Intent(this, ActivityItemDetails::class.java)
        intent.putExtra(EXTRAS_ITEMS, adapter?.mValues)
        intent.putExtra(EXTRAS_ITEM_POSITION, getItemPosition(item))
        intent.putExtra(EXTRAS_SEARCH_TYPE, mPresenter.searchType.name)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun getItemPosition(item: FilmModel): Int {
        for (position in 0 until adapter!!.mValues.size)
            if (item == adapter!!.mValues[position])
                return position
        return 0
    }

    override fun showSearchView() {
        searchView.showSearch()
    }

    override fun setSuggestions(suggestions: Array<String?>) {
        searchView.setSuggestions(suggestions)
        searchView.setSuggestionIcon(resources.getDrawable(R.drawable.ic_suggestion_key))
    }

    override fun setViewState(viewState: Int) {
        when (viewState) {
            ViewState.EMPTY_STATE.value -> {
                Log.d("12", "EMPTY_STATE")
                BaseDialog(InfoModel(getString(R.string.not_find_for_request), R.raw.not_found)).show(
                    supportFragmentManager,
                    ""
                )
                showLoadingProcess(false)
            }
            ViewState.CONTENT_STATE.value -> {
                Log.d("12", "CONTENT_STATE")
                showLoadingProcess(false)
            }
            ViewState.LOADING_STATE.value -> {
                Log.d("12", "LOADING_STATE")
                showLoadingProcess(true)
            }
            ViewState.ERROR_STATE.value -> {
                Log.d("12", "ERROR_STATE")
//                BaseDialog(InfoModel(getString(R.string.error_text), R.drawable.ic_error)).show(supportFragmentManager, "")
                showLoadingProcess(false)
            }
            ViewState.NETWORK_ERROR_STATE.value -> {
                Log.d("12", "NETWORK_ERROR_STATE")
                try {
                    BaseDialog(InfoModel(getString(R.string.connection_error_text), R.raw.connection_error)).show(
                        supportFragmentManager,
                        ""
                    )
                } catch (ex: Exception) {
                }
                showLoadingProcess(false)
            }
        }
    }

    private fun showLoadingProcess(needShow: Boolean) {
        Log.d("12", "Load animation is $needShow")
        if (needShow) {
            loadingProgress.visibility = View.VISIBLE

            val options = RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.DATA)

            Glide.with(loadingProgress)
                .load(R.raw.waiting_icon)
                .apply(options)
                .into(loadingProgress);
        } else {
            loadingProgress.visibility = View.GONE
        }
    }

}
