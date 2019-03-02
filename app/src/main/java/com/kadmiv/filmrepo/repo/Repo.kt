package com.kadmiv.filmrepo.repo

import android.util.Log
import com.kadmiv.filmrepo.base.interfaces.RepoListener
import com.kadmiv.filmrepo.repo.db.AppDataBase
import com.kadmiv.filmrepo.repo.db.models.FilmModel
import com.kadmiv.filmrepo.repo.rest.Api
import com.kadmiv.filmrepo.repo.rest.models.more_info.MoreInfo
import com.kadmiv.filmrepo.repo.rest.models.person_movie_model.FilmsByPerson
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


val API_KEY = "b15680264e2147ac36c5a1ba06f94b7d"

object Repo {

    private val listeners = ArrayList<RepoListener>()

    private var dataBase = AppDataBase.getInstance(null).dao()
    private var api = Api.getInstance()

    var currentQuery: String = ""
    var currentPage = 1
    var maxPage = 1

    fun addListener(listener: RepoListener) {
        if (!listeners.contains(listener))
            listeners.add(listener)
    }

    fun removeListener(listener: RepoListener) {
        listeners.remove(listener)
    }

    fun addItem(item: FilmModel) {
        // For just case in another thread
        Observable.just(item)
            .map(object : Function<FilmModel, String> {
                override fun apply(item: FilmModel): String {
                    dataBase.insert(item)
                    return "DONE"
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                listeners.forEach { listener -> listener.onInsertSuccess() }
            }

    }

    fun deleteItem(item: FilmModel) {
        // For just case in another thread
        Observable.just(item)
            .map(object : Function<FilmModel, String> {
                override fun apply(item: FilmModel): String {
                    dataBase.delete(item)
                    return "DONE"
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _ ->
                listeners.forEach { listener -> listener.onDeletingSuccess() }
            }

    }

    fun getFavorites() {
        listeners.forEach { listener -> listener.onStartLoading() }
        Observable.just("")
            .map(object : Function<String, List<FilmModel>> {
                override fun apply(t: String): List<FilmModel> {
                    return dataBase.getAll()
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { items -> listeners.forEach { listener -> listener.onReceivingSaved(items) } }

    }

    fun findFilmByTitle(query: String) {
        currentQuery = query
        currentPage = 1
        listeners.forEach { listener -> listener.onStartLoading() }
        api.findByTitle(query, currentPage, API_KEY).enqueue(object : Callback<FilmsByTitle> {
            override fun onFailure(
                call: Call<FilmsByTitle>,
                t: Throwable
            ) {
                Log.d("12", "Error " + t.stackTrace.toString())
                listeners.forEach { listener -> listener.onConnectionError() }
            }

            override fun onResponse(
                call: Call<FilmsByTitle>,
                response: Response<FilmsByTitle>
            ) {
                if (response.isSuccessful) {
                    maxPage = response.body()!!.total_pages
                    listeners.forEach { listener -> listener.onReceivingFindByTitleResults(response.body()!!) }
                    Log.d("12", "Current page =$currentPage Max pages =$maxPage")
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()}")
                listeners.forEach { listener -> listener.onResponseError(response.errorBody().toString()) }
            }
        })
    }

    fun getNextPageWithFilmByTitle() {
        currentPage++

        if (currentPage > maxPage)
            return
        listeners.forEach { listener -> listener.onStartLoading() }
        api.findByTitle(currentQuery, currentPage, API_KEY).enqueue(object : Callback<FilmsByTitle> {
            override fun onFailure(
                call: Call<FilmsByTitle>,
                t: Throwable
            ) {
                Log.d("12", "Error ${t.stackTrace} getNextPageWithFilmByTitle")
                listeners.forEach { listener -> listener.onConnectionError() }
            }

            override fun onResponse(
                call: Call<FilmsByTitle>,
                response: Response<FilmsByTitle>
            ) {
                if (response.isSuccessful) {
                    listeners.forEach { listener -> listener.onReceivingFindByTitleResults(response.body()!!) }
                    Log.d("12", " Current page =$currentPage Max pages =$maxPage getNextPageWithFilmByTitle")
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()} getNextPageWithFilmByTitle")
                listeners.forEach { listener -> listener.onResponseError(response.errorBody().toString()) }
            }
        })
    }

    fun getMoreInfo(id: Long) {
        api.findById(id, API_KEY).enqueue(object : Callback<MoreInfo> {
            override fun onFailure(
                call: Call<MoreInfo>,
                t: Throwable
            ) {
                Log.d("12", "Error ${t.stackTrace} getMoreInfo")
                listeners.forEach { listener -> listener.onConnectionError() }
            }

            override fun onResponse(
                call: Call<MoreInfo>,
                response: Response<MoreInfo>
            ) {
                if (response.isSuccessful) {
                    listeners.forEach { listener -> listener.onReceivingMoreDetailsResults(response.body()!!) }
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()} getMoreInfo")
                listeners.forEach { listener -> listener.onResponseError(response.errorBody().toString()) }
            }
        })
    }

    fun findFilmByPerson(query: String) {
        currentQuery = query
        currentPage = 1
        listeners.forEach { listener -> listener.onStartLoading() }
        api.findByPerson(query, currentPage, API_KEY).enqueue(object : Callback<FilmsByPerson> {
            override fun onFailure(
                call: Call<FilmsByPerson>,
                t: Throwable
            ) {
                Log.d("12", "Error ${t.stackTrace} findFilmByPerson")
                listeners.forEach { listener -> listener.onConnectionError() }

            }

            override fun onResponse(
                call: Call<FilmsByPerson>,
                response: Response<FilmsByPerson>
            ) {
                if (response.isSuccessful) {
                    maxPage = response.body()!!.total_pages
                    listeners.forEach { listener -> listener.onReceivingFindByPersonResults(response.body()!!) }
                    Log.d("12", "Current page =$currentPage Max pages =$maxPage findFilmByPerson")
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()} findFilmByPerson")
                listeners.forEach { listener -> listener.onResponseError(response.errorBody().toString()) }
            }
        })
    }

    fun getNextPageWithFilmByPerson() {
        currentPage++

        if (currentPage > maxPage)
            return

        listeners.forEach { listener -> listener.onStartLoading() }
        api.findByPerson(currentQuery, currentPage, API_KEY).enqueue(object : Callback<FilmsByPerson> {
            override fun onFailure(
                call: Call<FilmsByPerson>,
                t: Throwable
            ) {
                Log.d("12", "Error ${t.stackTrace} getNextPageWithFilmByPerson")
                listeners.forEach { listener -> listener.onConnectionError() }

            }

            override fun onResponse(
                call: Call<FilmsByPerson>,
                response: Response<FilmsByPerson>
            ) {
                if (response.isSuccessful) {
                    listeners.forEach { listener -> listener.onReceivingFindByPersonResults(response.body()!!) }
                    Log.d("12", "Current page =$currentPage Max pages =$maxPage getNextPageWithFilmByPerson")
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()} getNextPageWithFilmByPerson")
                listeners.forEach { listener -> listener.onResponseError(response.errorBody().toString()) }
            }
        })
    }

    fun getSuggestionsByTitle(newText: String) {
        api.findByTitle(newText, 1, API_KEY).enqueue(object : Callback<FilmsByTitle> {
            override fun onFailure(
                call: Call<FilmsByTitle>,
                t: Throwable
            ) {
                Log.d("12", "Error " + t.stackTrace.toString())
                listeners.forEach { listener -> listener.onConnectionError() }
            }

            override fun onResponse(
                call: Call<FilmsByTitle>,
                response: Response<FilmsByTitle>
            ) {
                if (response.isSuccessful) {
                    maxPage = response.body()!!.total_pages
                    listeners.forEach { listener -> listener.onReceivingFindByTitleSuggestions(response.body()!!) }
                    Log.d("12", "Current page =$currentPage Max pages =$maxPage getSuggestionsByTitle")
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()} getSuggestionsByTitle")
            }
        })
    }

    fun getSuggestionsByPerson(newText: String) {
        api.findByPerson(newText, 1, API_KEY).enqueue(object : Callback<FilmsByPerson> {
            override fun onFailure(
                call: Call<FilmsByPerson>,
                t: Throwable
            ) {
                Log.d("12", "Error ${t.stackTrace} getSuggestionsByPerson")
                listeners.forEach { listener -> listener.onConnectionError() }

            }

            override fun onResponse(
                call: Call<FilmsByPerson>,
                response: Response<FilmsByPerson>
            ) {
                if (response.isSuccessful) {
                    maxPage = response.body()!!.total_pages
                    listeners.forEach { listener -> listener.onReceivingFindByPersonSuggestions(response.body()!!) }
                    Log.d("12", "Current page =$currentPage Max pages =$maxPage getSuggestionsByPerson")
                    return
                }
                Log.d("12", "OnErrorResponse ${response.errorBody().toString()} getSuggestionsByPerson")
            }
        })
    }
}