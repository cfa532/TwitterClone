package com.example.twitterclone.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class TweetRequest(val tweet: Tweet)
data class TweetResponse(val mimeiId: MimeiId)

interface TweetApiService {
    @POST("tweets")
    fun uploadTweet(@Body tweetRequest: TweetRequest): Call<TweetResponse>
}