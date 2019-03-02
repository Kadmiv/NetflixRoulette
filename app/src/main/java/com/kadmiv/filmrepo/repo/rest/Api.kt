package com.kadmiv.filmrepo.repo.rest

import com.kadmiv.filmrepo.repo.rest.models.more_info.MoreInfo
import com.kadmiv.filmrepo.repo.rest.models.person_movie_model.FilmsByPerson
import com.kadmiv.filmrepo.repo.rest.models.title_movie_model.FilmsByTitle
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


const val BASE_URL = "https://api.themoviedb.org/3/"
const val KEY_API = "api_key"
const val REGION =
        "region" // Specify a ISO 3166-1 code to filter release dates. Must be uppercase. pattern: ^[A-Z]{2}$ Optional
const val QUERY = "query"
const val LANGUAGE = "language" // default en-US
const val PAGE = "page"

interface Api {

    @GET("search/movie")
    fun findByTitle(
            @Query(QUERY) query: String,
            @Query(PAGE) page: Int,
            @Query(KEY_API) apiKey: String
    ): Call<FilmsByTitle>

    @GET("movie/{id}")
    fun findById(
            @Path("id") id: Long,
            @Query(KEY_API) apiKey: String
    ): Call<MoreInfo>

    @GET("search/person")
    fun findByPerson(
            @Query(QUERY) query: String,
            @Query(PAGE) page: Int,
            @Query(KEY_API) apiKey: String
    ): Call<FilmsByPerson>

    companion object {

        private var instance: Api? = null

        fun getInstance(): Api {
            if (instance == null) {
                val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                instance = retrofit.create(Api::class.java!!)
            }

            return instance!!
        }
    }
}