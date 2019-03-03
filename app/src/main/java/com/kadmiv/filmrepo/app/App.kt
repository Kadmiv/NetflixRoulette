package com.kadmiv.filmrepo.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.kadmiv.filmrepo.app.main.ActivityMain
import com.kadmiv.filmrepo.repo.db.AppDataBase

class App : MultiDexApplication() {
    override fun onCreate() {
        MultiDex.install(applicationContext)
        AppDataBase.getInstance(applicationContext)
        super.onCreate()
        App.hasConnection(applicationContext)
    }

    companion object {
        //Check internet connection
        private var context: Context? = null

        fun hasConnection(context: Context?): Boolean {
            if (this.context == null)
                if (context != null)
                    this.context = context
                else
                    return false
            val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetworkInfo: NetworkInfo? = null
            activeNetworkInfo = cm.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }
}
