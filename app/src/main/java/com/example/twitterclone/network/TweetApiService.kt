package com.example.twitterclone.network

import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class TweetRequest(val tweet: Tweet)
data class TweetResponse(val mimeiId: MimeiId)

interface TweetApiService {
    @POST("tweets")
    fun uploadTweet(@Body tweetRequest: TweetRequest): Call<TweetResponse>

    companion object {
        const val BASE_URL = "http://localhost:8081/webapi"
//        val lapi: HproseClient = HproseClient.create(BASE_URL)
    }
}