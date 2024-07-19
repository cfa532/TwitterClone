package com.example.twitterclone.network

import com.example.twitterclone.model.TweetApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.yourservice.com/"

    val api: TweetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TweetApiService::class.java)
    }
}