package com.kadmiv.filmrepo.app.activity_item_details

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
import com.kadmiv.filmrepo.app.activity_item_details.adatper_pager.UltraPagerAdapter
import com.kadmiv.filmrepo.app.activity_item_details.adatper_pager.UltraPagerAdapter.*
import com.kadmiv.filmrepo.app.activity_main.*
import com.kadmiv.filmrepo.base.dialogs.BaseDialog
import com.kadmiv.filmrepo.databinding.ActivityItemDetailsBinding
import com.kadmiv.filmrepo.repo.InfoModel
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.utils.enums.ViewState
import com.kadmiv.filmrepo.utils.setSearchType
import com.tmall.ultraviewpager.UltraViewPager
import kotlinx.android.synthetic.main.activity_item_details.*
import java.lang.Exception

interface IView {
    fun closeActivity()
    fun saveItemPosition(position: Int)
    fun setViewState(viewState: Int)
    fun addNewItems(films: ArrayList<FilmModel>)
    fun changeItem(position: Int, newItem: FilmModel)
    fun putBindingModel(position: Int)
}

class ActivityItemDetails : AppCompatActivity(), IView {

    private var adapter: UltraPagerAdapter<FilmModel>? = null

    lateinit var binding: ActivityItemDetailsBinding
    lateinit var mValues: ArrayList<FilmModel>

    lateinit var mPresenter: PresenterDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_item_details
        )
        mPresenter = PresenterDetails.getInstance(this)

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

    override fun closeActivity() {
        material_menu_button.animateIconState(MaterialMenuDrawable.IconState.X)
        material_menu_button.postDelayed({ this.finish() }, 200)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(EXTRAS_RECYCLER_STATE,
                AdapterSaveInstance().apply {
                    data = mPresenter!!.oldList!!
                    currentPosition = mPresenter.currentItemPosition
                })
    }

    private fun initOtherComponents(savedInstanceState: Bundle?) {

        mValues = intent.getParcelableArrayListExtra<FilmModel>(EXTRAS_ITEMS)
        val currentItem = intent.getIntExtra(EXTRAS_ITEM_POSITION, 0)

        mPresenter.searchType = setSearchType(intent.getStringExtra(EXTRAS_SEARCH_TYPE))
        mPresenter.oldList = arrayListOf()
        mPresenter.oldList!!.addAll(mValues)

        binding.model = mValues[currentItem]
        binding.listener = mPresenter

        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        adapter = UltraPagerAdapter(mValues)
        ultraViewPager.adapter = adapter
        ultraViewPager.setOnPageChangeListener(mPresenter)

        if (savedInstanceState != null) {
            onRestoreState(savedInstanceState)
        } else {
            ultraViewPager.currentItem = currentItem
        }
    }

    private fun onRestoreState(savedInstanceState: Bundle?) {
        Log.d("12", "onRestoreInstanceState")
        if (savedInstanceState != null) {
            var pagerState =
                    savedInstanceState.getParcelable(EXTRAS_RECYCLER_STATE) as UltraPagerAdapter.AdapterSaveInstance
            ultraViewPager.currentItem = pagerState.currentPosition
            adapter?.mValues = pagerState.data as ArrayList<FilmModel>
            ultraViewPager.wrapAdapter.notifyDataSetChanged()
        }
    }

    override fun putBindingModel(position: Int) {
        try {
            binding.model = mValues[position]
        } catch (ex: Exception) {
            Log.d("12", "IndexOutOfBoundsException Position = $position")
            // Кастыль )
            binding.model = mValues[position - 1]
        }
    }

    // This function need for restoring position on ActivitySearch RecyclerView
    override fun saveItemPosition(position: Int) {
        val intent = Intent()
        intent.putExtra(EXTRAS_SELECTED_ITEM, position)
        setResult(AppCompatActivity.RESULT_OK, intent)
    }

    override fun setViewState(viewState: Int) {
        when (viewState) {
            ViewState.EMPTY_STATE.value -> {
                Log.d("12", "EMPTY_STATE")
                Toast.makeText(this, getString(R.string.not_find_for_request), Toast.LENGTH_SHORT).show()
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
                try {
                    BaseDialog(InfoModel(getString(R.string.error_text), R.drawable.ic_error)).show(supportFragmentManager, "")
                } catch (ex: Exception) {
                }
                showLoadingProcess(false)
            }
            ViewState.NETWORK_ERROR_STATE.value -> {
                Log.d("12", "NETWORK_ERROR_STATE")
                Toast.makeText(this, getString(R.string.connection_error_text), Toast.LENGTH_SHORT).show()
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

    override fun addNewItems(films: ArrayList<FilmModel>) {
        adapter!!.addNewData(films)
        ultraViewPager.wrapAdapter.notifyDataSetChanged()
    }

    override fun changeItem(position: Int, newItem: FilmModel) {
        adapter!!.changeItem(position, newItem)
    }

}
