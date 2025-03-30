package com.mycompany.sma.network

import com.mycompany.sma.entity.FeedResponse
import com.mycompany.sma.entity.AdafruitResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.*

interface AdafruitApi {
    @Headers("X-AIO-Key: aio_BgZE85gcRV2UfhSfMqbjaVDhnBvv")
    @GET("api/v2/{username}/feeds/{feedKey}/data?limit=1")
    fun getFeedData(
        @Path("username") username: String,
        @Path("feedKey") feedKey: String
    ): Call<List<FeedResponse>>

    @Headers("X-AIO-Key: aio_BgZE85gcRV2UfhSfMqbjaVDhnBvv")
    @FormUrlEncoded
    @POST("api/v2/{username}/feeds/{feedKey}/data")
    fun sendDataToFeed(
        @Path("username") username: String,
        @Path("feedKey") feedKey: String,
        @Field("value") value: Int
    ): Call<AdafruitResponse>
}
