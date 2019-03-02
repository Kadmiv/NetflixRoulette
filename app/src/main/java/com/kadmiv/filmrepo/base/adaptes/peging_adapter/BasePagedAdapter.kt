package com.kadmiv.filmrepo.base.adaptes.peging_adapter

import android.arch.paging.PagedListAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kadmiv.filmrepo.base.adaptes.BaseViewHolder

abstract class BasePagedAdapter<I, L>(
    diffUtilCallback: DiffUtil.ItemCallback<I>,
    private var listener: L
) :
    PagedListAdapter<I, BaseViewHolder<ViewDataBinding, L>>(diffUtilCallback) {

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
        viewHolder.bind(getItem(position))
    }

}