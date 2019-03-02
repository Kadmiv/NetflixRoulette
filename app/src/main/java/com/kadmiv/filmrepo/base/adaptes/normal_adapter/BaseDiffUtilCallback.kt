package com.kadmiv.filmrepo.base.adaptes.normal_adapter

import android.support.v7.util.DiffUtil
import com.kadmiv.filmrepo.repo.db.models.BaseItem

class BaseDiffUtilCallback<I : BaseItem>(val oldList: List<I>, val newList: List<I>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct.id == newProduct.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldProduct = oldList[oldItemPosition]
        val newProduct = newList[newItemPosition]
        return oldProduct == newProduct
    }

}