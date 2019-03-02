package com.kadmiv.filmrepo.base.interfaces

import android.arch.paging.PageKeyedDataSource

interface IPresenter<Key, Value> {

    fun <C : PageKeyedDataSource.LoadInitialCallback<Key, Value>> getFirsPageResult(initCallback: C)

    fun <C : PageKeyedDataSource.LoadCallback<Key, Value>> getNextPageResult(loadCallback: C)
}