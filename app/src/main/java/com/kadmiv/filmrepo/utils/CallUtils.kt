package com.kadmiv.filmrepo.utils

import com.kadmiv.filmrepo.repo.rest.CallbackWithRetry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object CallUtils {

    fun <T> enqueueWithRetry(call: Call<T>, callback: Callback<T>) {
        call.enqueue(object : CallbackWithRetry<T>(call) {
            override fun onFailure(call: Call<T>, t: Throwable) {
                super.onFailure(t)
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(call, response)
            }
        })
    }

}