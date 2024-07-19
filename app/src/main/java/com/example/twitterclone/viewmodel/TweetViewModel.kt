package com.example.twitterclone.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.model.MimeiId
import com.example.twitterclone.model.Tweet
import com.example.twitterclone.model.TweetApiService
import com.example.twitterclone.model.TweetRequest
import com.example.twitterclone.network.RetrofitInstance
import com.example.twitterclone.repository.TweetRepository
import kotlinx.coroutines.launch
import java.time.Instant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.awaitResponse

class TweetViewModel(
    private val tweetRepository: TweetRepository = TweetRepository()
) : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://your-api-base-url.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tweetApiService = retrofit.create(TweetApiService::class.java)

    private val tweets = mutableStateListOf<Tweet>()

    fun likeTweet(tweetMid: MimeiId) {
        viewModelScope.launch {
            val tweet = tweetRepository.getTweet(tweetMid)
            val updatedTweet = tweet.copy(likeCount = tweet.likeCount + 1)
            tweetRepository.updateTweet(updatedTweet)
        }
    }

    fun retweet(tweetMid: MimeiId, authorMid: MimeiId) {
        viewModelScope.launch {
            val originalTweet = tweetRepository.getTweet(tweetMid)
            val retweet = Tweet(
                content = originalTweet.content,
                timestamp = System.currentTimeMillis(),
                author = authorMid,
                original = originalTweet.mid
            )
            tweetRepository.addTweet(retweet)   // update tweet with Id returned from server
        }
    }

    private fun saveTweetToDatabase(tweet: Tweet) {
        // Implement your proprietary database access logic here
    }

    fun composeTweet(authorId: MimeiId, content: String, isPrivate: Boolean) {
        viewModelScope.launch {
            val tweetRequest = TweetRequest(Tweet(author = authorId, content = content))
            val response = RetrofitInstance.api.uploadTweet(tweetRequest).awaitResponse()
            if (response.isSuccessful) {
                val tweetResponse = response.body()
                tweetResponse?.let {
                    val newTweet = Tweet(
                        mid = it.mimeiId,
                        content = content,
                        timestamp = System.currentTimeMillis(),
                        author = authorId,
                        isPrivate = isPrivate
                    )
                    tweets.add(newTweet)
                }
            } else {
                // Handle error
            }
        }
    }
}