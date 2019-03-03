package com.kadmiv.filmrepo.app.details.adatper_pager

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.kadmiv.filmrepo.R
import com.kadmiv.filmrepo.repo.db.models.BaseModel

class UltraPagerAdapter<I : BaseModel>(var mValues: ArrayList<I>) : PagerAdapter() {

    override fun getCount(): Int {
        if (mValues.isNotEmpty())
            return mValues.size
        return 0
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == any
    }

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any {

        Log.d("12", "Position in adapter $position")
        val layoutInflater = LayoutInflater.from(viewGroup.context)

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, R.layout.fragment_content,
                viewGroup, false
        )

        binding.setVariable(BR.model, mValues[position])
        viewGroup.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val view = any as View
        container.removeView(view)
    }

    fun addNewData(newData: ArrayList<I>) {
        mValues.addAll(newData)
    }

    fun changeItem(position: Int, newItem: I) {
        mValues[position] = newItem
    }

    class AdapterSaveInstance() : Parcelable {
        lateinit var data: ArrayList<*>
        var currentPosition: Int = 0

        constructor(parcel: Parcel) : this() {
            currentPosition = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(currentPosition)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AdapterSaveInstance> {
            override fun createFromParcel(parcel: Parcel): AdapterSaveInstance {
                return AdapterSaveInstance(parcel)
            }

            override fun newArray(size: Int): Array<AdapterSaveInstance?> {
                return arrayOfNulls(size)
            }
        }

    }
}