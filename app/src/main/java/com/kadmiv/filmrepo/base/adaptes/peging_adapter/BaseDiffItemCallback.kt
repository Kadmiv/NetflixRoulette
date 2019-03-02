package com.kadmiv.filmrepo.base.adaptes.peging_adapter

import android.support.v7.util.DiffUtil
import com.kadmiv.filmrepo.repo.db.models.BaseItem


class BaseDiffItemCallback<I : BaseItem>() : DiffUtil.ItemCallback<I>() {
    override fun areItemsTheSame(
        oldItem: I,
        newItem: I
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: I,
        newItem: I
    ): Boolean {
        return oldItem == newItem
    }
}