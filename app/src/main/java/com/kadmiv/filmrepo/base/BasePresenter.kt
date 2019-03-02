package com.kadmiv.filmrepo.base

abstract class BasePresenter {

    abstract fun onStart()
    open fun onPause() {}
    open fun onRestart() {}
    abstract fun onStop()
    open fun onSaveInstanceState() {}
    abstract fun onDestroy()

}