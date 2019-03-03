package com.kadmiv.filmrepo.app.details

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.balysv.materialmenu.MaterialMenuDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kadmiv.filmrepo.R
import com.kadmiv.filmrepo.app.App
import com.kadmiv.filmrepo.app.details.adatper_pager.UltraPagerAdapter
import com.kadmiv.filmrepo.app.main.*
import com.kadmiv.filmrepo.databinding.ActivityItemDetailsBinding
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.utils.enums.ViewState
import com.kadmiv.filmrepo.utils.setSearchType
import com.tmall.ultraviewpager.UltraViewPager
import kotlinx.android.synthetic.main.activity_item_details.*

interface IView {
    fun closeActivity(
        dataList: java.util.ArrayList<FilmModel>,
        itemPosition: Int
    )

    fun setViewState(viewState: Int)
    fun addNewItems(films: ArrayList<FilmModel>)
    fun changeItem(position: Int, newItem: FilmModel)
    fun putBindingModel(item: FilmModel)
    fun hasConnection(): Boolean
}

class ActivityItemDetails : AppCompatActivity(), IView {
    override fun hasConnection(): Boolean {
        return App.hasConnection(this)
    }

    private var adapter: UltraPagerAdapter<FilmModel>? = null

    lateinit var binding: ActivityItemDetailsBinding

    lateinit var mPresenter: PresenterDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_item_details
        )
        mPresenter = PresenterDetails(this)

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

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

    override fun onBackPressed() {
        mPresenter.onBackButtonPressed()
    }

    override fun closeActivity(
        dataList: java.util.ArrayList<FilmModel>,
        itemPosition: Int
    ) {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRAS_ITEMS, dataList)
        resultIntent.putExtra(EXTRAS_ITEM_POSITION, itemPosition)
        setResult(Activity.RESULT_OK, resultIntent)
        material_menu_button.animateIconState(MaterialMenuDrawable.IconState.X)
        material_menu_button.postDelayed({ this.finish() }, 200)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArrayList(EXTRAS_ITEMS, mPresenter.oldList)
        outState?.putInt(EXTRAS_ITEM_POSITION, mPresenter.currentItemPosition)
        outState?.putString(EXTRAS_SEARCH_TYPE, mPresenter.searchType.name)
    }

    private fun initOtherComponents(savedState: Bundle?) {
        Log.d("12", "initOtherComponents")

        val data: ArrayList<FilmModel>
        val position: Int
        var searchType = ""

        if (savedState != null) {
            data = savedState.getParcelableArrayList<FilmModel>(EXTRAS_ITEMS)
            position = savedState.getInt(EXTRAS_ITEM_POSITION, 0)
            searchType = savedState.getString(EXTRAS_SEARCH_TYPE, "")
        } else {
            data = intent.getParcelableArrayListExtra<FilmModel>(EXTRAS_ITEMS)
            position = intent.getIntExtra(EXTRAS_ITEM_POSITION, 0)
            val type = intent.getStringExtra(EXTRAS_SEARCH_TYPE)
            if (type != null)
                searchType = type
        }

        mPresenter.oldList.addAll(data)
        mPresenter.searchType = setSearchType(searchType)

        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        adapter = UltraPagerAdapter(data)
        ultraViewPager.adapter = adapter
        ultraViewPager.setOnPageChangeListener(mPresenter)
        ultraViewPager.currentItem = position

        binding.model = data[position]
        binding.listener = mPresenter
    }

    override fun putBindingModel(item: FilmModel) {
        binding.model = item
    }

    override fun setViewState(viewState: Int) {
        showLoadingProcess(false)
        when (viewState) {
            ViewState.EMPTY_STATE.value -> {
                Log.d("12", "EMPTY_STATE")
                Toast.makeText(this, getString(R.string.not_find_for_request), Toast.LENGTH_SHORT).show()
            }
            ViewState.CONTENT_STATE.value -> {
                Log.d("12", "CONTENT_STATE")
            }
            ViewState.LOADING_STATE.value -> {
                Log.d("12", "LOADING_STATE")
                showLoadingProcess(true)
            }
            ViewState.ERROR_STATE.value -> {
                Log.d("12", "ERROR_STATE")
//                try {
//                    BaseDialog(InfoModel(getString(R.string.error_text), R.drawable.ic_error)).show(supportFragmentManager, "")
//                } catch (ex: Exception) {
//                }
            }
            ViewState.NETWORK_ERROR_STATE.value -> {
                Log.d("12", "NETWORK_ERROR_STATE")
                Toast.makeText(this, getString(R.string.connection_error_text), Toast.LENGTH_SHORT).show()
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

    override fun addNewItems(films: ArrayList<FilmModel>) {
        adapter!!.addNewData(films)
        ultraViewPager.wrapAdapter.notifyDataSetChanged()
    }

    override fun changeItem(position: Int, newItem: FilmModel) {
        adapter!!.changeItem(position, newItem)
    }

}
