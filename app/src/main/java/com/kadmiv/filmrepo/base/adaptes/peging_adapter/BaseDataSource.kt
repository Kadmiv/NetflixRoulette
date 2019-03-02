package com.kadmiv.filmrepo.base.adaptes.peging_adapter

import android.arch.paging.PageKeyedDataSource
import android.util.Log
import com.kadmiv.filmrepo.base.interfaces.IPresenter


class BaseDataSource<K, I>(val mPresenter: IPresenter<K, I>) : PageKeyedDataSource<K, I>() {
    override fun loadInitial(
        params: LoadInitialParams<K>,
        callback: LoadInitialCallback<K, I>
    ) {

        Log.d(
            "12",
            "requestedLoadSize = ${params.requestedLoadSize}"
        )
        mPresenter.getFirsPageResult(callback)
    }

    override fun loadAfter(
        params: LoadParams<K>,
        callback: LoadCallback<K, I>
    ) {
        Log.d(
            "12",
            "key = ${params.key}"
        )
        mPresenter.getNextPageResult(callback)
    }

    override fun loadBefore(
        params: LoadParams<K>,
        callback: LoadCallback<K, I>
    ) {

    }

}