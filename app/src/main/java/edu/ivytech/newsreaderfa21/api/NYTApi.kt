package edu.ivytech.newsreaderfa21.api

import retrofit2.Call
import retrofit2.http.GET

interface NYTApi {

    @GET("movies.json?api-key=AWjPeE3hqlzvrQxyhaA9il60sTG2ofX8")
    fun downloadArticles() : Call<NYTResponse>
}