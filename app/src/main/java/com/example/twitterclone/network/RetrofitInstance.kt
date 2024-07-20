package com.example.twitterclone.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: TweetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TweetApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TweetApiService::class.java)
    }
}