package com.DADN.smartplantcare

import retrofit2.Call
import retrofit2.http.*


// 🆕 Thêm vào đây
data class FeedRequest(val value: Int)
data class AdafruitResponse(val id: String, val value: String)

interface AdafruitApi {

    @Headers("X-AIO-Key: aio_BgZE85gcRV2UfhSfMqbjaVDhnBvv")
    @GET("api/v2/{username}/feeds/{feedKey}/data?limit=1")
    fun getFeedData(
        @Path("username") username: String,
        @Path("feedKey") feedKey: String
    ): Call<List<FeedResponse>>

    // 🆕 Gửi dữ liệu lên feed
    @Headers(
        "Content-Type: application/json",
        "X-AIO-Key: aio_BgZE85gcRV2UfhSfMqbjaVDhnBvv"
    )
    @POST("api/v2/{username}/feeds/{feedKey}/data")
    fun sendDataToFeed(
        @Path("username") username: String,
        @Path("feedKey") feedKey: String,
        @Body data: FeedRequest
    ): Call<AdafruitResponse>
}
