package com.kadmiv.filmrepo.app

import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.kadmiv.filmrepo.repo.db.AppDataBase

class App : MultiDexApplication() {
    override fun onCreate() {
        MultiDex.install(applicationContext)
        AppDataBase.getInstance(applicationContext)
        super.onCreate()
    }
}