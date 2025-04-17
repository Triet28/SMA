package com.mycompany.sma.data.remote

import com.mycompany.sma.data.model.FeedResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AdafruitApi {
    @GET("api/v2/{username}/feeds/{feedKey}/data?limit=1")
    fun getFeedData(
        @Path("username") username: String,
        @Path("feedKey") feedKey: String,
        @Header("X-AIO-Key") apiKey: String
    ): Call<List<FeedResponse>>
}
