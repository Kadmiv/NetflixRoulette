package com.kadmiv.filmrepo.base.adaptes.normal_adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kadmiv.filmrepo.base.adaptes.BaseViewHolder
import com.kadmiv.filmrepo.repo.db.models.BaseModel

class BaseNormalAdapter<I : BaseModel, L>(
        private var listener: L,
        private val viewType: Int
) :
        RecyclerView.Adapter<BaseViewHolder<ViewDataBinding, L>>() {

    var mValues: ArrayList<I> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int):
            BaseViewHolder<ViewDataBinding, L> {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding: ViewDataBinding?

        binding = DataBindingUtil.inflate(
                layoutInflater, viewType,
                viewGroup, false
        )

        return BaseViewHolder(binding, listener)
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder<ViewDataBinding, L>, position: Int) {

        viewHolder.bind(mValues[position])
    }

    override fun getItemCount(): Int {
        if (mValues.isNotEmpty())
            return mValues.size
        return 0
    }

    fun onNewData(newData: ArrayList<I>) {
        val diffResult = DiffUtil.calculateDiff(BaseDiffUtilCallback(mValues, newData))
        this.mValues.clear()
        this.mValues.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

}