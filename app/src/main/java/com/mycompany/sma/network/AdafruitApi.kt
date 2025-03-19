package com.mycompany.sma.network

import com.mycompany.sma.entity.FeedResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface AdafruitApi {
    @Headers("X-AIO-Key: aio_BgZE85gcRV2UfhSfMqbjaVDhnBvv")
    @GET("api/v2/{username}/feeds/{feedKey}/data?limit=1")
    fun getFeedData(
        @Path("username") username: String,
        @Path("feedKey") feedKey: String
    ): Call<List<FeedResponse>>
}
