package com.kadmiv.filmrepo.app.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.balysv.materialmenu.MaterialMenuDrawable
import com.kadmiv.filmrepo.R
import com.kadmiv.filmrepo.app.details.ActivityItemDetails
import com.kadmiv.filmrepo.app.search.ActivitySearch
import com.kadmiv.filmrepo.base.adaptes.normal_adapter.BaseNormalAdapter
import com.kadmiv.filmrepo.databinding.ActivityMainBinding
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_activity_main.*
import kotlinx.android.synthetic.main.content_activity.*
import com.kadmiv.filmrepo.utils.enums.SearchType


interface IView {
    fun initRecyclerView(items: List<FilmModel>)
    fun showItemDetails(item: FilmModel)
    fun showSearchView(searchType: SearchType)
    fun showDrawer()
    fun closeDrawer()
}

const val LANDSCAPE_COLUMN_COUNT = 2
const val PORTRAIT_COLUMN_COUNT = 1
const val REQUEST_CODE = 937

const val EXTRAS_RECYCLER_STATE = "EXTRAS_RECYCLER_STATE"
const val EXTRAS_ITEMS = "EXTRAS_ITEMS"
const val EXTRAS_ITEM_POSITION = "EXTRAS_ITEM_POSITION"
const val EXTRAS_SELECTED_ITEM = "EXTRAS_SELECTED_ITEM"
const val EXTRAS_SEARCH_TYPE = "EXTRAS_SEARCH_TYPE"

class ActivityMain : AppCompatActivity(), IView {

    lateinit var mPresenter: PresenterMain
    private var recyclerState: Parcelable? = null
    private var adapter: BaseNormalAdapter<FilmModel, PresenterMain>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = PresenterMain(this)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
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

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initOtherComponents(savedState: Bundle?) {
        nav_view_part.setNavigationItemSelectedListener(mPresenter)

        Log.d("12", "onRestoreInstanceState")
        if (savedState != null) {
            recyclerState = savedState.getParcelable(EXTRAS_RECYCLER_STATE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                val position = data.getIntExtra(EXTRAS_ITEM_POSITION, 0)
                val dataList = data.getParcelableArrayListExtra<FilmModel>(EXTRAS_ITEMS)
                adapter?.onNewData(dataList)
                itemRecycler.scrollToPosition(position)
            }
        }
    }

    override fun initRecyclerView(items: List<FilmModel>) {
        val recyclerView = itemRecycler
        val data = arrayListOf<FilmModel>()
        data.addAll(items)

        //Adapter
        adapter = BaseNormalAdapter(mPresenter, R.layout.item_film)
        adapter?.mValues?.addAll(data)
        recyclerView.adapter = adapter

        //Manager
        val columns = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> LANDSCAPE_COLUMN_COUNT
            else -> PORTRAIT_COLUMN_COUNT
        }

        val lManager = GridLayoutManager(this, columns)
        recyclerView.layoutManager = lManager

        // Restore instance
        if (recyclerState != null)
            recyclerView.layoutManager!!.onRestoreInstanceState(recyclerState)

    }

    override fun showItemDetails(item: FilmModel) {
        val intent = Intent(this, ActivityItemDetails::class.java)
        intent.putExtra(EXTRAS_ITEMS, adapter?.mValues)
        intent.putExtra(EXTRAS_ITEM_POSITION, getItemPosition(item))
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun getItemPosition(item: FilmModel): Int {
        for (position in 0 until adapter!!.mValues.size)
            if (item == adapter!!.mValues[position])
                return position
        return 0
    }

    override fun showSearchView(searchType: SearchType) {
        val intent = Intent(this, ActivitySearch::class.java)
        intent.putExtra(EXTRAS_SEARCH_TYPE, searchType.name)
        startActivity(intent)
        closeDrawer()
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

    override fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
}
