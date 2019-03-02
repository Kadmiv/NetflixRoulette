package com.kadmiv.filmrepo.base.adaptes

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.android.databinding.library.baseAdapters.BR


open class BaseViewHolder<B : ViewDataBinding, L>(
        open val binding: B,
        open val listener: L) :
        RecyclerView.ViewHolder(binding.root) {

    open fun <I> bind(model: I) {
        binding.setVariable(BR.model, model)
        binding.setVariable(BR.listener, listener)
        binding.executePendingBindings()
    }
}